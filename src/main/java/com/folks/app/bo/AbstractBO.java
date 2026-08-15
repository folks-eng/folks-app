package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.model.User;

/**
 *
 * @author schan280
 */
public abstract class AbstractBO {
    
    public static final String UNAUTHORIZED_MSG = "Access to this resource is restricted";
    
    protected AbstractBO() {}
 
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
}
