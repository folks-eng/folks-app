package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Category;
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
public class CategoryDAOImpl extends AbstractDAO implements CategoryDAO {
    
    private final String TABLE = "fks_categories";
    
    @PersistenceContext(name = "folks-app-pu")
    private EntityManager em;
    
    @Override
    public void insert(Category record) {
        insert(Arrays.asList(record));
    }

    @Override
    public void insert(List<Category> records) {
        for (Category record : records) {
            em.persist(record);
        }
    }

    @Override
    public void update(Category record) {
        update(Arrays.asList(record));
    }

    @Override
    public void update(List<Category> records) {
        for (Category record : records) {
            em.merge(record);
        }
    }

    @Override
    public void delete(Category record) {
        em.remove(record);
    }

    @Override
    public Category find(Category.CategoryPK pk) {
        return em.find(Category.class, pk);
    }

    @Override
    public List<Category> query(SearchCriteria search) {
        Criteria query = getQuery(TABLE, search);

        TypedQuery q = em.createNativeQuery(query.toQuery(), Category.class);
        List<Object> binds = query.params();
        
        int idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Category> result = q.getResultList();
        return result;
    }
}
