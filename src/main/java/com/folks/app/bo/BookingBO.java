package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.AddressDAO;
import com.folks.app.dao.BookingDAO;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.model.Professional;
import com.folks.app.model.User;
import com.folks.app.util.IdGenerator;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class BookingBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingBO.class);
    
    private final BookingDAO bookingDAO;
    private final AddressDAO addressDAO;
    private final ProfessionalDAO professionalDAO;
    
    public BookingBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized BookingBO: {}. BookingDAO: {}. UserDAO: {}, AddressDAO: {}. ProfessionalDAO: {}"
                    , getClass().getSimpleName(), bookingDAO, userDAO, addressDAO, professionalDAO);
        }
    }

    public Booking create(AppUser usr, Booking booking) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user.
        User user = fetchUser(usr);

        booking.setCustomerId(user.getUserId());
        booking.setProfessionalId(-1);              // A dummy professional id. Professional will be added later a cron job
        booking.setStatus(Booking.Status.PENDING);
        
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        // Booking id must be unique
        booking.setBookingId(IdGenerator.generate(
                String.valueOf(booking.getCustomerId())
                , String.valueOf(booking.getServiceId())
                , String.valueOf(booking.getAddressId())
                , String.valueOf(booking.getScheduledAt())
                , String.valueOf(booking.getTimeSlot())));
        
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

        // Fetch the user.
        User user = fetchUser(usr);

        for (Booking booking : records) {
            booking.setCustomerId(user.getUserId());
            booking.setProfessionalId(-1);              // A dummy professional id. Professional will be added later a cron job
            booking.setStatus(Booking.Status.PENDING);
            if (booking.getCreatedAt() == null) {
                booking.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }

            booking.setBookingId(IdGenerator.generate(
                    String.valueOf(booking.getCustomerId())
                    , String.valueOf(booking.getServiceId())
                    , String.valueOf(booking.getAddressId())
                    , String.valueOf(booking.getScheduledAt())
                    , String.valueOf(booking.getTimeSlot())));
        }
        bookingDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Booking record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Booking modify(AppUser usr, Booking booking) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user.
        User user = fetchUser(usr);

        // First fetch the entry, to see if this already exists.
        Booking existing = bookingDAO.find(new Booking.BookingPK(booking.getBookingId()));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + booking.getBookingId());
        }
        if (existing.getStatus() == Booking.Status.CONFIRMED) {
            throw new IllegalArgumentException("Cannot modify a booking once it is confirmed and professional is assigned");
        }
        if (! existing.getCustomerId().equals(user.getUserId())) {
            throw new IllegalAccessException("You do not have permission to modify this booking");
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
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        existing.setUpdatedBy(user.getUserId() + " - " + user.getFullName());

        bookingDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Booking record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }
    
    public Booking patch(AppUser usr, Booking booking) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the professional.
        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null) {
            throw new IllegalArgumentException("No professional found for id: " + usr.principal().sub());
        }
        
        // First fetch the entry, to see if this already exists.
        Booking current = bookingDAO.find(new Booking.BookingPK(booking.getBookingId()));
        if (current == null) {
            throw new IllegalArgumentException("No booking found for id: " + booking.getBookingId());
        }
        if (! current.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("You do not have permission to update status of this booking");
        }
        
        // Check the status. It should be updated sequentially.
        if (booking.getStatus() == null 
                || (current.getStatus() == Booking.Status.PENDING && booking.getStatus() != Booking.Status.IN_PROGRESS)
                || (current.getStatus() == Booking.Status.IN_PROGRESS && booking.getStatus() != Booking.Status.COMPLETED)) {
            
            throw new IllegalArgumentException("Invalid status specified");
        }
    
        // This API is supposed to be called by the professional to update the status, when ...
        // 1. The work is started => IN_PROGRESS
        // 2. The work is completed => COMPLETED
        booking.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        booking.setUpdatedBy(professional.getProfessionalId() + " - " + professional.getUser().getFullName());
        bookingDAO.updateStatus(booking);
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Updated status to {} for booking {}. Elapsed time(ms): {}"
                    , booking.getStatus(), booking.getBookingId(), timer.elapsedTimeMillis());
        }
        return booking;
    }

    public List<Booking> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user.
        User user = fetchUser(usr);
        if (user == null) {
            throw new ResourceNotFoundException("No user found for " + usr.principal().sub());
        }
        String field = null;
        
        if (user.getRole() == User.Role.CUSTOMER) {
            field = "customerId";
        }
        else if (user.getRole() == User.Role.PROFESSIONAL) {
            field = "professionalId";
        }

        // We need to fetch the bookings for the current user only.
        SearchCriteria search = SearchCriteria.from(params, field, user.getUserId());
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

    public Booking remove(AppUser usr, String id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // We will not delete the record, instead it will be marked as CANCELLED
        // bookingDAO.delete(booking);
        
        Booking existing = bookingDAO.find(new Booking.BookingPK(id));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + id);
        }
        
        // Fetch the user.
        User user = fetchUser(usr);
        if (! existing.getCustomerId().equals(user.getUserId())) {
            throw new IllegalAccessException("You do not have permission to cancel this booking");
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
        
        // Find the neighbourhood
        Address address = addressDAO.find(new Address.AddressPK(booking.getAddressId()));
        if (address == null) {
            throw new IllegalArgumentException("No address found for identifier: " + booking.getAddressId()
                    + " that is associated with booking: " + booking.getBookingId());
        }
        booking.setNeighbourhoodId(address.getNeighbourhoodId());
        
        // All good. Proceed with assign professional ...
        Boolean flag = bookingDAO.assignProfessional(booking);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            if (flag) {
                LOGGER.info("Successfully assigned professional {} to booking {}. Elapsed time(ms): {}"
                        , booking.getProfessionalId(), booking.getBookingId(), timer.elapsedTimeMillis());
            }
            else {
                LOGGER.info("Unable to assign any professional to booking {}. Will assign later. Elapsed time(ms): {}"
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
                        + "-" + String.format("%02d", cal.get(Calendar.MONTH) + 1)
                        + "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        
        String start = booking.getTimeSlot().split(" - ")[0];
        String end = booking.getTimeSlot().split(" - ")[1];
        
        Map<String, List<String>> map = new HashMap<>();
        map.put("professionalId", List.of(String.valueOf(booking.getProfessionalId())));
        map.put("date", List.of(date));
        map.put("startTime", List.of(start + ":00"));
        map.put("endTime", List.of(end + ":00"));
        
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
    
    public List<Booking> pendingBookings() {
        return bookingDAO.pendingBooking();
    }
}
