package com.folks.app.bo;

import com.folks.app.util.Validator;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import com.folks.app.auth.AppUser;
import com.folks.app.model.User;
import com.folks.app.util.IdGenerator;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.vertx.container.ResourceAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class UserBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBO.class);

    public UserBO() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized UserBO: {}. UserDAO: {}", getClass().getSimpleName(), userDAO);
        }
    }

    public User create(AppUser usr, User user) throws IllegalAccessException {
        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        validateScope(usr, "user:create");
        Validator.validateUser(user);

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        Map<String, List<String>> map = new HashMap<>();
        map.put("operator", List.of("OR"));
        map.put("phone1", List.of(user.getPhone1()));
        map.put("email", List.of(user.getEmail()));
        
        SearchCriteria search = SearchCriteria.from(new QueryParams(map));
        List<User> users = userDAO.query(search);
        if (! users.isEmpty()) {
            LOGGER.warn("User for {} or {} already exists. Skipping user creation ...", user.getPhone1(), user.getEmail());
            throw new ResourceAlreadyExistsException("User for " + user.getPhone1() + " or " + user.getEmail() + " already exists");
        }
        // User does not exist. Proceed to create the user ...
        user.setExternalId(IdGenerator.generate(user.getPhone1(), user.getEmail()));
        user.setRole(user.getRole() != null ? user.getRole() : User.Role.CUSTOMER);
        user.setStatus(User.Status.ACTIVE);
        user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        
        userDAO.insert(user);
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return user;
    }

    public void create(AppUser usr, List<User> records) throws IllegalAccessException {
        // Only admin has the privilege to create user in bulk.
        ensureAdmin(usr);
        validateScope(usr, "user:create");
        for (User user : records)
            Validator.validateUser(user);

        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (User user : records) {
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        userDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} User record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public User modify(AppUser usr, User user) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in user is allowed to modify the user as identified by this id.
        ensureAuthorized(usr, user.getExternalId());
        Validator.validateUser(user);
        // Fetch the user entry.
        User existing = fetchUser(usr);

        // Update attributes of existing record
        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setPhone1(user.getPhone1());
        existing.setPhone2(user.getPhone2());
        existing.setStatus(user.getStatus());
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        userDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<User> viewAll(AppUser usr, QueryParams params) throws IllegalAccessException {
        // Only admin has the privilege to view all users.
        ensureAdmin(usr);
        validateScope(usr, "user:query");

        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<User> rows = userDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded user record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public User view(AppUser usr, String id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in user is allowed to modify the user as identified by this id.
        ensureAuthorized(usr, id);
        
        // Fetch the user entry.
        User user = fetchUser(usr);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched user details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return user;
    }

    public User remove(AppUser usr, String id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in user is allowed to modify the user as identified by this id.
        ensureAuthorized(usr, id);
        // Fetch the user entry.
        User user = fetchUser(usr);

        userDAO.delete(user);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted User. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return user;
    }

    public User patchUp(AppUser usr, User user) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        User existing = userDAO.find(new User.UserPK(user.getUserId()));
        if (existing == null) {
            throw new IllegalArgumentException("No user found for identifier: " + user.getUserId());
        }
        // Update only the attributes given in input
        String attr = user.getFullName();
        if(attr != null && !attr.trim().isEmpty())
            existing.setFullName(attr);
        attr = user.getEmail();
        if(attr != null && !attr.trim().isEmpty())
            existing.setEmail(attr);
        attr = user.getPhone1();
        if(attr != null && !attr.trim().isEmpty())
            existing.setPhone1(attr);
        attr = user.getPhone2();
        if(attr != null && !attr.trim().isEmpty())
            existing.setPhone2(attr);
        attr = user.getPasswordHash();
        if(attr != null && !attr.trim().isEmpty())
            existing.setPasswordHash(attr);
        attr = user.getRole().name();
        if(attr != null && !attr.trim().isEmpty())
            existing.setRole(user.getRole());
        attr = user.getStatus().name();
        if(attr != null && !attr.trim().isEmpty())
            existing.setStatus(user.getStatus());

        userDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User record patched up successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }
}
