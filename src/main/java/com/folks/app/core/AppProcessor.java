package com.folks.app.core;

import io.vertx.core.AbstractVerticle;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AppProcessor extends AbstractVerticle {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppProcessor.class);

    // Name of this verticle.
    private static final String NAME = "AppProcessor";
    
    protected final List<Long> timerIds = new ArrayList<>(2);
    
    @Override
    public void start() throws Exception {
        initEventBus();
        initTimer();
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Started Verticle: {}", NAME);
        }
    }
    
    /**
     * Initialize the event bus consumer.
     */
    private void initEventBus() {
        // ToDo
    }
    
    private void initTimer() {
        long delay = 0L;
        long interval = 30 * 60 * 1000L;
        
        Long timerId = getVertx().setPeriodic(delay, interval, new DefaultTimer());
        timerIds.add(timerId);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scheduled default timer. Initial Delay: {}. Pause Time (ms): {}", delay, interval);
        }
    }

    @Override
    public void stop() throws Exception {
        for (Long timerId : timerIds) {
            vertx.cancelTimer(timerId);
        }
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Stopped worker verticle {}", NAME);
        }
    }
}
