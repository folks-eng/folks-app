package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Service;
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
public class ServiceDAOImpl extends AbstractDAO implements ServiceDAO {
    
    private final String TABLE = "fks_services";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Service record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Service> records) {
        for (Service record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Service record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Service> records) {
        for (Service record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Service record) {
        em.remove(record);
    }

    @Override
    public Service find(Service.ServicePK pk) {
        return em.find(Service.class, pk);
    }

    @Override
    public List<Service> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Service.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Service> result = q.getResultList();
        return result;
    }
    
}
