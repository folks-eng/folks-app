package com.folks.app.auth;

/**
 * Represents an authenticated User and contains principles to authorize the user.
 *
 * @author schan280
 */
public interface AppUser {
    
    /**
     * Return the user principal.
     * 
     * @return Principal
     */ 
    Principal principal();
}

