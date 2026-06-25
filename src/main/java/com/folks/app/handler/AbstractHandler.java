package com.folks.app.handler;

import org.javalabs.decl.vertx.container.util.ResponseHandler;
import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.util.QueryParams;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public abstract class AbstractHandler {
    
    private final ApplicationConfiguration config = ApplicationConfiguration.getInstance();
    
    // Verticle class that has this handler.
    private final Vertx vertx;
    
    protected AbstractHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * Return the verticle name this handler class is linked to.
     * @return AbstractVerticle
     */
    protected Vertx vertx() {
        return vertx;
    }
    
    /**
     * Return the platform configuration object.
     * @return EcmConfig
     */
    public ApplicationConfiguration config() {
        return config;
    }
    
    /**
     * Create and return the application user.
     * 
     * @param ctx
     * @return EcmUser
     */
    protected AppUser user(RoutingContext ctx) {
        if (ctx.user() != null) {
            AppUser user = new AppUserImpl(new UserPrincipal(ctx.user().principal().mapTo(HashMap.class)));
            return user;
        }
        return new AppUserImpl(new UserPrincipal(Map.of("sub", Collections.EMPTY_LIST)));
    }
    
    protected QueryParams params(RoutingContext ctx) {
        Map<String, List<String>> map = new HashMap<>();
        for (String key : ctx.queryParams().names()) {
            map.put(key, ctx.queryParams().getAll(key));
        }
        QueryParams params = new QueryParams(map);
        return params;
    }
    
    protected void sendResponse(RoutingContext ctx, int httpCode, Object responseObj) {
        sendResponse(ctx, responseObj, httpCode, Boolean.TRUE, "application/json");
    }
    
    protected void sendResponse(RoutingContext ctx
        , Object responseObj
        , int httpCode
        , Boolean chunked
        , String contentType) {
        
        ResponseHandler.send(
                ctx.request().params().contains("env")
                , ctx
                , null
                , httpCode
                , responseObj
                , chunked
                , contentType);
    }
}
