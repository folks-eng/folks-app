package com.folks.app.cache.impl;

import com.folks.app.cache.AbstractCache;
import com.folks.app.model.User;

/**
 * A cache to store the mapping between a user (email address) and the set of roles assigned to it.
 *
 * @author schan280
 */
public class UserRoleCache extends AbstractCache<String, User> {
    
    private static final UserRoleCache CACHE = new UserRoleCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private UserRoleCache() {}
    
    public static UserRoleCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "user";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
