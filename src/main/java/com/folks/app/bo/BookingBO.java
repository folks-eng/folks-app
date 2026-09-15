package com.folks.app.bo;

import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.model.Address;
import com.folks.app.model.Professional;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.BookingDAO;
import com.folks.app.model.Booking;
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

    private final ProfessionalDAO professionalDAO;

    public BookingBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized BookingBO: {}. BookingDAO: {}. UserDAO: {}", getClass().getSimpleName(), bookingDAO, userDAO);
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

    // We need to fetch all the bookings for the current user only (both from customer and then professional).
    public List<Booking> viewAll(AppUser usr, QueryParams params) {
        LOGGER.debug("Start of BookingBO:viewAll");
        String extId = usr.principal().sub();
        List<Booking> bookings = null;
        SearchCriteria search = null;

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        // Fetch the user.
        User user = fetchUser(usr);
        if (user == null) {
            throw new ResourceNotFoundException("No user found for " + usr.principal().sub());
        }
        if (user.getRole() == User.Role.CUSTOMER) {
            search = SearchCriteria.from(params, "customerId", user.getUserId());
            //LOGGER.debug("Search params for CUSTOMER " +search.params());

            bookings = bookingDAO.query(search);
            LOGGER.debug("Fetched {} booking record(s) for Customer {}", bookings.size(), user.getUserId());
        }
        else if (user.getRole() == User.Role.PROFESSIONAL) {
            Professional prof = professionalDAO.findByExtId(extId);
            if (prof == null) {
                throw new ResourceNotFoundException("No professional found for " + usr.principal().sub());
            }
            else {
                search = SearchCriteria.from(params, "professionalId", prof.getProfessionalId());
               // LOGGER.debug("Search params for PROFESSIONAL " +search.params());

                bookings = bookingDAO.query(search);
                LOGGER.debug("Fetched {} booking record(s) for Professional {}", bookings.size(), user.getUserId());
            }
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded booking record(s). Elapsed time(ms): {}", bookings.size(), timer.elapsedTimeMillis());
        }
        LOGGER.debug("End of BookingBO:viewAll");
        return bookings;
    }

    // Both customer and who is owner of this booking is allowed to view.
    public Booking view(AppUser usr, String bookingId) throws IllegalAccessException {
        LOGGER.debug("Start of BookingBO:view");
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user entry.
        User requestingUser = fetchUser(usr);
        Booking booking = bookingDAO.find(new Booking.BookingPK(bookingId));
        if (booking == null) {
            throw new ResourceNotFoundException("No Booking found for id: " + bookingId);
        }
        // Check if this booking is associated with the customer.
        ensureAuthorized(booking, requestingUser);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched booking details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        LOGGER.debug("End of BookingBO:view");
        return booking;
    }

    //Both booking customer and professional linked can view the Booking.
    private void ensureAuthorized(Booking booking, User requestingUser) throws IllegalAccessException {

        if (requestingUser.getRole().equals(User.Role.CUSTOMER)) {
            LOGGER.debug("Viewing a single booking booked by customer  " +requestingUser.getUserId());
            if (! booking.getCustomerId().equals(requestingUser.getUserId()))
                throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
        else if (requestingUser.getRole().equals(User.Role.PROFESSIONAL)) {
            Professional prof = professionalDAO.findByExtId(requestingUser.getExternalId());
            LOGGER.debug("Viewing a single booking assigned to a professional " +prof.getProfessionalId());
            if(prof == null)
                throw new ResourceNotFoundException("Professional not found for the requesting user.");
            if (! booking.getProfessionalId().equals(prof.getProfessionalId()))
                throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
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

    public Booking remove(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // We will not delete the record, instead it will be marked as CANCELLED
        // bookingDAO.delete(booking);

        Booking existing = bookingDAO.find(new Booking.BookingPK(id));
        if (existing == null) {
            throw new IllegalArgumentException("No booking found for identifier: " + id);
        }
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
}
