package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.User;
import com.folks.app.util.ResourceNotFoundException;
import jakarta.persistence.NoResultException;
import org.javalabs.jpa.DAOProxy;

/**
 *
 * @author schan280
 */
public abstract class AbstractBO {
    
    public static final String UNAUTHORIZED_MSG = "Access to this resource is restricted";
    
    protected final UserDAO userDAO;
    
    protected AbstractBO() {
        this.userDAO = DAOProxy.get(UserDAO.class);
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
    protected User fetchUser(AppUser usr) {
        try {
            // Query the user based on external_id.
            // external_id will be part of jwt token as 'sub'.
            return userDAO.findByExtId(usr.principal().sub());
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("No User found for id: " + usr.principal().sub());
            //throw new IllegalArgumentException("No User found for id: " + usr.principal().sub());
        }
    }
 
    protected void ensureAdmin(AppUser usr) throws IllegalAccessException {
        if (! User.isAdmin(usr.principal().priv())) {
            throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
    }
    
    protected void validateScope(AppUser usr, String scope) throws IllegalAccessException {
        if (! usr.principal().scopes().contains(scope)) {
            throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
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
     * @throws java.lang.IllegalAccessException 
     */
    protected void ensureAuthorized(AppUser usr, String extId) throws IllegalAccessException {
        if (extId != null && ! usr.principal().sub().contains(extId)) {
            throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
    }
}
