package com.folks.app.cache.impl;

import com.folks.app.cache.Cache;
import org.javalabs.decl.util.ReflectionUtil;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

/**
 *
 * @author schan280
 */
public class CacheTest {
 
    @Test
    public void testInit() {
        String cacheClass = "com.folks.app.cache.impl.CategoryCache";
        
        try {
            Class<?> cacheClazz = Class.forName(cacheClass);
            Cache<?, ?> cache = (Cache<?, ?>)ReflectionUtil.invokeStatic(cacheClazz, "getCache");
            
            Class<?> clazz = ReflectionUtil.getElementType(cacheClazz, 1);
            assertTrue(clazz.getName().equals("com.folks.app.model.Category"));
        }
        catch (Exception e) {
            fail(e);
        }
    }
}
