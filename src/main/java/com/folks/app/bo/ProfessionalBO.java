package com.folks.app.bo;

import com.folks.app.dao.*;
import com.folks.app.model.*;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.util.IdGenerator;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import com.folks.app.util.Validator;

import java.sql.Timestamp;
import java.util.List;
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
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
            LOGGER.debug("Initialized ProfessionalBO: {}. ProfessionalDAO: {}", getClass().getSimpleName(), professionalDAO);
        }
    }

    public ProfessionalProfile register(AppUser usr, ProfessionalProfile profProfile) throws IllegalAccessException {
        Validator.validateProfessional(profProfile);
        
        // Fetch the user entry and create the Entity objects for inserting into tables Address, Document.
        Professional existing = professionalDAO.findByExtId(usr.principal().sub());
        if (existing == null) {
            throw new IllegalArgumentException("User has to be registered first");
        }
        if (existing.getProfessionalId() != null) {
            throw new IllegalArgumentException("You have already applied as a professional");
        }
        // Get the underlying registered user object.
        User user = existing.getUser();
        if (user.getRole() == User.Role.CUSTOMER) {
            throw new IllegalStateException("You cannot register as both customer and professional");
        }

        // String applicationId = MD5HashGenerator.digest("professional", usr.principal().sub());
        String applicationId = IdGenerator.generate(usr.principal().sub(), profProfile.getDocuments().get(0).getDocumentNumber());
        profProfile.setApplicationId(applicationId);
        profProfile.setUser(user);
        
        return profProfile;
    }

    // User-specific operation
    public Professional view(AppUser usr, String extId) throws IllegalAccessException {
        // Only the logged in professional is allowed to view.
        ensureAuthorized(usr, extId);

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        Professional professional = professionalDAO.findByExtId(extId);
        // How to simulate TBD
        if (professional == null) {
            throw new ResourceNotFoundException("No Professional found for id: " + extId);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professional details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professional;
    }

    // Admin operation to view all Professionals of the system
    public List<Professional> viewAll(AppUser usr, QueryParams params) throws IllegalAccessException {
        ensureAdmin(usr);
        
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

    public Professional modify(AppUser usr, Professional profObj, String extId) throws IllegalAccessException {
        ensureAuthorized(usr, extId);

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // First fetch the entry from db(prof joins user), to see if this already exists.
        Professional existing = professionalDAO.findByExtId(extId);
        if (existing == null) {
            throw new IllegalArgumentException("No professional found for external id: " + profObj.getProfessionalId());
        }

        // Update attributes of existing record
       // LOGGER.info(existing.getUser().getRole() + " FROM DB, User id : " +existing.getUser().getUserId());
        existing.setUserId(existing.getUser().getUserId());
        existing.setBio(profObj.getBio());
        existing.setExperienceYears(profObj.getExperienceYears());
        existing.setServingCities(profObj.getServingCities());

        // TBD:
        // existing.setRatingAvg(profObj.getRatingAvg());
        // existing.setIsVerified(profObj.getIsVerified());

        professionalDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public Professional remove(AppUser usr, String extId) throws IllegalAccessException {
        ensureAuthorized(usr, extId);

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        // First fetch the entry, to see if this already exists.
        Professional professional = professionalDAO.findByExtId(extId);
        if (professional == null) {
            throw new ResourceNotFoundException("No professional found for extId: " + extId);
        }
        professionalDAO.delete(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Professional with extId: {}. Elapsed time(ms): {}", extId, timer.elapsedTimeMillis());
        }
        return professional;
    }

    public Professional create(AppUser usr, Professional professional) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        ensureAdmin(usr);
        validateScope(usr, "user:create");

        User user = professional.getUser();
        if (user != null) {
            user.setExternalId(IdGenerator.generate(user.getPhone1(), user.getEmail()));
            user.setRole(user.getRole() != null ? user.getRole() : User.Role.PROFESSIONAL);
            user.setStatus(User.Status.ACTIVE);
            user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
        if (professional.getCreatedAt() == null) {
            professional.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }
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
}
