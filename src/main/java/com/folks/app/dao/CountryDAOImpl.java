package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Country;
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
public class CountryDAOImpl extends AbstractDAO implements CountryDAO {
    
    private final String TABLE = "fks_countries";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Country record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Country> records) {
        for (Country record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Country record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Country> records) {
        for (Country record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Country record) {
        em.remove(record);
    }

    @Override
    public Country find(Country.CountryPK pk) {
        return em.find(Country.class, pk);
    }

    @Override
    public List<Country> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Country.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Country> result = q.getResultList();
        return result;
    }
    
}
