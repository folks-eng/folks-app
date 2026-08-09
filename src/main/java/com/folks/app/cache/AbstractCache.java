package com.folks.app.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract cache to have the implementation of some of the common APIs defined in {@link Cache}.
 * 
 * <p>
 * Theh abstract cache uses the default implementation of a LRU cache. If your
 * cache has the same requirement you can wish to extending this class, or write
 * your own cache by implementing {@link Cache} interface.
 * 
 * <p>
 * This implementation is not backed by any cache writer. That means, at the time of
 * querying the cache if it is found that the requested element is expired, then
 * there is no mechanism to automatically refresh the cached element. It will return
 * <code>null</code> , as opposed to send any stale data to the caller. It's the 
 * responsibility of the caller to refresh the element.
 * 
 * <p>
 * Implementation of this cache is synchronized.
 *
 * @author Sudiptasish Chanda
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);
    
    //private static final int MAX_SIZE = 512;
    private static final int MAX_SIZE = 30 * 1024;  // 30K elements.
    
    protected static final boolean IDLE = false;
    protected static final boolean BUSY = true;
    
    protected final AtomicBoolean lock = new AtomicBoolean(IDLE);
    private final AtomicLong cacheHit = new AtomicLong(0L);
    private final AtomicLong cacheMiss = new AtomicLong(0L);
    
    protected final LinkedHashMap<K, CachedObject<V>> LRU_MAP;
    
    protected AbstractCache() {
        this(MAX_SIZE);
    }
    
    protected AbstractCache(int maxSize) {
        LRU_MAP = new LinkedHashMap(32, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > maxSize;
            }
        };
    }
    
    /**
     * Return the retention policy of the element stored in the cache.
     * The retention policy set by the cache is applied on individual element.
     * A retention policy -1 indicates the cache will never expire.
     * 
     * @return long
     */
    public abstract long retention();

    @Override
    public void add(K key, V val) {
        addInternal(key, val);
    }
    
    protected void addInternal(K key, V val) {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            LRU_MAP.put(key, new CachedObject(val));
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public V get(K key) {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            CachedObject<V> obj = LRU_MAP.get(key);
            if (obj != null) {
                cacheHit.getAndIncrement();
                if (obj.expired(retention())) {
                    LRU_MAP.remove(key);

                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Element {} is expired in cache {}", key, getClass().getSimpleName());
                    }
                    return null;
                }
                return (V)obj.get();
            }
            else {
                cacheMiss.getAndIncrement();
            }
            return null;
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public boolean contains(K key) {
        return LRU_MAP.containsKey(key);
    }

    @Override
    public V remove(K key) {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            CachedObject<V> obj = LRU_MAP.remove(key);
            if (obj != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Element {} is removed from cache {}", key, getClass().getSimpleName());
                }
                return obj.get();
            }
            return null;
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public List<K> keys() {
        List<K> keys = new ArrayList<>(LRU_MAP.keySet());
        return keys;
    }

    @Override
    public int count() {
        return LRU_MAP.size();
    }

    @Override
    public long valueCount() {
        Collection<CachedObject<V>> values = LRU_MAP.values();
        
        if (values.isEmpty()) {
            return 0L;
        }
        CachedObject<V> cObject = values.iterator().next();
        V val = cObject.get();
        
        if (Collection.class.isAssignableFrom(val.getClass())) {
            long count = 0L;
            
            List<K> keys = keys();
            for (K key : keys) {
                CachedObject<V> obj = LRU_MAP.get(key);
                val = obj.get();
                count += ((Collection)val).size();
            }
            return count;
        }
        else {
            return values.size();
        }
    }
    
    @Override
    public long size() {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            // This API is a little expensive, as it calculates the size in realtime.
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bOut);
            out.writeObject(LRU_MAP);
            out.flush();
            out.close();
            
            return bOut.size();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public int flush() {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            int size = count();
            LRU_MAP.clear();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Cache {} is flushed", getClass().getSimpleName());
            }
            return size;
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public Map<K, V> getAll() {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            Map<K, V> tmp = new HashMap<>(count());
            for (Map.Entry<K, CachedObject<V>> me : LRU_MAP.entrySet()) {
                tmp.put(me.getKey(), me.getValue().get());
            }
            return tmp;
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public List<V> getAllValues() {
        try {
            while (! lock.compareAndSet(IDLE, BUSY));
            
            Collection<CachedObject<V>> values = LRU_MAP.values();
            List<V> list = new ArrayList<>(values.size());

            for (CachedObject<V> value : values) {
                list.add(value.get());
            }
            return Collections.unmodifiableList(list);
        }
        finally {
            lock.set(IDLE);
        }
    }

    @Override
    public long hits() {
        return cacheHit.get();
    }

    @Override
    public long misses() {
        return cacheMiss.get();
    }

    @Override
    public boolean readThrough() {
        return false;
    }
}
