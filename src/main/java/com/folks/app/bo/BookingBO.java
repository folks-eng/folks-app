package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.BookingDAO;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.Booking;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class BookingBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingBO.class);
    
    private final BookingDAO bookingDAO;
    private final UserDAO userDAO;

    public BookingBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        this.userDAO = DAOProxy.get(UserDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized BookingBO: {}. BookingDAO: {}. UserDAO: {}", getClass().getSimpleName(), bookingDAO, userDAO);
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
        
        // Fetch the user.
        User user = fetchUser(usr);

        // We need to fetch the addresses for the current user only.
        SearchCriteria search = SearchCriteria.from(params, "customerId", user.getUserId());
        List<Booking> rows = bookingDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded booking record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Booking view(AppUser usr, String id) {
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

    public Booking remove(AppUser usr, String id) {
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
    
    /**
     * Retrieves the user associated with the authenticated application user.
     *
     * <p>
     * The user's external identifier is obtained from the {@code sub} claim of the JWT principal and is used to
     * query the user data store.
     *
     * <p>
     * The user may first be looked up from a distributed cache to avoid an * unnecessary database query.
     * If the user is not available in the cache, the persistent data store is queried as a fallback.
     *
     * @param usr   The authenticated application user containing the JWT principal
     * @return User The user associated with the external identifier
     *
     * @throws IllegalArgumentException if no user exists for the external identifier
     */
    private User fetchUser(AppUser usr) {
        try {
            // Query the user based on external_id.
            // external_id will be part of jwt token as 'sub'.
            String extId = usr.principal().sub();
            return userDAO.select(extId);
        }
        catch (NoResultException e) {
            throw new IllegalArgumentException("No User found for id: " + usr.principal().sub());
        }
    }
}
