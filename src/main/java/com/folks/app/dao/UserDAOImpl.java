package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.User;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.javalabs.jpa.util.QueryHints;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class UserDAOImpl implements UserDAO {
    
    private final String TABLE = "fks_users";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(User record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<User> records) {
        for (User record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(User record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<User> records) {
        for (User record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(User record) {
        em.remove(record);
    }

    @Override
    public User find(User.UserPK pk) {
        return em.find(User.class, pk);
    }

    @Override
    public User select(String externalId) {
        return em.createNamedQuery("User.selectByExtId", User.class)
            .setParameter(1, externalId)
            .setHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE)
            .getSingleResult();
    }

    @Override
    public List<User> query(SearchCriteria search) {
        Criteria query = new Criteria()
                .select(Arrays.asList("*"))
                .from(TABLE);

        int idx = 0;
        for (Map.Entry<String, List<Object>> me : search.params().entrySet()) {
            String col = me.getKey();
            List<Object> vals = me.getValue();
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

        TypedQuery q = em.createNativeQuery(query.toQuery(), User.class);
        List<Object> binds = query.params();
        
        idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<User> result = q.getResultList();
        return result;
    }
    
}
