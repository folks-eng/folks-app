package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Wallet;
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
public class WalletDAOImpl extends AbstractDAO implements WalletDAO {
    
    private final String TABLE = "fks_wallets";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Wallet record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Wallet> records) {
        for (Wallet record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Wallet record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Wallet> records) {
        for (Wallet record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Wallet record) {
        em.remove(record);
    }

    @Override
    public Wallet find(Wallet.WalletPK pk) {
        return em.find(Wallet.class, pk);
    }

    @Override
    public List<Wallet> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Wallet.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Wallet> result = q.getResultList();
        return result;
    }
    
}
