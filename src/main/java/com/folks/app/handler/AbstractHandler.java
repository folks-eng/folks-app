package com.folks.app.handler;

import org.javalabs.decl.vertx.container.util.ResponseHandler;
import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.model.ItemList;
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
    
    /**
     * Builds an {@link ItemList} containing the supplied items and generates pagination links based on
     * the provided query parameters.
     *
     * <p>The method preserves the existing query parameters from {@code params} when constructing pagination links.
     * A {@code nextLink} is added when the number of returned items is equal to the requested limit, indicating that
     * additional results may be available. A {@code previousLink} is added when the current offset indicates that a
     * previous page exists.</p>
     *
     * <p>The pagination links include updated {@code offset} and {@code limit} query parameters while retaining
     * the other query parameters supplied in {@code params}.</p>
     *
     * @param path      The base path used to construct pagination links
     * @param params    The query parameters containing pagination information such as {@code offset} and {@code limit}
     *                  , as well as any additional parameters that should be preserved in generated links.
     * @param list      The items to include in the resulting {@link ItemList}
     * @return  An {@link ItemList} containing the supplied items and, when applicable, links to the next and previous pages
    */
    protected ItemList build(String path, QueryParams params, List<Object> list) {
        // Build the item list.
        ItemList itemList = new ItemList(list.size(), list, path);
        itemList.setHasMore(itemList.getTotal() == params.limit());
        
        String sep = "?";
        int idx = 0;
        
        for (String key : params.keys()) {
            String val = params.param(key);
            path += sep + key + "=" + val;
            idx ++;

            if (idx > 0) {
                sep = "&";
            }
        }
        if (itemList.isHasMore()) {
            itemList.setNextLink(path + sep + "offset=" + (params.offset() + params.limit()) + "&limit=" + params.limit());
        }
        if (params.offset() - params.limit() >= 0) {
            itemList.setPreviousLink(path + sep + "offset=" + (params.offset() + params.limit()) + "&limit=" + params.limit());
        }
        return itemList;
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
