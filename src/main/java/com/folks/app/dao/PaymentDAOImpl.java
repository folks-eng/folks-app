package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Payment;
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
public class PaymentDAOImpl extends AbstractDAO implements PaymentDAO {
    
    private final String TABLE = "fks_payments";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Payment record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Payment> records) {
        for (Payment record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Payment record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Payment> records) {
        for (Payment record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Payment record) {
        em.remove(record);
    }

    @Override
    public Payment find(Payment.PaymentPK pk) {
        return em.find(Payment.class, pk);
    }

    @Override
    public List<Payment> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Payment.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Payment> result = q.getResultList();
        return result;
    }
    
}
