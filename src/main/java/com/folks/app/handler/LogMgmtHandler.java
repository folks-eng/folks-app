package com.folks.app.handler;

import com.folks.app.bo.LogMgmtBO;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class LogMgmtHandler extends AbstractHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LogMgmtHandler.class);
    
    private final LogMgmtBO logBO;
    
    public LogMgmtHandler(Vertx vertx) {
        super(vertx);
        logBO = new LogMgmtBO();
    }
    
    /**
     * API to update the logging level.
     * 
     * <p>
     * Traditionally, this API changes the logging level of a class specific logger.
     * If user wants to change the log level of the complete app, then specify "default"
     * as name in the payload (json) , which will result the log level to be changed
     * for the entire service.
     * @param ctx 
     */
    public void modify(RoutingContext ctx) {
        Map<String, String> payload = MapperUtil.decode(ctx.body().buffer().getBytes(), HashMap.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Receievd a request to modify the logger. Payload: {}", payload);
        }
        String name = payload.get("name");
        String level = payload.get("level");
        
        try {
            logBO.modify(name, level);
            sendResponse(ctx
                , HttpURLConnection.HTTP_OK
                , new ServerMessage(HttpURLConnection.HTTP_OK
                    , "Changed logging level of logger " + name + " to " + level));
        }
        catch (Exception e) {
            ctx.fail(e);
        }
    }
    
    /**
     * View all the {@link Logger}s.
     * @param ctx   Vertx routing context object.
     */
    public void viewAll(RoutingContext ctx) {
        String ecmOnly = ctx.request().getParam("ecmOnly");
        
        try {
            if (ecmOnly == null) {
                ecmOnly = "true";
            }
            List<Map<String, String>> list = logBO.viewAll(ecmOnly);
            sendResponse(ctx, HttpURLConnection.HTTP_OK, list);
        }
        catch (RuntimeException e) {
            ctx.fail(e);
        }
    }
}
