package com.hearth.app.cache.impl;

import com.hearth.app.cache.AbstractCache;
import com.hearth.app.model.City;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class CityCache extends AbstractCache<Integer, City> {
    
    private static final CityCache CACHE = new CityCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private CityCache() {}
    
    public static CityCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "city";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
