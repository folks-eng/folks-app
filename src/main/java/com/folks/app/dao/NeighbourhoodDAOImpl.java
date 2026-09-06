package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Neighbourhood;
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
public class NeighbourhoodDAOImpl extends AbstractDAO implements NeighbourhoodDAO {
    
    private final String TABLE = "fks_neighbourhoods";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Neighbourhood record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Neighbourhood> records) {
        for (Neighbourhood record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Neighbourhood record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Neighbourhood> records) {
        for (Neighbourhood record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Neighbourhood record) {
        em.remove(record);
    }

    @Override
    public Neighbourhood find(Neighbourhood.NeighbourhoodPK pk) {
        return em.find(Neighbourhood.class, pk);
    }

    @Override
    public List<Neighbourhood> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Neighbourhood.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Neighbourhood> result = q.getResultList();
        return result;
    }
    
}
