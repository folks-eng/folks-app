package com.folks.app.core;

import com.folks.app.bo.BookingBO;
import com.folks.app.model.Booking;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class BookingEventConsumer implements Handler<Message<Booking>> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingEventConsumer.class);
    
    private final BookingBO bookingBO;

    public BookingEventConsumer() {
        this.bookingBO = new BookingBO();
    }

    @Override
    public void handle(Message<Booking> event) {
        // Now find and assign a professional.
        Booking booking = event.body();
        if (booking.getStatus() == Booking.Status.PENDING) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Received new booking creation event. Booking id: {}", event.body().getBookingId());
            }
            assignProfessional(booking);
        }
        else if (booking.getStatus() == Booking.Status.CANCELLED) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Received booking cancellation event. Booking id: {}", event.body().getBookingId());
            }
            freeProfessional(booking);
        }
    }

    private void assignProfessional(Booking booking) {
        bookingBO.assignProfessional(booking);
    }

    private void freeProfessional(Booking booking) {
        bookingBO.freeProfessional(booking);
    }
    
}
