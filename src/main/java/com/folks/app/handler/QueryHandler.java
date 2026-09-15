package com.folks.app.handler;

import com.folks.app.model.Address;
import com.folks.app.model.ItemList;
import com.folks.app.util.QueryParams;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.List;

/**
 *
 * @author sudip
 */
public class QueryHandler extends AbstractHandler {
    
    public QueryHandler(Vertx vertx) {
        super(vertx);
    }
    
    public void query(RoutingContext ctx) {
        final QueryParams params = params(ctx);

        vertx().executeBlocking(() -> {
            List<Address> addresss = addressBO.viewAll(user(ctx), params);
            List<Object> rows = (List)addresss;

            ItemList itemList = build(ctx.normalizedPath(), params, rows);
            return itemList;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
}
