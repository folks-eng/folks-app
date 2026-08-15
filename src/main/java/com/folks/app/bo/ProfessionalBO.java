package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.model.Professional;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalBO.class);
    
    private final ProfessionalDAO professionalDAO;

    public ProfessionalBO() {
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ProfessionalDAO: {}", getClass().getSimpleName(), professionalDAO);
        }
    }

    public Professional create(AppUser usr, Professional professional) throws IllegalAccessException {
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalDAO.insert(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professional;
    }

    public void create(AppUser usr, List<Professional> records) throws IllegalAccessException {
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        professionalDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Professional record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Professional modify(AppUser usr, Professional professional) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Professional existing = professionalDAO.find(new Professional.ProfessionalPK(professional.getProfessionalId()));
        if (existing == null) {
            throw new IllegalArgumentException("No professional found for identifier: " + professional.getProfessionalId());
        }
        // Update attributes of existing record
        existing.setUserId(professional.getUserId());
        existing.setBio(professional.getBio());
        existing.setExperienceYears(professional.getExperienceYears());
        existing.setRatingAvg(professional.getRatingAvg());
        existing.setIsVerified(professional.getIsVerified());

        professionalDAO.update(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Professional> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Professional> rows = professionalDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded professional record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Professional view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.find(new Professional.ProfessionalPK(id));
        if (professional == null) {
            throw new IllegalArgumentException("No Professional found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professional details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professional;
    }

    public Professional remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Professional professional = professionalDAO.find(new Professional.ProfessionalPK(id));

        if (professional == null) {
            throw new IllegalArgumentException("No professional found for id: " + id);
        }
        professionalDAO.delete(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Professional. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return professional;
    }
}
