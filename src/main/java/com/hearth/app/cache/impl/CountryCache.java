package com.hearth.app.cache.impl;

import com.hearth.app.cache.AbstractCache;
import com.hearth.app.model.Country;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class CountryCache extends AbstractCache<Integer, Country> {
    
    private static final CountryCache CACHE = new CountryCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private CountryCache() {}
    
    public static CountryCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "country";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
