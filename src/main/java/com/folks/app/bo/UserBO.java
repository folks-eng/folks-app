package com.folks.app.bo;

import com.folks.app.exception.DataValidationException;
import com.folks.app.util.Constants;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class UserBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBO.class);
    
    private final UserDAO userDAO;

    public UserBO() {
        this.userDAO = DAOProxy.get(UserDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. UserDAO: {}", getClass().getSimpleName(), userDAO);
        }
    }

    public User create(AppUser usr, User user) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        validate(user);
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        //To move
        if (user.getStatus() == null) {
            System.out.println("Status set to ACTIVE");
            user.setStatus(User.Status.ACTIVE);
        }
        userDAO.insert(user);
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User created successfully with id {}. Elapsed time(ms): {}", user.getUserId(),
                    timer.elapsedTimeMillis());
        }
        return user;
    }

    private void validate(User user) {

        String name = user.getFullName();
        String mandatoryContact = user.getPhone1();
        String optionalContact = user.getPhone1();
        String email = user.getEmail();
        User.Status status = user.getStatus();

        if (status != null ) {
            String statusStr = status.name();
            System.out.println("Status in validate " +status.name());
            List<String> validStatusList = Arrays.asList("ACTIVE", "INACTIVE", "BLOCKED");
            if (!validStatusList.contains(statusStr))
                throw new DataValidationException("Status entered is invalid.");
        }
        if (mandatoryContact == null || mandatoryContact.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required.");
        }
        if (!Constants.MOBILENUM_PATTERN.matcher(mandatoryContact).matches()) {
            throw new IllegalArgumentException("Mobile number invalid." + mandatoryContact);
        }
        if (optionalContact != null && !optionalContact.trim().isEmpty()) {
            if (!Constants.MOBILENUM_PATTERN.matcher(optionalContact).matches())
                throw new IllegalArgumentException("Mobile number invalid." + optionalContact);
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        else {
            //TBD : verify by sending mail
        }
    }

    public User modify(AppUser usr, User user) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        validate(user);

        // First fetch the entry, to see if this already exists.
        User existing = userDAO.find(new User.UserPK(user.getUserId()));
        if (existing == null) {
            throw new IllegalArgumentException("No user found for identifier: " + user.getUserId());
        }
        // All attributes to be specified in input.
        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setPhone1(user.getPhone1());
        existing.setPhone2(user.getPhone2());
        existing.setPasswordHash(user.getPasswordHash());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
        if (user.getUpdatedAt() == null) {
            user.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        existing.setUpdatedAt(user.getUpdatedAt());
        userDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public User view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        User user = userDAO.find(new User.UserPK(id));
        if (user == null) {
            throw new IllegalArgumentException("No User found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched user details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return user;
    }

    public User remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        User user = userDAO.find(new User.UserPK(id));

        if (user == null) {
            throw new IllegalArgumentException("No user found for id: " + id);
        }
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

    public void create(AppUser usr, List<User> records) {
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

    public List<User> viewAll(AppUser usr, QueryParams params) {
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

}
