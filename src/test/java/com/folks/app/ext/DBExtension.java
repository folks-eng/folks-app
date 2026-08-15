package com.folks.app.ext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Properties;
import org.javalabs.decl.util.StreamUtil;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class DBExtension implements BeforeAllCallback, AfterAllCallback, ExtensionContext.Store.CloseableResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DBExtension.class);
    
    private static final String PERSISTENCE_UNIT = "folks-app-pu";
    private EntityManagerFactory emf = null;

    @Override
    public void beforeAll(ExtensionContext ec) throws Exception {
        String uniqueKey = getClass().getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::beforeAll. Test Class: {}", uniqueKey);
        }
        Object value = ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).get(uniqueKey + "~DB");
        
        if (value == null) {
            // First test container invocation.
            // It will start the in-memory h2 db.
            // A first time call to DriverManager.getConnection("jdbc:h2:mem:.....) will launch the database.
            setup();
            
            // Post setup, keep the entity manager factory in the global store.
            ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).put(uniqueKey + "~DB", emf);
        }
        else {
            LOGGER.warn("DBExtension is already initialized");
        }
    }
    
    protected void setup() throws Exception {
        try {
            Properties props = new Properties();

            configureDB(props);

            // Perform cleanup
            // cleanupDB(props);
        }
        catch (Exception e) {
            LOGGER.error("Error in setup", e);
            throw e;
        }    
    }

    private void configureDB(Properties prop) throws Exception {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Configured application. Entity manager factory is initialized");
        }
    }
    
    private void cleanupDB(Properties prop) throws Exception {
        if (prop.getProperty("db.cleanup", "true").equals("true")) {
            // Read the db script to be executed
            String script = new String(StreamUtil.read("db_cleanup.sql"));

            // Execute db script
            int count = execute("Cleanup", script);
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Performed database cleanup. Response: {}", count);
            }
        }
    }
    
    private int execute(String op, String script) {
        EntityManager em = null;
        
        try {
            em = emf.createEntityManager();
            
            em.getTransaction().begin();
            int i = em.createNativeQuery(script).executeUpdate();
            em.getTransaction().commit();
            
            return i;
        }
        catch (Exception e) {
            LOGGER.error("Error executing script. Operation: " + op, e);
            if (em != null) {
                em.getTransaction().rollback();
            }
            throw e;
        }
        finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext ec) throws Exception {
        String uniqueKey = getClass().getName();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::afterAll. Test Class: {}", uniqueKey);
        }
        
        emf = (EntityManagerFactory)ec.getRoot()
                .getStore(ExtensionContext.Namespace.GLOBAL)
                .get(uniqueKey + "~DB");
            
        Properties prop = new Properties();
        cleanupDB(prop);
        
        ec.getRoot().getStore(ExtensionContext.Namespace.GLOBAL).remove(uniqueKey + "~DB");
    }

    @Override
    public void close() throws Throwable {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Invoked Database Extension::close");
        }
        if (emf != null) {
            emf.close();
        }
    }
    
}
