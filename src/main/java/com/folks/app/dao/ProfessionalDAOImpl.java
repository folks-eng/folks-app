package com.folks.app.dao;

import com.folks.app.model.*;
import org.javalabs.jpa.query.Criteria;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import org.javalabs.jpa.annotation.Dao;
import org.javalabs.jpa.descriptor.QueryCache;
import org.javalabs.jpa.util.QueryHints;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class ProfessionalDAOImpl extends AbstractDAO implements ProfessionalDAO {

    private final String TABLE = "fks_professionals";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Dao
    private UserDAO userDAO;
    
    @Dao
    private AddressDAO addressDAO;
    
    @Dao
    private DocumentDAO documentDAO;
    
    @Dao
    private ProfessionalServiceDAO profServiceDAO;
    
    @Override
    public void insertProfile(Professional professional) {
        // User record is already present, no need to insert it again.
        // Maintain the below insertion order.
        addressDAO.insert(professional.getUser().getAddresses());
        documentDAO.insert(professional.getUser().getDocuments());

        // Persist the Professional entity after which the professional_id will be generated.
        // Flushing is important because it ensures that the insert statement is immediately sent to the RDBMS,
        // allowing the database-generated identity value, professional_id to become available.
        em.persist(professional);
        em.flush();
        
        // Assign this professional idd to all the professional service objects.
        for (ProfessionalService pService: professional.getProfServices()) {
            pService.setProfessionalId(professional.getProfessionalId());
        }
        profServiceDAO.insert(professional.getProfServices());
    }

    @Override
    public void insert(Professional record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Professional> records) {
        List<User> users = new ArrayList<>(records.size());
        for (Professional record : records) {
            if (record.getUser() != null) {
                users.add(record.getUser());
            }
        }
        if (! users.isEmpty()) {
            userDAO.insert(users);
        }
        for (Professional record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Professional record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Professional> records) {
        for (Professional record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Professional record) {
        em.remove(record);
    }

    @Override
    public Professional find(Professional.ProfessionalPK pk) {
        return em.find(Professional.class, pk);
    }

    @Override
    public Professional findByExtId(String externalId) {
        List<Professional> list = em.createNamedQuery("Professional.selectByExtId", Professional.class)
            .setParameter(1, User.Role.PROFESSIONAL.name())
            .setParameter(2, externalId)
            .setHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE)
            .setHint(QueryHints.QUERY_TYPE, QueryCache.QueryType.SELECT_REL)
            .setHint(QueryHints.FETCH_DEF, "OneToOne")
            .setHint(QueryHints.FETCH_FIELD, "user")
            .getResultList();
        
        if (! list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Integer> findProfessionalIds(int offset, int limit) {
        Criteria query = new Criteria()
                .select("professional_id")
                .from(TABLE)
                .where("is_verified").eq((short)1)
                .orderBy("professional_id");
        
        Query q = em.createNativeQuery(query.toQuery());
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        return q.setFirstResult(offset)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public List<Professional> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Professional.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Professional> result = q.getResultList();
        return result;
    }
    
}
