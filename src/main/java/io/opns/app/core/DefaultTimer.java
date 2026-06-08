package io.opns.app.core;

import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default timer of the application.
 *
 * @author schan280
 */
public class DefaultTimer implements Handler<Long> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTimer.class);

    @Override
    public void handle(Long event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time {} is invoked", getClass().getSimpleName());
        }
        // Add repetitive task ...
    }
    
}
