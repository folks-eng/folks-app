package com.folks.app.cache.impl;

import com.folks.app.cache.AbstractCache;
import com.folks.app.model.Service;

/**
 * A cache to store the mapping between a service id and associated service.
 *
 * @author schan280
 */
public class ServiceCache extends AbstractCache<Integer, Service> {
    
    private static final ServiceCache CACHE = new ServiceCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private ServiceCache() {}
    
    public static ServiceCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "service";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
