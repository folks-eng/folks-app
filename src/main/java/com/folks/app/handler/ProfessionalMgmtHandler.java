package com.folks.app.handler;

import com.folks.app.bo.ProfessionalMgmtBO;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.model.Professional;
import com.folks.app.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;

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
public class ProfessionalMgmtHandler extends AbstractHandler {
    
    private final ProfessionalMgmtBO professionalMgmtBO;
    
    public ProfessionalMgmtHandler(Vertx vertx) {
        super(vertx);
        this.professionalMgmtBO = new ProfessionalMgmtBO();
    }

    public void approve(RoutingContext ctx) {
        vertx().executeBlocking(() -> {
            Map<String, String> payload = MapperUtil.decode(ctx.body().buffer().getBytes(), HashMap.class);
            Professional professional = professionalMgmtBO.approveProfessional(user(ctx), payload);
            
            // Send message to message bus to add professional
            Map<String, Object> map = Map.of("professionalId", professional.getProfessionalId(), "numberOfDays", 5);
            vertx().eventBus().send(Constants.AVAIL_GEN_ADDRESS, AvailabilityGenEvent.from(map));
            
            return new ServerMessage(HttpURLConnection.HTTP_OK, "Professional has been approved");
            
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
