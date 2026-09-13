package com.folks.app.handler;

import com.folks.app.bo.AdminOpsBO;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;

/**
 *
 * @author sudip
 */
public class AdminOpsHandler extends AbstractHandler {
    
    private final AdminOpsBO adminBO;
    
    public AdminOpsHandler(Vertx vertx) {
        super(vertx);
        this.adminBO = new AdminOpsBO();
    }
    
    public void approveProfessional(RoutingContext ctx) {
        // Approve the application.
    }
    
    /**
     * Create a new resource element in the system.
     * 
     * <p>
     * The newly created resource is stored in the memory. If you intend to use a database, the 
     * {@link Vertx#executeBlocking(java.util.concurrent.Callable, io.vertx.core.Handler) } will ensure the
     * request is processed in a non-blocking fashion.
     * 
     * <p>
     * The <code>COUNTER</code> will create a unique id to identify the element.
     * 
     * @param ctx   Vertx {@link RoutingContext} object.
     */
    public void genAvailability(RoutingContext ctx) {
        // If you use a remote store, this method will safely execute the blocking code.
        vertx().executeBlocking(() -> {
            Map<String, Object> payload = MapperUtil.decode(ctx.body().buffer().getBytes(), HashMap.class);
            AvailabilityGenEvent event = AvailabilityGenEvent.from(payload);
            
            if (event.getNumberOfDays() <= 0 || event.getNumberOfDays() > 5) {
                throw new IllegalArgumentException("Invalid number of days specified. Must be between 1 and 5");
            }
            vertx().eventBus().send(Constants.AVAIL_GEN_ADDRESS, event);
            
            ServerMessage msg = new ServerMessage();
            msg.setCode(HttpURLConnection.HTTP_ACCEPTED);
            msg.setMessage("Your request to generate availability has been accepted. Allow us 1 minute to work on it");
            
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
