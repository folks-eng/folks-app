package com.folks.app.cache.impl;

import com.folks.app.cache.AbstractCache;
import com.folks.app.model.Category;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class CategoryCache extends AbstractCache<Integer, Category> {
    
    private static final CategoryCache CACHE = new CategoryCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private CategoryCache() {}
    
    public static CategoryCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "category";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
