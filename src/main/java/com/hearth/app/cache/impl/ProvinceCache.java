package com.hearth.app.cache.impl;

import com.hearth.app.cache.AbstractCache;
import com.hearth.app.model.Province;

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
