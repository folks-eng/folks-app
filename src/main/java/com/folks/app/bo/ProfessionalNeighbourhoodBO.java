package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.model.ProfessionalNeighbourhood;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.folks.app.dao.ProfessionalNeighbourhoodDAO;

/**
 *
 * @author schan280
 */
public class ProfessionalNeighbourhoodBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalNeighbourhoodBO.class);
    
    private final ProfessionalNeighbourhoodDAO professionalNeighbourhoodDAO;

    public ProfessionalNeighbourhoodBO() {
        this.professionalNeighbourhoodDAO = DAOProxy.get(ProfessionalNeighbourhoodDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalNeighbourhoodBO: {}. ProfessionalNeighbourhoodDAO: {}", getClass().getSimpleName(), professionalNeighbourhoodDAO);
        }
    }

    public ProfessionalNeighbourhood create(AppUser usr, ProfessionalNeighbourhood professionalNeighbourhood) throws IllegalAccessException {
        // Only admin has the privilege to register a professionalNeighbourhood.
        ensureAdmin(usr);
        validateScope(usr, "professionalNeighbourhood:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalNeighbourhood.setStatus(ProfessionalNeighbourhood.Status.PACTIVE);
        if (professionalNeighbourhood.getCreatedAt() == null) {
            professionalNeighbourhood.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        
        professionalNeighbourhoodDAO.insert(professionalNeighbourhood);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingProfessionalNeighbourhood created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professionalNeighbourhood;
    }

    public void create(AppUser usr, List<ProfessionalNeighbourhood> records) throws IllegalAccessException {
        // Only admin has the privilege to register a professionalNeighbourhood.
        ensureAdmin(usr);
        validateScope(usr, "professionalNeighbourhood:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        for (ProfessionalNeighbourhood professionalNeighbourhood : records) {
            professionalNeighbourhood.setStatus(ProfessionalNeighbourhood.Status.PACTIVE);
            if (professionalNeighbourhood.getCreatedAt() == null) {
                professionalNeighbourhood.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        
        professionalNeighbourhoodDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} OperatingProfessionalNeighbourhood record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public ProfessionalNeighbourhood modify(AppUser usr, ProfessionalNeighbourhood professionalNeighbourhood) throws IllegalAccessException {
        // Only admin has the privilege to register a professionalNeighbourhood.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        ProfessionalNeighbourhood existing = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(professionalNeighbourhood.getId()));
        if (existing == null) {
            throw new IllegalArgumentException("No professionalNeighbourhood found for identifier: " + professionalNeighbourhood.getId());
        }
        // Update attributes of existing record
        existing.setNeighbourhoodId(professionalNeighbourhood.getNeighbourhoodId());
        existing.setProfessionalId(professionalNeighbourhood.getProfessionalId());
        existing.setStatus(professionalNeighbourhood.getStatus());
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        professionalNeighbourhoodDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("OperatingProfessionalNeighbourhood record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<ProfessionalNeighbourhood> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<ProfessionalNeighbourhood> rows = professionalNeighbourhoodDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded professionalNeighbourhood record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public ProfessionalNeighbourhood view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        ProfessionalNeighbourhood professionalNeighbourhood = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(id));
        if (professionalNeighbourhood == null) {
            throw new IllegalArgumentException("No OperatingProfessionalNeighbourhood found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professionalNeighbourhood details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professionalNeighbourhood;
    }

    public ProfessionalNeighbourhood remove(AppUser usr, Integer id) throws IllegalAccessException {
        // Only admin has the privilege to register a professionalNeighbourhood.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        ProfessionalNeighbourhood professionalNeighbourhood = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(id));

        if (professionalNeighbourhood == null) {
            throw new IllegalArgumentException("No professionalNeighbourhood found for id: " + id);
        }
        professionalNeighbourhoodDAO.delete(professionalNeighbourhood);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted OperatingProfessionalNeighbourhood. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return professionalNeighbourhood;
    }
}
