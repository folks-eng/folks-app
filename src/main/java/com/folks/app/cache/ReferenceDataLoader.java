package com.folks.app.cache;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import org.javalabs.decl.util.ReflectionUtil;
import org.javalabs.jpa.JdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ReferenceDataLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceDataLoader.class);
    
    private final String REF_CACHE_CONFIG = "reference-data-cache.xml";
    
    public void loadCache(EntityManagerFactory emf) {
        EntityManager em = null;
        
        try {
            em = emf.createEntityManager();
            
            ReferenceDataCacheConfig refDataConfig = loadConfig();
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Read reference data file {}", REF_CACHE_CONFIG);
            }
            for (CacheConfig cc : refDataConfig.getCaches()) {
                Class<?> cacheClazz = Class.forName(cc.getClazz());
                Cache<Object, Object> cache = (Cache<Object, Object>)ReflectionUtil.invokeStatic(cacheClazz, "getCache");
                
                Class<?> clazz = ReflectionUtil.getElementType(cacheClazz, 1);
                String query = cc.getQuery() != null ? cc.getQuery() : clazz.getSimpleName() + ".selectAll";
                String pkField = idFieldName(cc, clazz);
                
                List<?> records = em
                        .createNamedQuery(query, clazz)
                        .getResultList();

                for (Object record : records) {
                    Object val = ReflectionUtil.invokeGetter(record, pkField);
                    cache.add(val, record);
                }
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Loaded {} cache. Size: {}", clazz.getSimpleName(), cache.count());
                }
            }
        }
        catch (ClassNotFoundException | JdbcException e) {
            LOGGER.error("Error loading startup cache", e);
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    private String idFieldName(CacheConfig cc, Class<?> clazz) {
        if (cc.getIdProperty() != null) {
            return cc.getIdProperty();
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Id.class) != null) {
                return field.getName();
            }
        }
        throw new IllegalArgumentException("Pojo " + clazz.getName() + " does not have an @Id field");
    }
    
    private ReferenceDataCacheConfig loadConfig() {
        try (InputStream inputStream =
                Thread.currentThread()
                      .getContextClassLoader()
                      .getResourceAsStream(REF_CACHE_CONFIG)) {

            if (inputStream == null) {
                throw new IllegalStateException("reference-data-cache.xml not found in classpath");
            }

            JAXBContext context = JAXBContext.newInstance(ReferenceDataCacheConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            return (ReferenceDataCacheConfig)unmarshaller.unmarshal(inputStream);
        }
        catch (JAXBException | IOException e) {
            throw new IllegalStateException("Unable to load reference-data-cache.xml", e);
        }
    }
}
