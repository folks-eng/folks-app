package com.hearth.app.cache.impl;

import com.hearth.app.cache.AbstractCache;
import com.hearth.app.model.Neighbourhood;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class NeighbourhoodCache extends AbstractCache<Integer, Neighbourhood> {
    
    private static final NeighbourhoodCache CACHE = new NeighbourhoodCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private NeighbourhoodCache() {}
    
    public static NeighbourhoodCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "neighbourhood";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }
}
