package com.folks.app.core;

import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.event.AvailabilityEventConsumer;
import com.folks.app.event.AvailabilityGenCodec;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.event.BookingEventConsumer;
import com.folks.app.event.BookingCodec;
import com.folks.app.event.ProfessionalEventConsumer;
import com.folks.app.event.ProfessionalRegCodec;
import com.folks.app.event.ProfessionalRegEvent;
import com.folks.app.model.Booking;
import com.folks.app.util.Constants;
import io.vertx.core.AbstractVerticle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        getVertx().eventBus().consumer(Constants.BOOKING_ADDRESS, new BookingEventConsumer());
        getVertx().eventBus().registerDefaultCodec(Booking.class, new BookingCodec());
        
        getVertx().eventBus().consumer(Constants.AVAIL_GEN_ADDRESS, new AvailabilityEventConsumer());
        getVertx().eventBus().registerDefaultCodec(AvailabilityGenEvent.class, new AvailabilityGenCodec());
        
        getVertx().eventBus().consumer(Constants.PROF_REG_ADDRESS, new ProfessionalEventConsumer());
        getVertx().eventBus().registerDefaultCodec(ProfessionalRegEvent.class, new ProfessionalRegCodec());
        
    }
    
    private void initTimer() {
        Map<String, Object> config = ApplicationConfiguration.getInstance().get("timer.config");
        if (config == null) {
            config = new HashMap<>();
        }
        
        long delay = 0L;
        long interval = (Integer)config.getOrDefault("booking.timer.interval.s", 60);
        
        Long timerId = getVertx().setPeriodic(delay, interval * 1000L, new BookingTimer());
        timerIds.add(timerId);
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scheduled booking timer. Initial Delay: {}. Pause Time (s): {}", delay, interval);
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
