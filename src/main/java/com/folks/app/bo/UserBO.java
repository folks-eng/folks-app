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
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
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
            LOGGER.debug("Initialized User Business Object: {}. UserDAO: {}", getClass().getSimpleName(), userDAO);
        }
    }

    public User create(AppUser usr, User user) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        validateScope(usr, "user:create");
        
        Validator.validateUser(user);

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
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only admin has the privilege to create user in bulk.
        ensureAdmin(usr);
        validateScope(usr, "user:create");
        
        for (User user : records) {
            Validator.validateUser(user);
        }
        for (User user : records) {
            user.setExternalId(IdGenerator.generate(user.getPhone1(), user.getEmail()));
            user.setRole(user.getRole() != null ? user.getRole() : User.Role.CUSTOMER);
            user.setStatus(User.Status.ACTIVE);
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

    public List<User> viewAll(AppUser usr, QueryParams params) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only admin has the privilege to view all users.
        ensureAdmin(usr);
        
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
        if (! isAdmin(usr)) {
            ensureAuthorized(usr, id);
        }
        
        // Fetch the user entry.
        User user = userDAO.findByExtId(id);
        if (user == null) {
            throw new ResourceNotFoundException("No User found for id: " + id);
        }

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched user details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return user;
    }

    /* Update all the attributes for the resource from payload. If you leave something out, that part of the resource will be
    erased or set to default.
     */
    public User modify(AppUser usr, User user) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in user is allowed to modify the user as identified by this id.
        if (! isAdmin(usr)) {
            ensureAuthorized(usr, user.getExternalId());
        }
        Validator.validateUser(user);

        User existing = userDAO.findByExtId(user.getExternalId());
        if (existing == null) {
            throw new ResourceNotFoundException("No User found for id: " + user.getExternalId());
        }

        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setPhone1(user.getPhone1());
        existing.setPhone2(user.getPhone2());
        
        // User cannot change the status or role.
        // existing.setStatus(user.getStatus());
        // existing.setRole(user.getRole());
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        userDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public User remove(AppUser usr, String id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in user is allowed to modify the user as identified by this id.
        if (! isAdmin(usr)) {
            ensureAuthorized(usr, id);
        }
        // Fetch the user entry.
        User user = userDAO.findByExtId(id);
        if (user == null) {
            throw new ResourceNotFoundException("No User found for id: " + id);
        }

        userDAO.delete(user);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted User. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return user;
    }
}
