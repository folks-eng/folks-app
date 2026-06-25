package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Availability;
import com.folks.app.util.SearchCriteria;
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
public class AvailabilityDAOImpl implements AvailabilityDAO {
    
    private final String TABLE = "fks_availability";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Availability record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Availability> records) {
        for (Availability record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Availability record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Availability> records) {
        for (Availability record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Availability record) {
        em.remove(record);
    }

    @Override
    public Availability find(Availability.AvailabilityPK pk) {
        return em.find(Availability.class, pk);
    }

    @Override
    public List<Availability> query(SearchCriteria search) {
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

        TypedQuery q = em.createNativeQuery(query.toQuery(), Availability.class);
        List<Object> binds = query.params();
        
        idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Availability> result = q.getResultList();
        return result;
    }
    
}
