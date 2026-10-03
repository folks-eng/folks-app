package com.hearth.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.hearth.app.auth.AppUser;
import com.hearth.app.cache.impl.NeighbourhoodCache;
import com.hearth.app.dao.ProfessionalDAO;
import com.hearth.app.model.ProfessionalNeighbourhood;
import com.hearth.app.util.QueryParams;
import com.hearth.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hearth.app.dao.ProfessionalNeighbourhoodDAO;
import com.hearth.app.model.Neighbourhood;
import com.hearth.app.model.Professional;
import java.util.ArrayList;

/**
 *
 * @author schan280
 */
public class ProfessionalNeighbourhoodBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalNeighbourhoodBO.class);
    
    private final ProfessionalNeighbourhoodDAO professionalNeighbourhoodDAO;

    private final ProfessionalDAO professionalDAO;

    public ProfessionalNeighbourhoodBO() {
        this.professionalNeighbourhoodDAO = DAOProxy.get(ProfessionalNeighbourhoodDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalNeighbourhoodBO: {}. ProfessionalNeighbourhoodDAO: {}"
                    , getClass().getSimpleName(), professionalNeighbourhoodDAO);
        }
    }

    public ProfessionalNeighbourhood create(AppUser usr, ProfessionalNeighbourhood professionalNeighbourhood) throws IllegalAccessException {
        // Only admin has the privilege to register a professionalNeighbourhood.
        ensureAdmin(usr);
        validateScope(usr, "professionalNeighbourhood:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalNeighbourhood.setStatus(ProfessionalNeighbourhood.Status.ACTIVE);
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
            professionalNeighbourhood.setStatus(ProfessionalNeighbourhood.Status.ACTIVE);
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

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        // First fetch the entry, to see if this already exists.
        ProfessionalNeighbourhood existing = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(professionalNeighbourhood.getId()));
        if (existing == null) {
            throw new IllegalArgumentException("No professional locally found for identifier: " + professionalNeighbourhood.getId());
        }
        if (! existing.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
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
    
    public void updateNeighbourhoods(AppUser usr, List<Integer> nbHoodIds) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }
        // First, fetch the existing mappings
        SearchCriteria search = SearchCriteria.from(new QueryParams(), "professionalId", professional.getProfessionalId());
        List<ProfessionalNeighbourhood> currentNeighbourhoods = professionalNeighbourhoodDAO.query(search);
        
        // Re-Build the professional vs services mapping.
        List<Neighbourhood> nbhoods = fetchNeighbourhoods(nbHoodIds);
        Timestamp createdAt = new Timestamp(DateUtil.currentUTCDate().getTime());
        
        // Assign individual services to this professional's profile
        List<ProfessionalNeighbourhood> pNbHoods = new ArrayList<>(nbhoods.size());
        for(Neighbourhood nbhood: nbhoods) {
            ProfessionalNeighbourhood pNbHood = new ProfessionalNeighbourhood();
            pNbHood.setProfessionalId(professional.getProfessionalId());
            pNbHood.setNeighbourhoodId(nbhood.getNeighbourhoodId());
            pNbHood.setStatus(ProfessionalNeighbourhood.Status.ACTIVE);
            pNbHood.setCreatedAt(createdAt);
            
            pNbHoods.add(pNbHood);
        }
        
        // Delete existing mapping(s)
        professionalNeighbourhoodDAO.delete(currentNeighbourhoods);
        
        // Now insert new mapping(s)
        professionalNeighbourhoodDAO.insert(pNbHoods);
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional neighbourhoods modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
    }

    public List<ProfessionalNeighbourhood> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        SearchCriteria search = SearchCriteria.from(params, "professionalId", professional.getProfessionalId());
        List<ProfessionalNeighbourhood> records = professionalNeighbourhoodDAO.query(search);
        
        for (ProfessionalNeighbourhood record : records) {
            Neighbourhood nbhood = NeighbourhoodCache.getCache().get(record.getNeighbourhoodId());
            record.setLocality(nbhood.getLocality());
            record.setPincode(nbhood.getPincode());
            record.setZone(nbhood.getZone());
        }

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded professionalNeighbourhood record(s). Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
        return records;
    }

    public ProfessionalNeighbourhood view(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        // First fetch the entry, to see if this already exists.
        ProfessionalNeighbourhood professionalNeighbourhood = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(id));
        if (professionalNeighbourhood == null) {
            throw new IllegalArgumentException("No OperatingProfessionalNeighbourhood found for id: " + id);
        }
        if (! professionalNeighbourhood.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
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

        Professional professional = professionalDAO.findByExtId(usr.principal().sub());
        if (professional == null || professional.getProfessionalId() == null) {
            throw new IllegalArgumentException("No such professional is found with id " + usr.principal().sub());
        }

        // First fetch the entry, to see if this already exists.
        ProfessionalNeighbourhood professionalNeighbourhood = professionalNeighbourhoodDAO.find(new ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK(id));
        if (professionalNeighbourhood == null) {
            throw new IllegalArgumentException("No professionalNeighbourhood found for id: " + id);
        }
        if (! professionalNeighbourhood.getProfessionalId().equals(professional.getProfessionalId())) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
        professionalNeighbourhoodDAO.delete(professionalNeighbourhood);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted OperatingProfessionalNeighbourhood. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return professionalNeighbourhood;
    }
    
    private List<Neighbourhood> fetchNeighbourhoods(List<Integer> nbhoodIds) {
        List<Neighbourhood> nbhoods = new ArrayList<>();
        
        for (Integer nbhoodId : nbhoodIds) {
            nbhoods.add(NeighbourhoodCache.getCache().get(nbhoodId));
        }
        return nbhoods;
    }
}
