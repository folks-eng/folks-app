package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.BookingDAO;
import com.folks.app.model.Booking;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class BookingBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingBO.class);
    
    private final BookingDAO bookingDAO;

    public BookingBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. BookingDAO: {}", getClass().getSimpleName(), bookingDAO);
        }
    }

    public Booking create(AppUser usr, Booking booking) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        bookingDAO.insert(booking);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Booking created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return booking;
    }

    public void create(AppUser usr, List<Booking> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (Booking booking : records) {
            if (booking.getCreatedAt() == null) {
                booking.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        bookingDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Booking record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Booking modify(AppUser usr, Booking booking) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Booking existing = bookingDAO.find(new Booking.BookingPK(booking.getBookingId()));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + booking.getBookingId());
        }
        // Update attributes of existing record
        existing.setCustomerId(booking.getCustomerId());
        existing.setProfessionalId(booking.getProfessionalId());
        existing.setServiceId(booking.getServiceId());
        existing.setAddressId(booking.getAddressId());
        existing.setScheduledAt(booking.getScheduledAt());
        existing.setStatus(booking.getStatus());
        existing.setTotalAmount(booking.getTotalAmount());

        bookingDAO.update(booking);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Booking record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Booking> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Booking> rows = bookingDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded booking record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Booking view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Booking booking = bookingDAO.find(new Booking.BookingPK(id));
        if (booking == null) {
            throw new IllegalArgumentException("No Booking found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched booking details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return booking;
    }

    public Booking remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Booking booking = bookingDAO.find(new Booking.BookingPK(id));

        if (booking == null) {
            throw new IllegalArgumentException("No booking found for id: " + id);
        }
        bookingDAO.delete(booking);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Booking. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return booking;
    }
}
