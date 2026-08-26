package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.AddressDAO;
import com.folks.app.dao.BookingDAO;
import com.folks.app.dao.ServiceDAO;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final AddressDAO addressDAO;
    private final ServiceDAO serviceDAO;

    public BookingBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        this.userDAO = DAOProxy.get(UserDAO.class);
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
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

    public Booking patch(AppUser usr, Booking booking) {
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

        // Only the following attributes are allowed to be updated.
        if (booking.getAddressId() != null) {
            existing.setAddressId(booking.getAddressId());
        }
        if (booking.getScheduledAt() != null) {
            existing.setScheduledAt(booking.getScheduledAt());
        }
        if (booking.getTimeSlot() != null) {
            existing.setTimeSlot(booking.getTimeSlot());
        }
        if (booking.getPaymentMethod() != null) {
            existing.setPaymentMethod(booking.getPaymentMethod());
        }
        if (booking.getProfessionalId() != null) {
            existing.setProfessionalId(booking.getProfessionalId());
        }
        if (booking.getStatus() != null) {
            existing.setStatus(booking.getStatus());
        }
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        
        bookingDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Booking record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }
    
    public void assignProfessional(Booking booking) {
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

    public List<Booking> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user.
        User user = fetchUser(usr);

        // We need to fetch the addresses for the current user only.
        SearchCriteria search = SearchCriteria.from(params, "customerId", user.getUserId());
        List<Booking> bookings = bookingDAO.query(search);
        
        // Fetch addresses (In future, it will be fetched from in-memory cache)
        Set<Integer> addressIds = new HashSet<>();
        for (Booking booking : bookings) {
            addressIds.add(booking.getAddressId());
        }
        List<Address> addresses = addressDAO.find(new ArrayList<>(addressIds));
        for (Address address : addresses) {
            for (Booking booking : bookings) {
                if (address.getAddressId().equals(booking.getAddressId())) {
                    booking.setAddress(String.join(" "
                            , address.getAddressLine1()
                            , address.getAddressLine2()
                            , address.getCity()
                            , String.valueOf(address.getPincode())));
                }
            }
        }
        
        // Fetch services (In future, it will be fetched from in-memory cache)
        Set<Integer> serviceIds = new HashSet<>();
        for (Booking booking : bookings) {
            serviceIds.add(booking.getServiceId());
        }
        List<Service> services = serviceDAO.find(new ArrayList<>(serviceIds));
        for (Service service : services) {
            for (Booking booking : bookings) {
                if (service.getServiceId().equals(booking.getServiceId())) {
                    booking.setServiceName(service.getName());
                }
            }
        }

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
