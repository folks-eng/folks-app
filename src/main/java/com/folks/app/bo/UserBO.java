package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
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
        
        user.setExternalId(UUID.randomUUID().toString());
        user.setRole(User.Role.CUSTOMER);
        user.setStatus(User.Status.ACTIVE);
        
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        userDAO.insert(user);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return user;
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

    public User modify(AppUser usr, User user) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        User existing = userDAO.find(new User.UserPK(user.getUserId()));
        if (existing == null) {
            throw new IllegalArgumentException("No user found for identifier: " + user.getUserId());
        }
        // Update attributes of existing record
        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());
        existing.setPhone1(user.getPhone1());
        existing.setPhone2(user.getPhone2());
        existing.setPasswordHash(user.getPasswordHash());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());

        userDAO.update(user);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("User record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
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
}
