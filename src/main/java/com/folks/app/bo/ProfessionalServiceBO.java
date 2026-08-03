package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.ProfessionalServiceDAO;
import com.folks.app.model.ProfessionalService;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalServiceBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalServiceBO.class);
    
    private final ProfessionalServiceDAO professionalServiceDAO;

    public ProfessionalServiceBO() {
        this.professionalServiceDAO = DAOProxy.get(ProfessionalServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ProfessionalServiceDAO: {}", getClass().getSimpleName(), professionalServiceDAO);
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

    public ProfessionalService modify(AppUser usr, ProfessionalService professionalService) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        ProfessionalService existing = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(professionalService.getId()));
        if (existing == null) {
            throw new IllegalArgumentException("No professionalService found for identifier: " + professionalService.getId());
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

    public List<ProfessionalService> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<ProfessionalService> rows = professionalServiceDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded professionalService record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public ProfessionalService view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        ProfessionalService professionalService = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(id));
        if (professionalService == null) {
            throw new IllegalArgumentException("No ProfessionalService found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professionalService details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professionalService;
    }

    public ProfessionalService remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        ProfessionalService professionalService = professionalServiceDAO.find(new ProfessionalService.ProfessionalServicePK(id));

        if (professionalService == null) {
            throw new IllegalArgumentException("No professionalService found for id: " + id);
        }
        professionalServiceDAO.delete(professionalService);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted ProfessionalService. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return professionalService;
    }
}
