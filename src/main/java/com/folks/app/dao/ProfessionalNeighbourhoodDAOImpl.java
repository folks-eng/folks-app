package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.ProfessionalNeighbourhood;
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
public class ProfessionalNeighbourhoodDAOImpl extends AbstractDAO implements ProfessionalNeighbourhoodDAO {
    
    private final String TABLE = "fks_professional_neighbourhoods";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(ProfessionalNeighbourhood record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<ProfessionalNeighbourhood> records) {
        for (ProfessionalNeighbourhood record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(ProfessionalNeighbourhood record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<ProfessionalNeighbourhood> records) {
        for (ProfessionalNeighbourhood record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(ProfessionalNeighbourhood record) {
        em.remove(record);
    }

    @Override
    public ProfessionalNeighbourhood find(ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK pk) {
        return em.find(ProfessionalNeighbourhood.class, pk);
    }

    @Override
    public List<ProfessionalNeighbourhood> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), ProfessionalNeighbourhood.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<ProfessionalNeighbourhood> result = q.getResultList();
        return result;
    }
    
}
