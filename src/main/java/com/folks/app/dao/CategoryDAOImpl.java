package com.folks.app.dao;

import org.javalabs.jpa.query.Criteria;
import com.folks.app.model.Category;
import com.folks.app.model.Service;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    
    @Override
    public List<Category> queryAll(SearchCriteria search) {
        List<Object> ids = search.params().get("id");
        Integer val = ids != null && !ids.isEmpty() ? 0 : 1;
        
        Query q = em.createNamedQuery("Category.selectCategoryAndServices");
        q.setParameter(1, val);
        q.setParameter("ids", ids != null && !ids.isEmpty() ? ids : Arrays.asList(-1));
        
        List<Object[]> rows = q.getResultList();
        
        // SELECT a.category_id [0]
        //      , a.name AS category_name [1]
        //      , a.icon [2]
        //      , a.image AS category_image [3]
        //      , a.tag_line [4]
        //
        //      , b.category_id AS sub_category_id [5]
        //      , b.name AS sub_category_name [6]
        //      , b.image AS sub_category_image [7]
        //      , b.parent_id [8]
        //
        //      , c.category_id AS srvc_category_id [9]
        //      , c.service_id [10]
        //      , c.name AS service_name [11]
        //      , c.description [12]
        //      , c.base_price [13]
        //      , c.duration_minutes [14]
        //      , c.image [15]
        //      , c.rating_avg [16]
        //      , c.reviews [17]
        
        Map<Integer, Category> parents = new HashMap<>();
        Map<Integer, Category> subCategories = new HashMap<>();
        
        for (Object[] row : rows) {
            Integer categoryId = (Integer)row[0];
            if (! parents.containsKey(categoryId)) {
                Category category = new Category();
                category.setCategoryId(categoryId);
                category.setName((String)row[1]);
                category.setIcon((String)row[2]);
                category.setImage((String)row[3]);
                category.setTagLine((String)row[4]);
                category.setSubCategories(new ArrayList<>());
                
                parents.put(categoryId, category);
            }
            
            Integer subCategoryId = (Integer)row[5];
            if (! subCategories.containsKey(subCategoryId)) {
                Category subCategory = new Category();
                subCategory.setCategoryId(subCategoryId);
                subCategory.setName((String)row[6]);
                subCategory.setImage((String)row[7]);
                subCategory.setServices(new ArrayList<>());
                
                subCategories.put(subCategoryId, subCategory);
                Category parent = parents.get((Integer)row[8]);
                if (parent == null) {
                    throw new IllegalArgumentException("Inconsistent data for parentId: " + (Integer)row[8]);
                }
                parent.getSubCategories().add(subCategory);
            }
            
            Integer srvcCategoryId = (Integer)row[9];
            Integer serviceId = (Integer)row[10];
            
            Service service = new Service();
            service.setServiceId(serviceId);
            service.setName((String)row[11]);
            service.setDescription((String)row[12]);
            service.setBasePrice((Double)row[13]);
            service.setCurrency((String)row[14]);
            service.setDurationMinutes(((Integer)row[15]).shortValue());
            service.setImage((String)row[16]);
            service.setRatingAvg((Double)row[17]);
            service.setReviews((Integer)row[18]);
            
            Category subCategory = subCategories.get(srvcCategoryId);
            if (subCategory == null) {
                throw new IllegalArgumentException("Inconsistent data for service: " + serviceId);
            }
            subCategory.getServices().add(service);
        }
        return new ArrayList<>(parents.values());
    }
}
