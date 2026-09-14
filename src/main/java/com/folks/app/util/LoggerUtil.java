package com.folks.app.util;

import java.lang.reflect.Method;
import java.util.Collection;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author schan280
 */
public final class LoggerUtil {
    
    /**
     * Change the logging level of the logger as identified by the loggerName to 
     * the level as specified by level.
     * 
     * @param loggerName    Logegr name
     * @param level         Desired level to be set
     */
    public static void changeLoggingLevel(String loggerName, String level) throws Exception {
        Method method = null;
        org.apache.logging.log4j.Logger logger = null;
            
        if ("default".equalsIgnoreCase(loggerName)) {
            // Change logging level of the service.
            logger = LogManager.getRootLogger();
        }
        else {
            // Change logging level of specific logger class.
            Collection<?> loggers = getLoggers("true");
            String n = null;
            boolean found = false;

            for (Object l : loggers) {
                method = l.getClass().getMethod("getName", new Class[] {});
                n = (String)method.invoke(l, new Object[] {});

                if (n.equals(loggerName)) {
                    found = true;
                    break;
                }
            }
            if (! found) {
                throw new IllegalArgumentException("No logger with name " + loggerName + " found");
            }
            logger = LogManager.getLogger(loggerName);
        }
        method = logger.getClass().getDeclaredMethod("setLevel", new Class[] {Level.class});
        method.invoke(logger, new Object[] {Level.valueOf(level)});
    }
    
    public static  Collection<?> getLoggers(String ecmOnly) throws Exception {
        // Because log42-core is available only at runtime, therefore use reflection.
        org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();

        // Step 1: Cast the Logger object to core Logger.
        Class<?> clazz = Class.forName("org.apache.logging.log4j.core.Logger");
        Object coreLogger = clazz.cast(logger);

        // Step 2: Obtain the LoggerContext.
        Method method = coreLogger.getClass().getDeclaredMethod("getContext", new Class[] {});
        Object loggerContext = method.invoke(coreLogger, new Object[] {});

        // Step 3: Fetch all registered loggers.
        method = loggerContext.getClass().getDeclaredMethod("getLoggers", new Class[] {});
        return (Collection<?>)method.invoke(loggerContext, new Object[] {});
    }
    
    public static Object getLogger(String ecmOnly, String loggerName) throws Exception {
        // Because log42-core is available only at runtime, therefore use reflection.
        org.apache.logging.log4j.Logger logger = LogManager.getRootLogger();

        // Step 1: Cast the Logger object to core Logger.
        Class<?> clazz = Class.forName("org.apache.logging.log4j.core.Logger");
        Object coreLogger = clazz.cast(logger);

        // Step 2: Obtain the LoggerContext.
        Method method = coreLogger.getClass().getDeclaredMethod("getContext", new Class[] {});
        Object loggerContext = method.invoke(coreLogger, new Object[] {});

        // Step 3: Fetch all registered loggers.
        method = loggerContext.getClass().getDeclaredMethod("getLogger", new Class[] {String.class});
        return method.invoke(loggerContext, new Object[] { loggerName });
    }
}
