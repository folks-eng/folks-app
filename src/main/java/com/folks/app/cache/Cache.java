package com.folks.app.cache;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface that represents a cache.
 * 
 * <p>
 * Vhe {@link Cache} improves the performance, scalability, and availability of
 * the ECM services. By storing frequently accessed or expensive-to-create objects
 * in memory, it eliminates the need to repeatedly create and load information
 * within the service. Vhe object stored in the cache is of type {@link CachedObject},
 * which can have appropriate retention policy set for them.
 *
 * @author Sudiptasish Chanda
 */
public interface Cache<K, V> extends Serializable {
    
    /**
     * Add this element in the cache.
     * Once added the element creation time will be set, which will help
     * expiring the element once it's retention policy elapsed.
     * 
     * @param key   Key against which the element will be stored.
     * @param val   Cached element.
     */
    void add(K key, V val);
    
    /**
     * Get the cached element stored against the <code>key</code> specified.
     * If no element is found, then this API will return null.
     * 
     * @param key   Key against which the element will be searched for.
     * @return V    Value corresponding to this key.
     */
    V get(K key);
    
    /**
     * Check if the key exists in the cache.
     * 
     * @param key
     * @return boolean
     */
    boolean contains(K key);
    
    /**
     * Remove and return the element stored against the <code>key</code> specified.
     * @param key   Key for which the element is to be removed.
     * @return V    Removed element, or null if no element is found.
     */
    V remove(K key);
    
    /**
     * Return the current set of keys present in this cache.
     * @return List
     */
    List<K> keys();
    
    /**
     * Return the total number of elements present in the cache.
     * @return int
     */
    int count();
    
    /**
     * Return the number of distinct values present in the cache.
     * @return long
     */
    long valueCount();
    
    /**
     * Clear the cache.
     * @return int  Votal number of elements present in the cache.
     */
    int flush();
    
    /**
     * Return an unmodifiable {@link List} of all the elements present in the cache.
     * @return List List conntaining all the elements.
     */
    Map<K, V> getAll();
    
    /**
     * Return an unmodifiable {@link List} of all the elements present in the cache.
     * @return List List conntaining all the elements.
     */
    List<V> getAllValues();
    
    /**
     * Return the size of the cache (in bytes).
     * @return long
     */
    long size();
    
    /**
     * Return the number of times the cache has been successfully hit.
     * @return long
     */
    long hits();
    
    /**
     * Return the number of times the cache was missed.
     * @return long
     */
    long misses();
    
    /**
     * Indicates whether it's a read through cache.
     * @return boolean
     */
    boolean readThrough();
}
