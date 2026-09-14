package com.folks.app.handler;

import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;
import com.folks.app.bo.AvailabilityMgmtBO;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.model.Availability;
import com.folks.app.model.ItemList;
import com.folks.app.util.Constants;
import com.folks.app.util.QueryParams;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example REST handler.
 * 
 * <p>
 * This handler class is designed to handle asynchronous events, such as incoming network requests,
 * database responses, or other events within your application, allowing you to process data and respond
 * accordingly without blocking the main event loop, making your application highly scalable and reactive.
 * 
 * <p>
 * Refer to the <code>routing-config.xml</code> to understand the url mapping.
 */
public class AvailabilityMgmtHandler extends AbstractHandler {
    
    private final AvailabilityMgmtBO availMgmtBO;
    
    public AvailabilityMgmtHandler(Vertx vertx) {
        super(vertx);
        this.availMgmtBO = new AvailabilityMgmtBO();
    }

    public void viewProfAvailability(RoutingContext ctx) {
        final QueryParams params = params(ctx);

        vertx().executeBlocking(() -> {
            List<Availability> availabilitys = availMgmtBO.viewProfessionalAvailability(user(ctx), params);
            List<Object> rows = (List)availabilitys;

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

    public void generate(RoutingContext ctx) {
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            Map<String, Object> payload = MapperUtil.decode(ctx.body().buffer().getBytes(), HashMap.class);
            
            // Send message to message bus to add professional
            vertx().eventBus().send(Constants.AVAIL_GEN_ADDRESS, AvailabilityGenEvent.from(payload));
            
            ServerMessage msg = new ServerMessage();
            msg.setCode(HttpURLConnection.HTTP_ACCEPTED);
            msg.setMessage("Your request to generate professional calendar has been accepted."
                    + " Allow us a minute to work on this");
            
            return msg;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_ACCEPTED, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
}
