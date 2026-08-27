package com.folks.app.dao;

import com.folks.app.model.Availability;
import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Booking;
import com.folks.app.model.Payment;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.jpa.annotation.Dao;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class BookingDAOImpl extends AbstractDAO implements BookingDAO {
    
    private final String TABLE = "fks_bookings";
    
    @Dao
    private AvailabilityDAO availabilityDAO;
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Booking record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Booking> records) {
        for (Booking record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Booking record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Booking> records) {
        for (Booking record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Booking record) {
        em.remove(record);
    }

    @Override
    public Booking find(Booking.BookingPK pk) {
        return em.find(Booking.class, pk);
    }

    @Override
    public List<Booking> query(SearchCriteria search) {
        if (search.fetchDependency()) {
            return expandedQuery(search);
        }
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Booking.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Booking> result = q.getResultList();
        return result;
    }
    
    private List<Booking> expandedQuery(SearchCriteria search) {
        String query = """
                SELECT a.booking_id
                        , a.scheduled_at
                        , a.time_slot
                        , a.status
                        , a.payment_method
                        , a.total_amount
                        , a.created_at
                        , b.service_id
                        , b.name
                        , c.address_line1
                        , c.address_line2
                        , c.city
                        , c.pincode
                        , a.professional_id
                        , COALESCE(e.full_name, 'Professional not assigned') AS professional_name
                        , COALESCE(e.phone1, '') AS phone1
                  FROM fks_bookings a
                 INNER JOIN fks_services b ON (a.service_id = b.service_id)
                 INNER JOIN fks_addresses c ON (a.address_id = c.address_id)
                 LEFT OUTER JOIN fks_professionals d ON (a.professional_id = d.professional_id)
                 LEFT OUTER JOIN fks_users e ON (d.user_id = e.user_id AND e.role = ?)
                 WHERE a.customer_id = ?
                 ORDER BY a.created_at DESC;""";
        
        List<Object> val = search.params().get("customer_id");
        
        Query q = em.createNativeQuery(query);
        q.setParameter(1, "PROFESSIONAL");
        q.setParameter(2, val.get(0));
        
        List<Object[]> rows = q.getResultList();
        
        List<Booking> bookings = new ArrayList<>(rows.size());
        for (Object[] row : rows) {
            Booking booking = new Booking();
            booking.setBookingId((String)row[0]);
            booking.setScheduledAt((Timestamp)row[1]);
            booking.setTimeSlot((String)row[2]);
            booking.setStatus(Enum.valueOf(Booking.Status.class, (String)row[3]));
            booking.setPaymentMethod(Enum.valueOf(Payment.Paymentmethod.class, (String)row[4]));
            booking.setTotalAmount((Double)row[5]);
            booking.setCreatedAt((Timestamp)row[6]);
            booking.setServiceId((Integer)row[7]);
            booking.setServiceName((String)row[8]);
            booking.setAddress(String.join(" ", (String)row[9], (String)row[10], (String)row[11], String.valueOf((Integer)row[12])));
            booking.setProfessionalId((Integer)row[13]);
            booking.setProfessionalName((String)row[14]);
            booking.setProfessionalContact((String)row[15]);
            
            bookings.add(booking);
        }
        return bookings;
    }
    
    @Override
    public Boolean assignProfessional(Booking booking) {
        Timestamp date = booking.getScheduledAt();
        String start = booking.getTimeSlot().split(" - ")[0];
        String end = booking.getTimeSlot().split(" - ")[1];
        
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        
        List<Availability> availabilities = availabilityDAO.findProfessional(
                booking.getServiceId()
                , String.valueOf(cal.get(Calendar.YEAR))
                        + "-" + String.format("%02d", (cal.get(Calendar.MONTH) + 1))
                        + "-" + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH))
                , start
                , end);
        
        if (! availabilities.isEmpty()) {
            booking.setProfessionalId(availabilities.get(0).getProfessionalId());
            booking.setStatus(Booking.Status.CONFIRMED);
            booking.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            em.merge(booking);

            for (Availability availability : availabilities) {
                availability.setIsBooked((short)1);
            }
            availabilityDAO.update(availabilities);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Override
    public Boolean freeProfessional(Booking booking, SearchCriteria search) {
        List<Availability> availabilities = availabilityDAO.query(search);
        
        if (! availabilities.isEmpty()) {
            for (Availability availability : availabilities) {
                availability.setIsBooked((short)0);
            }
            availabilityDAO.update(availabilities);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
}
