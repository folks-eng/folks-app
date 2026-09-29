package com.folks.app.cache.impl;

import com.folks.app.cache.AbstractCache;
import com.folks.app.model.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A cache to store the mapping between a service id and associated service.
 *
 * @author schan280
 */
public class ServiceCache extends AbstractCache<Integer, Service> {
    
    private static final ServiceCache CACHE = new ServiceCache();
    
    private static final int RETENTION_POLICY = -1;     // Never expires
    
    private ServiceCache() {}
    
    public static ServiceCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     * @return String
     */
    public static String name() {
        return "service";
    }
    
    @Override
    public long retention() {
        return RETENTION_POLICY;
    }

    @Override
    public List<Service> query(String attrName, Object attrValue) {
        if (attrName.equals("categoryId")) {
            return getAllValues().stream()
                    .filter(s -> Objects.equals(s.getCategoryId(), attrValue))
                    .collect(Collectors.toList());
        }
        else if (attrName.equals("name")) {
            return getAllValues().stream()
                    .filter(s -> Objects.equals(s.getName(), attrValue))
                    .collect(Collectors.toList());
        }
        else {
            throw new IllegalArgumentException("Unsupported attribute name. Supported names: categoryId, name");
        }
    }
}
