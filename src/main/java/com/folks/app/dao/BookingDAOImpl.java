package com.folks.app.dao;

import com.folks.app.model.Availability;
import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Booking;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
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
    
    @Override
    public Boolean assignProfessional(Booking booking) {
        Date date = booking.getScheduledAt();
        String start = booking.getTimeSlot().split(" - ")[0];
        String end = booking.getTimeSlot().split(" - ")[1];
        
        List<Availability> availabilities = availabilityDAO.findProfessional(
                booking.getServiceId()
                , date.toString()
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
    
}
