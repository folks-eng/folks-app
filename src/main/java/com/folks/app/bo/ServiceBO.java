package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.ServiceDAO;
import com.folks.app.model.Service;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ServiceBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceBO.class);
    
    private final ServiceDAO serviceDAO;

    public ServiceBO() {
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ServiceDAO: {}", getClass().getSimpleName(), serviceDAO);
        }
    }

    public Service create(AppUser usr, Service service) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        serviceDAO.insert(service);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Service created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return service;
    }

    public void create(AppUser usr, List<Service> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        serviceDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Service record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Service modify(AppUser usr, Service service) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Service existing = serviceDAO.find(new Service.ServicePK(service.getServiceId()));
        if (existing == null) {
            throw new IllegalArgumentException("No service found for identifier: " + service.getServiceId());
        }
        // Update attributes of existing record
        existing.setCategoryId(service.getCategoryId());
        existing.setName(service.getName());
        existing.setDescription(service.getDescription());
        existing.setBasePrice(service.getBasePrice());
        existing.setDurationMinutes(service.getDurationMinutes());

        serviceDAO.update(service);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Service record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Service> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Service> rows = serviceDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded service record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Service view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Service service = serviceDAO.find(new Service.ServicePK(id));
        if (service == null) {
            throw new IllegalArgumentException("No Service found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched service details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return service;
    }

    public Service remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Service service = serviceDAO.find(new Service.ServicePK(id));

        if (service == null) {
            throw new IllegalArgumentException("No service found for id: " + id);
        }
        serviceDAO.delete(service);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Service. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return service;
    }
}
