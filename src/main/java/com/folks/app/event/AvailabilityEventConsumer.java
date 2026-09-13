package com.folks.app.event;

import com.folks.app.bo.AvailabilityGenBO;
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
    
    private final AvailabilityGenBO availGenBO;

    public AvailabilityEventConsumer() {
        this.availGenBO = new AvailabilityGenBO();
    }

    @Override
    public void handle(Message<AvailabilityGenEvent> event) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Received availability generation event. Event: {}", event.body());
        }
        AvailabilityGenEvent availEvent = event.body();
        availGenBO.generate(availEvent);
    }
    
}
