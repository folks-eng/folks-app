package com.folks.app.dao;

import com.folks.app.model.*;
import com.folks.app.util.Constants;
import org.javalabs.jpa.query.Criteria;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class ProfessionalDAOImpl implements ProfessionalDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalDAOImpl.class);

    private final String TABLE = "fks_professionals";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;

    public void insertAll(Address addr, Document doc, Professional prof, List<Service> serviceList) {
        em.persist(addr);
        em.persist(doc);

        // Persist the Professional entity after which it has the professionalId
        em.persist(prof);
        em.flush();
        Integer profId = prof.getProfessionalId();
        LOGGER.info( "Professional inserted with id " +profId);
        for(Service service: serviceList) {
            ProfessionalService pService = new ProfessionalService();
            pService.setProfessionalId(profId);
            pService.setServiceId(service.getServiceId());
            pService.setPrice(BigDecimal.valueOf(service.getBasePrice().doubleValue()));
            pService.setIsActive(Constants.PROF_SERVICE_ACTIVE);
            em.persist(pService);
        }
        LOGGER.info( "Professional services inserted  to complete registration of Professional " +serviceList.size());
    }

    @Override
    public void insert(Professional record) {
        // Persist the User entity
        em.persist(record.getUser());

        // Persist the Professional entity (saves the foreign key user_id)
        em.persist(record);
    }

    @Override
    public void insert(List<Professional> records) {
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
    public List<Professional> query(SearchCriteria search) {
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

        TypedQuery q = em.createNativeQuery(query.toQuery(), Professional.class);
        List<Object> binds = query.params();
        
        idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Professional> result = q.getResultList();
        return result;
    }
    
}
