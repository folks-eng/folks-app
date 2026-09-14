package com.folks.app.bo;

import com.folks.app.cache.Cache;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.util.ImplClassScanner;
import com.folks.app.util.QueryParams;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CacheMgmtBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheMgmtBO.class);
    
    private final Map<String, Cache> cacheMapping = new HashMap<>();
    
    public Map<String, Object> invokeAPI(String methodName) {
        try {
            if (methodName == null || methodName.trim().length() == 0) {
                throw new RuntimeException("Missing mandatory query parameter: method");
            }
            Method method = ApplicationConfiguration.class.getDeclaredMethod(methodName, new Class[] {});
            Object val = method.invoke(ApplicationConfiguration.getInstance(), new Object[] {});
            
            Map<String, Object> map = new HashMap<>();
            map.put("method", methodName);
            map.put("value", val);
            
            return map;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<Map> viewStats() {
        verify();

        NumberFormat nFormat = new DecimalFormat("0.00");
        List<Map> stats = new ArrayList<>();
        Map<String, Object> stat = null;
        Cache cache = null;
        long cacheHit, cacheMiss = 0L;

        for (Map.Entry<String, Cache> me : cacheMapping.entrySet()) {
            cache = me.getValue();
            cacheHit = cache.hits();
            cacheMiss = cache.misses();

            stat = new HashMap<>();
            stat.put("name", me.getKey());
            stat.put("readThrough", cache.readThrough());
            stat.put("size", cache.size());
            stat.put("count", cache.count());
            stat.put("hits", cacheHit);
            stat.put("misses", cacheMiss);
            if (cacheHit > 0 || cacheMiss > 0) {
                stat.put("hitRatio", nFormat.format((cacheHit * 1.0D / (cacheHit + cacheMiss)) * 100) + "%");
                stat.put("missRatio", nFormat.format((cacheMiss * 1.0D / (cacheHit + cacheMiss)) * 100) + "%");
            }
            else {
                stat.put("hitRatio", "0%");
                stat.put("missRatio", "0%");
            }
            stats.add(stat);
        }
        return stats;
    }
    
    /**
     * View all the elements from the specific cache.
     * 
     * @param cacheId
     * @param params
     */
    public Map viewAll(String cacheId, QueryParams params) {
        verify();
        
        Cache mappedCache = cacheMapping.get(cacheId);
        if (mappedCache != null) {
            Map map = mappedCache.getAll();
            
            if (params != null && !map.isEmpty() && !params.isEmpty()) {
                Object element = null;
                
                Collection<Object> list = map.values();
                Iterator<Object> itr = list.iterator();
                if (itr.hasNext()) {
                    element = itr.next();
                }
                boolean exit = false;
                if (element != null && !(element instanceof Map || element instanceof Collection)) {
                    for (Iterator<Map.Entry<Object, Object>> meItr = map.entrySet().iterator(); meItr.hasNext(); ) {
                        Map.Entry<Object, Object> me = meItr.next();
                        element = me.getValue();
                        
                        for (Map.Entry<String, List<String>> param : params.entries().entrySet()) {
                            String attr = param.getKey();
                            List<String> vals = param.getValue();

                            Object attrVal = ReflectionUtil.invokeGetter(element, attr);
                            if (vals.get(0) == null && attrVal == null) {
                                // Retain this entry in the map
                            }
                            else if (vals.get(0) != null && attrVal != null) {
                                if (isPrimitive(attrVal)) {
                                    if (String.valueOf(attrVal).equals(vals.get(0))) {
                                        // Retain this entry in the map
                                    }
                                    else {
                                        // Not equal ...
                                        meItr.remove();
                                        break;
                                    }
                                }
                                else {
                                    // No support for non-primitive data
                                    exit = true;
                                    break;
                                }
                            }
                            else {
                                // Not equal ...
                                meItr.remove();
                                break;
                            }
                        }
                        if (exit) {
                            break;
                        }
                    }
                }
            }
            return map;
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid cache id " + cacheId + " provided."
                    + " Valid values are: " + cacheMapping.keySet());
        }
    }
    
    /**
     * View a specific element against key specified from the given cache.
     * @param cacheId
     * @param key
     * 
     * @return  Object
     */
    public Object view(String cacheId, String key) {
        Cache mappedCache = cacheMapping.get(cacheId);
        if (mappedCache != null) {
            Object element = mappedCache.get(key);
            if (element != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Fetched element {} from cache {}", element, cacheId);
                }
                return element;
            }
            else {
                throw new IllegalArgumentException("No cache element found in cache "
                        + cacheId + " for key " + key);
            }
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid cache id " + cacheId + " provided."
                    + " Valid values are: " + cacheMapping.keySet());
        }
    }
    
    /**
     * Remove all elements from the cache as identified by the cache id.
     * @param cacheId 
     * @return  int
     */
    public int removeAll(String cacheId) {
        Cache mappedCache = cacheMapping.get(cacheId);
        
        if (mappedCache != null) {
            int size = mappedCache.flush();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Removed {} element(s) from cache {}", size, cacheId);
            }
            return size;
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid cache id " + cacheId + " provided."
                    + " Valid values are: " + cacheMapping.keySet());
        }
    }
    
    /**
     * Remove only the element identified by the key from the specified cache.
     * @param cacheId
     * @param key
     * 
     * @return  int
     */
    public int remove(String cacheId, String key) {
        Cache mappedCache = cacheMapping.get(cacheId);
        if (mappedCache != null) {
            Object element = mappedCache.remove(key);
            if (element != null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Removed element {} from cache {}", element, cacheId);
                }
                return 1;
            }
            else {
                throw new IllegalArgumentException("No cache element found in cache "
                        + cacheId + " for key " + key);
            }
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid cache id " + cacheId + " provided."
                    + " Valid values are: " + cacheMapping.keySet());
        }
    }
    
    private void verify() {
        if (! cacheMapping.isEmpty()) {
            return;
        }
        synchronized (cacheMapping) {
            List<Class<?>> impl = ImplClassScanner.findImplementingClasses(Cache.class, new String[] {"com.folks.app"});
            if (! impl.isEmpty()) {
                for (Class<?> clazz : impl) {
                    String name = (String)ReflectionUtil.invokeStatic(clazz, "name");
                    Cache cache = (Cache)ReflectionUtil.invokeStatic(clazz, "getCache");
                    cacheMapping.put(name, cache);
                }
            }
        }
    }
    
    private boolean isPrimitive(Object val) {
        Class<?> clazz = val.getClass();
        return clazz == Byte.class
                || clazz == Short.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Float.class
                || clazz == Double.class
                || clazz == Boolean.class
                || clazz == String.class
                || clazz == byte.class
                || clazz == short.class
                || clazz == int.class
                || clazz == float.class
                || clazz == double.class
                || clazz == boolean.class
                || clazz.isEnum();
    }
}
