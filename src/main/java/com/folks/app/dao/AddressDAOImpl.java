package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Address;
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
public class AddressDAOImpl extends AbstractDAO implements AddressDAO {
    
    private final String TABLE = "fks_addresses";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Address record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Address> records) {
        for (Address record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Address record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Address> records) {
        for (Address record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Address record) {
        em.remove(record);
    }

    @Override
    public Address find(Address.AddressPK pk) {
        return em.find(Address.class, pk);
    }
    
    @Override
    public List<Address> find(List<Integer> ids) {
        return em.createNamedQuery("Address.selectByIds", Address.class)
            .setParameter("ids", ids != null && !ids.isEmpty() ? ids : Arrays.asList(-1))
            .setHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE)
            .getResultList();
    }

    @Override
    public List<Address> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Address.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Address> result = q.getResultList();
        return result;
    }

    @Override
    public List<Address> queryByUser(String extUserId) {
        return em.createNamedQuery("Address.selectAll", Address.class)
            .setParameter(1, extUserId)
            .setHint(QueryHints.ALLOW_NATIVE_QUERY, Boolean.TRUE)
            .getResultList();
    }
    
}
