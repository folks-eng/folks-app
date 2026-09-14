package com.folks.app.event;

import com.folks.app.bo.ProfessionalMgmtBO;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalEventConsumer implements Handler<Message<ProfessionalRegEvent>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalEventConsumer.class);
    
    private final ProfessionalMgmtBO profMgmtBO;

    public ProfessionalEventConsumer() {
        this.profMgmtBO = new ProfessionalMgmtBO();
    }

    @Override
    public void handle(Message<ProfessionalRegEvent> event) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Received professional registration event. Event: {}", event.body());
        }
        ProfessionalRegEvent profEvent = event.body();
        profMgmtBO.register(profEvent.getPropfProfile());
    }
    
}
