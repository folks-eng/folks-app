package io.opns.app.dao;

import org.javalabs.jpa.query.Criteria;
import io.opns.app.model.Payment;
import io.opns.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class PaymentDAOImpl implements PaymentDAO {
    
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
        Criteria query = new Criteria()
                .select(Arrays.asList("*"))
                .from(TABLE);

        int idx = 0;
        for (Map.Entry<String, List<String>> me : search.params().entrySet()) {
            String col = me.getKey();
            List<String> vals = me.getValue();
            if (vals.isEmpty()) {
                continue;
            }
            if (idx == 0) {
                if (vals.size() > 1) {
                    query.where(col).in(vals);
                }
                else {
                    query.where(col).eq(vals.get(0));
                }
                idx ++;
            }
            else {
                if (vals.size() > 1) {
                    query.and(col).in(vals);
                }
                else {
                    query.and(col).eq(vals.get(0));
                }
            }
        }
        if (search.orderBy() != null) {
            query.orderBy(search.orderBy());
        }
        if (! search.asc()) {
            query.desc();
        }

        TypedQuery q = em.createNativeQuery(query.toQuery(), Payment.class);
        List<Object> binds = query.params();
        
        idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Payment> result = q.getResultList();
        return result;
    }
    
}
