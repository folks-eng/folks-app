package com.folks.app.bo;

import com.folks.app.util.LoggerUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class LogMgmtBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogMgmtBO.class);
    
    public LogMgmtBO() { }
    
    public List<Map<String, String>> viewAll(String flag) {
        Method method = null;
        
        try {
            Collection<?> loggers = LoggerUtil.getLoggers(flag);
            
            List<Map<String, String>> array = new ArrayList<>();
            Map<String, String> entry = null;
            String name = null;
            
            for (Object logger : loggers) {
                method = logger.getClass().getMethod("getName", new Class[] {});
                name = (String)method.invoke(logger, new Object[] {});
                
                if (flag.equals("true") && ! name.startsWith("com.folks.app")) {
                    continue;
                }
                method = logger.getClass().getMethod("getLevel", new Class[] {});
                
                entry = new HashMap<>();
                entry.put("name", name);
                entry.put("level", method.invoke(logger, new Object[] {}).toString());
                
                array.add(entry);
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Found {} loggers", array.size());
            }
            return array;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * API to update the logging level.
     * 
     * <p>
     * Traditionally, this API changes the logging level of a class specific logger.
     * If user wants to change the log level of the complete app, then specify "default"
     * as name in the payload (json) , which will result the log level to be changed
     * for the entire service.
     * 
     * @param logger
     * @param level
     */
    public void modify(String logger, String level) {
        try {
            LoggerUtil.changeLoggingLevel(logger, level);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Changed logging level of logger {} to {}", logger, level);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
