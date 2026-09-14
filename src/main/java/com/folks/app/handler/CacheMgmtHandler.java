package com.folks.app.handler;

import com.folks.app.bo.CacheMgmtBO;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CacheMgmtHandler extends AbstractHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheMgmtHandler.class);
    
    private final CacheMgmtBO cacheBO;
    
    public CacheMgmtHandler(Vertx vertx) {
        super(vertx);
        cacheBO = new CacheMgmtBO();
    }
    
    public void viewConfigProp(RoutingContext ctx) {
        String methodName = ctx.request().getParam("method");
        
        try {
            Map<String, Object> map = cacheBO.invokeAPI(methodName);
            sendResponse(ctx, HttpURLConnection.HTTP_OK, map);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
    
    /**
     * API to return the comprehensive statistics for all the internal caches.
     * @param ctx 
     */
    public void viewStats(RoutingContext ctx) {
        try {
            List<Map> stats = cacheBO.viewStats();
            sendResponse(ctx, HttpURLConnection.HTTP_OK, stats);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
    
    /**
     * View all the elements from the specific cache.
     * @param ctx
     */
    public void viewAll(RoutingContext ctx) {
        final String cacheId = ctx.pathParam("cacheId");
        
        try {
            Map map = cacheBO.viewAll(cacheId, params(ctx));
            sendResponse(ctx, HttpURLConnection.HTTP_OK, map);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
    
    /**
     * View a specific element against key specified from the given cache.
     * @param ctx 
     */
    public void view(RoutingContext ctx) {
        final String cacheId = ctx.pathParam("cacheId");
        final String key = ctx.pathParam("key");
        
        try {
            Object element = cacheBO.view(cacheId, key);
            sendResponse(ctx, HttpURLConnection.HTTP_OK, element);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
    
    /**
     * Remove all elements from the cache as identified by the cache id.
     * @param ctx 
     */
    public void removeAll(RoutingContext ctx) {
        final String cacheId = ctx.pathParam("cacheId");
        final String confirm = ctx.request().getParam("confirm");
        
        try {
            if (confirm == null || ! "true".equalsIgnoreCase(confirm)) {
                throw new IllegalArgumentException("Provide confirm=true to flush the cache");
            }
            int size = cacheBO.removeAll(cacheId);
            sendResponse(ctx
                    , HttpURLConnection.HTTP_OK
                    , new ServerMessage(HttpURLConnection.HTTP_OK, "Cleared all " + size
                        + " element(s) from cache " + cacheId));
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
    
    /**
     * Remove only the element identified by the key from the specified cache.
     * @param ctx 
     */
    public void remove(RoutingContext ctx) {
        final String cacheId = ctx.pathParam("cacheId");
        final String key = ctx.pathParam("key");
        
        try {
            int size = cacheBO.remove(cacheId, key);
            sendResponse(ctx
                    , HttpURLConnection.HTTP_OK
                    , new ServerMessage(HttpURLConnection.HTTP_OK, "Remove specified element for key " + key
                        + " from the cache " + cacheId));
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage(), e);
            ctx.fail(e);
        }
    }
}
