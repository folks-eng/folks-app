package com.folks.app.event;

import com.folks.app.bo.AvailabilityMgmtBO;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AvailabilityEventConsumer implements Handler<Message<AvailabilityGenEvent>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityEventConsumer.class);
    
    private final AvailabilityMgmtBO availMgmtBO;

    public AvailabilityEventConsumer() {
        this.availMgmtBO = new AvailabilityMgmtBO();
    }

    @Override
    public void handle(Message<AvailabilityGenEvent> event) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Received availability generation event. Event: {}", event.body());
            }
            AvailabilityGenEvent availEvent = event.body();
            availMgmtBO.generate(availEvent);
        }
        catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
        }
    }
    
}
