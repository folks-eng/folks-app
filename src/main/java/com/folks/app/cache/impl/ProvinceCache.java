package com.folks.app.cache.impl;

import com.folks.app.cache.AbstractCache;
import com.folks.app.model.Province;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class ProvinceCache extends AbstractCache<Integer, Province> {
    
    private static final ProvinceCache CACHE = new ProvinceCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private ProvinceCache() {}
    
    public static ProvinceCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "province";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
