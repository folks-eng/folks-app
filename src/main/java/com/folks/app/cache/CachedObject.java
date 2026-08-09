package com.folks.app.cache;

import java.io.Serializable;

/**
 * Represent a cached object.
 *
 * @author Sudiptasish Chanda
 */
public class CachedObject<T> implements Serializable {
    
    private final T element;
    private final long lastUpdate;
    
    public CachedObject(T obj) {
        element = obj;
        lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Return the underlying element.
     * @return T
     */
    public T get() {
        return (T)element;
    }
    
    /**
     * Check if this cache element is expired.
     * @param retention
     * @return boolean
     */
    public boolean expired(long retention) {
        return retention != -1 && System.currentTimeMillis() - lastUpdate > retention;
    }
}
