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
public class UserBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBO.class);
    
    private final UserDAO userDAO;

    public UserBO() {
        this.userDAO = DAOProxy.get(UserDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. UserDAO: {}", getClass().getSimpleName(), userDAO);
        }
    }

    public User create(AppUser usr, User user) {
        // Only adming has the privilege to create user.
        ensureAdmin(usr);
        validateScope(usr, "user:create");

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

    public void create(AppUser usr, List<User> records) throws IllegalAccessException {
        // Only adming has the privilege to create user in bulk.
        ensureAdmin(usr);
        validateScope(usr, "user:create");

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

        // Only the logged in user is allowed to modify the user as identified by this id.
        ensureAuthorized(usr, user.getExternalId());
        
        // Fetch the user entry.
        User existing = fetchUser(usr);
        
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
        // Only adming has the privilege to view all users.
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

    public User view(AppUser usr, String id) {
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

    public User remove(AppUser usr, String id) {
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
    
    /**
     * Retrieves the user associated with the authenticated application user.
     * 
     * <p>
     * The user's external identifier is obtained from the {@code sub} claim of the JWT principal and is used to
     * query the user data store.
     * 
     * <p>
     * The user may first be looked up from a distributed cache to avoid an * unnecessary database query.
     * If the user is not available in the cache, the persistent data store is queried as a fallback.
     *
     * @param usr   The authenticated application user containing the JWT principal
     * @return User The user associated with the external identifier
     * 
     * @throws IllegalArgumentException if no user exists for the external identifier
     */
    private User fetchUser(AppUser usr) {
        // Query the user based on external_id.
        // external_id will be part of jwt token as 'sub'.
        String extId = usr.principal().sub();

        User user = userDAO.select(extId);
        if (user == null) {
            throw new IllegalArgumentException("No User found for id: " + extId);
        }
        return user;
    }
    
    /**
     * Ensure the logged in user is authorized to perform certain operation.
     * 
     * <p>
     * Ensure the external id present in the path parameter or in the payload matches with the {@code sub} 
     * claim of the JWT principal.
     * 
     * @param usr   The authenticated application user containing the JWT principal
     * @param extId The external id passed in the path parameter. 
     */
    private void ensureAuthorized(AppUser usr, String extId) {
        try {
            if (extId != null && ! usr.principal().sub().contains(extId)) {
                throw new IllegalAccessException(UNAUTHORIZED_MSG);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
