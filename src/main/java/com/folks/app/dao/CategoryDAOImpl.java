package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Category;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Concrete DAO class to handle database operations related.
 *
 * @author Sudiptasish Chanda
 */
public class CategoryDAOImpl implements CategoryDAO {
    
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

        TypedQuery q = em.createNativeQuery(query.toQuery(), Category.class);
        List<Object> binds = query.params();
        
        idx = 1;
        for (Object bind : binds) {
            q.setParameter(idx ++, bind);
        }
        q.setFirstResult(search.offset());
        q.setMaxResults(search.limit());
        
        List<Category> result = q.getResultList();
        return result;
    }
    
}
