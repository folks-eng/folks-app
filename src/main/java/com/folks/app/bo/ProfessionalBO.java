package com.folks.app.bo;

import com.folks.app.dao.*;
import com.folks.app.model.*;
import com.folks.app.util.*;
import jakarta.persistence.NoResultException;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.util.IdGenerator;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalBO.class);

    private final ProfessionalDAO professionalDAO;

    private final ServiceDAO serviceDAO;

    public ProfessionalBO() {
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalBO: {}. ProfessionalDAO: {}", getClass().getSimpleName(), professionalDAO);
        }
    }

    public ProfessionalProfile register(AppUser usr, ProfessionalProfile profProfile) throws IllegalAccessException {
        Validator.validateProf(profProfile);
        StopWatch timer = StopWatch.newTimer();
        timer.start();

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
        String applicationId = UUID.randomUUID().toString();
        Timestamp createdAt = new Timestamp(DateUtil.currentUTCDate().getTime());

        // Build the professional details.
        Professional professional = new Professional();
        professional.setUserId(user.getUserId());
        professional.setExperienceYears(profProfile.getExperienceYears());
        professional.setServingCities(profProfile.getServingCities());
        professional.setIsVerified(Constants.PROF_NOT_VERIFIED);
        professional.setCreatedAt(createdAt);
        professional.setUser(user);
        
        // Build the local address.
        Address localAddress = profProfile.getAddress();
        localAddress.setUserId(user.getUserId());
        if (localAddress.getLabel() == null) {
            localAddress.setLabel(Constants.DEFAULT_LABEL);
        }
        if (localAddress.getIsDefault() == null) {
            localAddress.setIsDefault(Constants.IS_DEFAULT_ADDR);     // All addresses are set to default.
        }
        localAddress.setCreatedAt(createdAt);
        user.setAddresses(List.of(localAddress));
        
        // Build the document parts.
        List<Document> documents = profProfile.getDocuments();
        for (Document doc : documents) {
            doc.setUserId(user.getUserId());
            doc.setApplicationId(applicationId);
            doc.setVerificationStatus(Document.Verificationstatus.PENDING);
            doc.setCreatedAt(createdAt);
        }
        user.setDocuments(documents);
        
        // Build the professional vs services mapping.
        List<Service> services = fetchServices(profProfile.getExpertise());
        
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
        professional.setProfServices(pServices);
        
        professionalDAO.insertProfile(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional onboarded successfully with Id {}, Elapsed time(ms): {}"
                    , professional.getProfessionalId(), timer.elapsedTimeMillis());
        }
        // Set the applicationId before sending the response. Pros may use this id for further followup.
        profProfile.setApplicationId(applicationId);

        return profProfile;
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

    public Professional view(AppUser usr, String extId) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Only the logged in professional is allowed to view.
        ensureAuthorized(usr, extId);
        User userFromDb = userDAO.findByExtId(extId);
        if(userFromDb == null)
            throw new ResourceNotFoundException("No user found for id: " + extId);
        List<Professional> profList = getProfessionals(userFromDb.getUserId());
        Professional professional = professionalDAO.findByExtId(extId);
        if (professional == null) {
            throw new ResourceNotFoundException("No Professional found for id: " + extId);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched professional details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return professional;
    }

    private List<Professional> getProfessionals(Integer userId) {
        Map<String, List<String>> param = new HashMap<>();
        param.put("userId", List.of(String.valueOf(userId)));
        SearchCriteria search = SearchCriteria.from(new QueryParams(param));
        List<Professional> profList = professionalDAO.query(search);
        return profList;
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
     * @throws ResourceNotFoundException if no user exists for the external identifier
     */
    private User fetchUser(String extUserId) {
        try {
            return userDAO.findByExtId(extUserId);
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("No User found for id: " + extUserId);
        }
    }

    private List<Service> fetchServices(List<Integer> expertise) {
        try {
            List<String> subCategoryIds = new ArrayList<>(expertise.size());
            for (Integer subCategoryId : expertise) {
                subCategoryIds.add(String.valueOf(subCategoryId));
            }
            Map<String, List<String>> param = new HashMap<>();
            param.put("categoryId", subCategoryIds);

            SearchCriteria search = SearchCriteria.from(new QueryParams(param));
            List<Service> services = serviceDAO.query(search);
            
            if (services.size() < subCategoryIds.size()) {
                throw new IllegalArgumentException("Invalid non-existent sub-categories provided.");
            }
            return services;
        }
        catch (NoResultException e) {
            throw new ResourceNotFoundException("No Service found for the sub-categories: " + expertise);
        }
    }
}
