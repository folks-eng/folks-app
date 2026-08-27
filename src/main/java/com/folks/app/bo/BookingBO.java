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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        
        // Fetch the user.
        User user = fetchUser(usr);

        booking.setBookingId(UUID.randomUUID().toString());
        booking.setCustomerId(user.getUserId());
        booking.setProfessionalId(-1);              // A dummy professional id. Professional will be added later a cron job
        booking.setStatus(Booking.Status.PENDING);
        
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
        if (existing.getStatus() == Booking.Status.CONFIRMED) {
            throw new IllegalArgumentException("Cannot modify a booking once it is confirmed and professional is assigned");
        }

        // Update attributes of existing record
        existing.setCustomerId(booking.getCustomerId());
        existing.setProfessionalId(booking.getProfessionalId());
        existing.setServiceId(booking.getServiceId());
        existing.setAddressId(booking.getAddressId());
        existing.setScheduledAt(booking.getScheduledAt());
        existing.setTimeSlot(booking.getTimeSlot());
        existing.setStatus(booking.getStatus());
        existing.setTotalAmount(booking.getTotalAmount());

        bookingDAO.update(existing);
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
        List<Booking> bookings = bookingDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded booking record(s). Elapsed time(ms): {}", bookings.size(), timer.elapsedTimeMillis());
        }
        return bookings;
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

        // We will not delete the record, instead it will be marked as CANCELLED
        // bookingDAO.delete(booking);
        
        Booking existing = bookingDAO.find(new Booking.BookingPK(id));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + id);
        }
        // if (existing.getStatus() == Booking.Status.CONFIRMED) {
        //     throw new IllegalArgumentException("Cannot modify a booking once it is confirmed and professional is assigned");
        // }
        
        existing.setStatus(Booking.Status.CANCELLED);
        existing.setStatusMsg("Cancelled by user");
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        bookingDAO.update(existing);
        
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Cancelled Booking. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return existing;
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
    
    public void assignProfessional(Booking booking) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Booking existing = bookingDAO.find(new Booking.BookingPK(booking.getBookingId()));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + booking.getBookingId());
        }
        if (existing.getStatus() != Booking.Status.PENDING) {
            throw new IllegalArgumentException("Cannot add a professional to a booking which is already " + existing.getStatus());
        }
        Boolean flag = bookingDAO.assignProfessional(booking);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            if (flag) {
                LOGGER.info("Successfully assigned professional {} to booking {}. Elapsed time(ms): {}"
                        , booking.getProfessionalId(), booking.getBookingId(), timer.elapsedTimeMillis());
            }
            else {
                LOGGER.info("Unable to assign any professional to booking {}. Elapsed time(ms): {}"
                        , booking.getBookingId(), timer.elapsedTimeMillis());
            }
        }
    }
    
    public void freeProfessional(Booking booking) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.getScheduledAt());
        String date = String.valueOf(cal.get(Calendar.YEAR))
                        + "-" + String.format("%02d", cal.get(Calendar.MONTH + 1))
                        + "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        
        String start = booking.getTimeSlot().split(" - ")[0];
        String end = booking.getTimeSlot().split(" - ")[1];
        
        Map<String, List<String>> map = new HashMap<>();
        map.put("professionalId", List.of(String.valueOf(booking.getProfessionalId())));
        map.put("date", List.of(date));
        map.put("startTime", List.of(start));
        map.put("endTime", List.of(end));
        
        SearchCriteria search = SearchCriteria.from(new QueryParams(map));
        Boolean flag = bookingDAO.freeProfessional(booking, search);
        
        timer.stop();
        
        if (flag) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Professional {} freed-up succesfully from booking {}. Elapsed time(ms): {}"
                        , booking.getProfessionalId(), booking.getBookingId(), timer.elapsedTimeMillis());
            }
        }
        else {
            LOGGER.warn("Inconsistent data in database. Cannot free-up professional {} from booking {}"
                    , booking.getProfessionalId(), booking.getBookingId());
        }
    }
}
