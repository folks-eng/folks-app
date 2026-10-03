package com.hearth.app.cache.impl;

import com.hearth.app.cache.AbstractCache;
import com.hearth.app.model.Category;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A cache to store the mapping between a category id and associated category.
 *
 * @author schan280
 */
public class CategoryCache extends AbstractCache<Integer, Category> {

    private static final CategoryCache CACHE = new CategoryCache();

    private static final int RETENTION_POLICY = -1;     // Never expires

    private CategoryCache() {
    }

    public static CategoryCache getCache() {
        return CACHE;
    }

    /**
     * Return the name of the cache.
     *
     * @return String
     */
    public static String name() {
        return "category";
    }

    @Override
    public long retention() {
        return RETENTION_POLICY;
    }

    @Override
    public List<Category> query(String attrName, Object attrValue) {
        if (attrName.equals("parentId")) {
            return getAllValues().stream()
                    .filter(c -> Objects.equals(c.getParentId(), attrValue))
                    .collect(Collectors.toList());
        }
        else if (attrName.equals("name")) {
            return getAllValues().stream()
                    .filter(c -> Objects.equals(c.getName(), attrValue))
                    .collect(Collectors.toList());
        }
        else {
            throw new IllegalArgumentException("Unsupported attribute name. Supported names: parentId, name");
        }
    }
}
