package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.User;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import org.javalabs.jpa.util.QueryHints;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class UserDAOImpl extends AbstractDAO implements UserDAO {
    
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
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), User.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<User> result = q.getResultList();
        return result;
    }
    
}
