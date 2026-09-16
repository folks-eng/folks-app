package com.folks.app.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

/**
 *
 * @author sudip
 */
public class QueryDAOImpl implements QueryDAO {
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public List<Object> execute(String sql, List<Object> params) {
        Query query =  em.createNativeQuery(sql);
        
        for (int i = 0; i < params.size(); i ++) {
            query.setParameter(i + 1, params.get(i));
        }
        return query.getResultList();
    }
}
