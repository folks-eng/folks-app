package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.cache.impl.CategoryCache;
import com.folks.app.cache.impl.ServiceCache;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.dao.ProfessionalServiceDAO;
import com.folks.app.model.Category;
import com.folks.app.model.Professional;
import com.folks.app.model.ProfessionalService;
import com.folks.app.model.Service;
import com.folks.app.util.Constants;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalServiceBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalServiceBO.class);
    
    private final ProfessionalServiceDAO professionalServiceDAO;

    private final ProfessionalDAO professionalDAO;

    public ProfessionalServiceBO() {
        this.professionalServiceDAO = DAOProxy.get(ProfessionalServiceDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalServiceBO: {}. ProfessionalServiceDAO: {}", getClass().getSimpleName(), professionalServiceDAO);
        }
    }

    public ProfessionalService create(AppUser usr, ProfessionalService professionalService) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalServiceDAO.insert(professionalService);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("ProfessionalService created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professionalService;
    }

    public void create(AppUser usr, List<ProfessionalService> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalServiceDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} ProfessionalService record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public ProfessionalService modify(AppUser usr, ProfessionalService professionalService) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        // First fetch the entry, to see if this already exists.
        ProfessionalService existing = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(professionalService.getId()));
        if (existing == null) {
            throw new IllegalArgumentException("No professionalService found for identifier: " + professionalService.getId());
        }
        if (! professionalService.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
        // Update attributes of existing record
        existing.setProfessionalId(professionalService.getProfessionalId());
        existing.setServiceId(professionalService.getServiceId());
        existing.setPrice(professionalService.getPrice());
        existing.setIsActive(professionalService.getIsActive());

        professionalServiceDAO.update(professionalService);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("ProfessionalService record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }
    
    public void updateExpertise(AppUser usr, List<Integer> subCategoryIds) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }
        // First, fetch the existing mappings
        SearchCriteria search = SearchCriteria.from(new QueryParams(), "professionalId", professional.getProfessionalId());
        List<ProfessionalService> currentServices = professionalServiceDAO.query(search);
        
        // Re-Build the professional vs services mapping.
        List<Service> services = fetchServices(subCategoryIds);
        Timestamp createdAt = new Timestamp(DateUtil.currentUTCDate().getTime());
        
        // Assign individual services to this professional's profile
        List<ProfessionalService> pServices = new ArrayList<>(services.size());
        for(Service service: services) {
            ProfessionalService pService = new ProfessionalService();
            pService.setProfessionalId(professional.getProfessionalId());
            pService.setServiceId(service.getServiceId());
            pService.setPrice(service.getBasePrice());
            pService.setIsActive(Constants.PROF_SERVICE_ACTIVE);
            pService.setCreatedAt(createdAt);
            
            pServices.add(pService);
        }
        
        // Delete existing mapping(s)
        professionalServiceDAO.delete(currentServices);
        
        // Now insert new mapping(s)
        professionalServiceDAO.insert(pServices);
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional expertise modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
    }

    public List<ProfessionalService> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        Professional existing = professionalDAO.findByExtId(usr.principal().sub());
        if (existing == null || existing.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        SearchCriteria search = SearchCriteria.from(params, "professionalId", existing.getProfessionalId());
        List<ProfessionalService> pServices = professionalServiceDAO.query(search);
        
        for (ProfessionalService pService : pServices) {
            Service service = ServiceCache.getCache().get(pService.getServiceId());
            Category category = CategoryCache.getCache().get(service.getCategoryId());
            
            pService.setSubCategoryName(category.getName());
            pService.setServiceName(service.getName());
        }

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded professionalService record(s). Elapsed time(ms): {}", pServices.size(), timer.elapsedTimeMillis());
        }
        return pServices;
    }

    public ProfessionalService view(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        ProfessionalService professionalService = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(id));
        if (professionalService == null) {
            throw new IllegalArgumentException("No ProfessionalService found for id: " + id);
        }
        if (! professionalService.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professionalService details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professionalService;
    }

    public ProfessionalService remove(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        // First fetch the entry, to see if this already exists.
        ProfessionalService professionalService = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(id));

        if (professionalService == null) {
            throw new IllegalArgumentException("No professionalService found for id: " + id);
        }
        if (! professionalService.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
        professionalServiceDAO.delete(professionalService);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted ProfessionalService. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return professionalService;
    }
    
    private List<Service> fetchServices(List<Integer> expertise) {
        List<Service> services = new ArrayList<>();
        
        for (Service service : ServiceCache.getCache().getAllValues()) {
            for (Integer subCategory : expertise) {
                if (service.getCategoryId().equals(subCategory)) {
                    services.add(service);
                }
            }
        }
        return services;
    }
}
