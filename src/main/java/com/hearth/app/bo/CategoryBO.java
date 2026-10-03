package com.hearth.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.hearth.app.auth.AppUser;
import com.hearth.app.cache.impl.CategoryCache;
import com.hearth.app.cache.impl.ServiceCache;
import com.hearth.app.dao.CategoryDAO;
import com.hearth.app.model.Category;
import com.hearth.app.model.Service;
import com.hearth.app.util.QueryParams;
import com.hearth.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CategoryBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryBO.class);
    
    private final CategoryDAO categoryDAO;

    public CategoryBO() {
        this.categoryDAO = DAOProxy.get(CategoryDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized BO: {}. CategoryDAO: {}", getClass().getSimpleName(), categoryDAO);
        }
    }

    public Category create(AppUser usr, Category category) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();        
        
        categoryDAO.insert(category);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Category created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return category;
    }

    public void create(AppUser usr, List<Category> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        categoryDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Category record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Category modify(AppUser usr, Category category) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Category existing = categoryDAO.find(new Category.CategoryPK(category.getCategoryId()));
        if (existing == null) {
            throw new IllegalArgumentException("No category found for identifier: " + category.getCategoryId());
        }
        // Update attributes of existing record
        existing.setName(category.getName());
        existing.setParentId(category.getParentId());

        categoryDAO.update(category);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Category record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Category> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Category> rows = categoryDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded category record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Category view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Category category = categoryDAO.find(new Category.CategoryPK(id));
        if (category == null) {
            throw new IllegalArgumentException("No Category found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched category details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return category;
    }

    public List<Category> viewAllHierarchy() {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        List<Category> categories = CategoryCache.getCache().query("parentId", null);
        for (Category category : categories) {
            // Fetch the sub-categories for each of these categories.
            List<Category> subCategories = CategoryCache.getCache().query("parentId", category.getCategoryId());
            category.setSubCategories(subCategories);
            
            // Now for each sub-category, fetch the corresponding services.
            for (Category subCategory : subCategories) {
                List<Service> services = ServiceCache.getCache().query("categoryId", subCategory.getCategoryId());
                subCategory.setServices(services);
            }
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded category hierarchical record(s). Elapsed time(ms): {}", categories.size(), timer.elapsedTimeMillis());
        }
        return categories;
    }

    public Category remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Category category = categoryDAO.find(new Category.CategoryPK(id));

        if (category == null) {
            throw new IllegalArgumentException("No category found for id: " + id);
        }
        categoryDAO.delete(category);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Category. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return category;
    }
}
