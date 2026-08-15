package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Booking;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class BookingDAOImpl extends AbstractDAO implements BookingDAO {
    
    private final String TABLE = "fks_bookings";
    
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
    
}
