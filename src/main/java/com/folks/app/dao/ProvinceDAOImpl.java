package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Province;
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
public class ProvinceDAOImpl extends AbstractDAO implements ProvinceDAO {
    
    private final String TABLE = "fks_provinces";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Province record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Province> records) {
        for (Province record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Province record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Province> records) {
        for (Province record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Province record) {
        em.remove(record);
    }

    @Override
    public Province find(Province.ProvincePK pk) {
        return em.find(Province.class, pk);
    }

    @Override
    public List<Province> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Province.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Province> result = q.getResultList();
        return result;
    }
    
}
