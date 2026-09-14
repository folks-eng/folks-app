package com.folks.app.core;

import com.folks.app.bo.BookingMgmtBO;
import com.folks.app.model.Booking;
import io.vertx.core.Handler;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Booking timer of the application.
 *
 * @author schan280
 */
public class BookingTimer implements Handler<Long> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingTimer.class);

    private final BookingMgmtBO bookingMgmtBO;

    public BookingTimer() {
        this.bookingMgmtBO = new BookingMgmtBO();
    }
    
    @Override
    public void handle(Long event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Time {} is invoked", getClass().getSimpleName());
        }
        // Add repetitive task ...
        List<Booking> bookings = bookingMgmtBO.pendingBookings();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} pending booking(s)", bookings.size());
        }
        for (Booking booking : bookings) {
            booking.setUpdatedBy("System - BookingTimer");
            bookingMgmtBO.assignProfessional(booking);
        }
    }
    
}
