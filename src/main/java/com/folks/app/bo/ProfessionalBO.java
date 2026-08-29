package com.folks.app.bo;

import com.folks.app.dao.*;
import com.folks.app.model.*;
import com.folks.app.util.*;
import jakarta.persistence.NoResultException;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalBO.class);

    private final UserDAO userDAO;

    private final ProfessionalDAO professionalDAO;

    private final ServiceDAO serviceDAO;

    public ProfessionalBO() {
        this.userDAO = DAOProxy.get(UserDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. ProfessionalDAO: {}", getClass().getSimpleName(), professionalDAO);
        }
    }

    public ProfessionalMaster register(AppUser usr, ProfessionalMaster profMaster) throws IllegalAccessException {
        // Only admin has the privilege to create user or professional
//        ensureAdmin(usr);
//        validateScope(usr, "user:create");
        Validator.validateProfAll(profMaster);

        User existingUser = null;
        try {
            existingUser = userDAO.findByExtId(profMaster.getExtUserId());
        }
        catch(NoResultException ex) {
            throw new ResourceNotFoundException("User not existing.");
        }
        if(existingUser == null)
            throw new ResourceNotFoundException("User not found.");
        Integer userId = existingUser.getUserId();
        Map<String, List<String>> param = new HashMap<>();
        param.put("userId", List.of(String.valueOf(userId)));
        SearchCriteria search = SearchCriteria.from(new QueryParams(param));
        List<Professional> profList = professionalDAO.query(search);
        if(!profList.isEmpty())
            throw new IllegalArgumentException("Professional already existing");

        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user entry and create the Entity objects for inserting into tables Address, Document, Professional, ProfServices
        Address newAddr = profMaster.getAddress();
        newAddr.setUserId(userId);
        if (newAddr.getLabel() == null) {
            newAddr.setLabel(Constants.DEFAULT_LABEL);
        }
        newAddr.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        if (newAddr.getIsDefault() == null) {
            newAddr.setIsDefault(Constants.IS_DEFAULT_ADDR);     // All addresses are set to default.
        }
        Document newDoc = profMaster.getDocument();
        newDoc.setUserId(userId);
        if(newDoc.getDocumentType() == null) {
            newDoc.setDocumentType(Constants.DEFAULT_DOC_TYPE);
        }
        newDoc.setVerificationStatus(Document.Verificationstatus.PENDING);
        newAddr.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        Professional newProf = buildProfObj(userId, profMaster);
        List<Service> serviceList = fetchServices(profMaster.getSubCategories());

        professionalDAO.insertAll(newAddr, newDoc, newProf, serviceList);
        profMaster.setAddress(newAddr);
        profMaster.setDocument(newDoc);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional created successfully with Id {}, Elapsed time(ms): {}", newProf.getProfessionalId(), timer.elapsedTimeMillis());
        }
        return profMaster;
    }

    private void addProfServices(Integer professionalId, List<Service> serviceList) {
    }

    private List<Service> fetchServices(List<Integer> categoryIdList) {
        List<Service>  serviceList = new ArrayList<>();
        try {
            serviceList = serviceDAO.findByCat(categoryIdList);
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("No Service found for Categories  : " + categoryIdList);
        }
        return serviceList;
    }

    private Professional buildProfObj(Integer userId, ProfessionalMaster profMaster) {
        Professional newProf = new Professional();
        newProf.setUserId(userId);
        newProf.setExperienceYears(profMaster.getExperienceYears());
        newProf.setIsVerified(Constants.PROF_NOT_VERIFIED);
        return newProf;
    }

    public Professional create(AppUser usr, Professional professional) throws IllegalAccessException {
        ensureAdmin(usr);
        validateScope(usr, "user:create");
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        professionalDAO.insert(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professional;
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

    /**
     * Retrieves the user associated with the authenticated application user.
     *
     * <p>
     * The user's external identifier is obtained from the {@code sub} claim of the JWT principal and is used to
     * query the user data store.
     *
     * <p>
     * The user may first be looked up from a distributed cache to avoid an * unnecessary database query.
     * If the user is not available in the cache, the persistent data store is queried as a fallback.
     *
     * @param extUserId   The authenticated application user containing the JWT principal
     * @return User The user associated with the external identifier
     *
     * @throws IllegalArgumentException if no user exists for the external identifier
     */
    private User fetchUser(String extUserId) {
        try {
            return userDAO.findByExtId(extUserId);
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("No User found for id: " + extUserId);
        }
    }
}
