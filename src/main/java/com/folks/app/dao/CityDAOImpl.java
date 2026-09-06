package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.City;
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
public class CityDAOImpl extends AbstractDAO implements CityDAO {
    
    private final String TABLE = "fks_wallets";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(City record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<City> records) {
        for (City record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(City record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<City> records) {
        for (City record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(City record) {
        em.remove(record);
    }

    @Override
    public City find(City.CityPK pk) {
        return em.find(City.class, pk);
    }

    @Override
    public List<City> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), City.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<City> result = q.getResultList();
        return result;
    }
    
}
