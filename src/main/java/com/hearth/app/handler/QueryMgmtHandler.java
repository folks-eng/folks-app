package com.hearth.app.handler;

import com.hearth.app.bo.QueryBO;
import com.hearth.app.model.AnalyticReq;
import com.hearth.app.model.AnalyticRes;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import org.javalabs.decl.util.MapperUtil;

/**
 *
 * @author sudip
 */
public class QueryMgmtHandler extends AbstractHandler {
    
    private final QueryBO queryBO;
    
    public QueryMgmtHandler(Vertx vertx) {
        super(vertx);
        this.queryBO = new QueryBO();
    }
    
    public void query(RoutingContext ctx) {
        vertx().executeBlocking(() -> {
            AnalyticReq req = MapperUtil.decode(ctx.body().buffer().getBytes(), AnalyticReq.class);
            AnalyticRes res = queryBO.execute(user(ctx), req);

            return res;
            
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
