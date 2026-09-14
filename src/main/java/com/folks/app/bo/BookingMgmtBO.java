package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.dao.AddressDAO;
import com.folks.app.dao.BookingDAO;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class BookingMgmtBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingMgmtBO.class);
    
    private final BookingDAO bookingDAO;
    private final AddressDAO addressDAO;
    
    public BookingMgmtBO() {
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized BookingBO: {}. BookingDAO: {}. UserDAO: {}, AddressDAO: {}."
                    , getClass().getSimpleName(), bookingDAO, userDAO, addressDAO);
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
