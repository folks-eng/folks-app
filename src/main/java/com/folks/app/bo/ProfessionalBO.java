package com.folks.app.bo;

import com.folks.app.dao.*;
import com.folks.app.model.*;
import jakarta.persistence.NoResultException;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.util.Constants;
import com.folks.app.util.IdGenerator;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import com.folks.app.util.Validator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private final ServiceDAO serviceDAO;

    private final ProfessionalServiceDAO profServiceDAO;

    public ProfessionalBO() {
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        this.profServiceDAO = DAOProxy.get(ProfessionalServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalBO: {}. ProfessionalDAO: {}", getClass().getSimpleName(), professionalDAO);
        }
    }

    public ProfessionalProfile register(AppUser usr, ProfessionalProfile profProfile) {
        Validator.validateProf(profProfile, false);
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        /* First fetch the entry from db (prof joins user), to see if Prof already exists.
        and create the Entity objects for inserting into tables Address, Document.
         */
        Professional existing = professionalDAO.findByExtId(usr.principal().sub());
        // TBD : how to simulate this error?
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
        LOGGER.debug("Registered User identifier " +user.getUserId());

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
        
        professionalDAO.insertProfessional(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional onboarded successfully with Id {}, Elapsed time(ms): {}"
                    , professional.getProfessionalId(), timer.elapsedTimeMillis());
        }
        // Set the applicationId before sending the response. Pros may use this id for further followup.
        profProfile.setApplicationId(applicationId);

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

    public ProfessionalProfile modify(AppUser usr, ProfessionalProfile profObj, String extId) throws IllegalAccessException {
        ensureAuthorized(usr, extId);
        Validator.validateProf(profObj, true);

        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // First fetch the entry from db(prof joins user), to see if this already exists.
        Professional existingProf = professionalDAO.findByExtId(extId);
        if (existingProf == null) {
            throw new ResourceNotFoundException("No professional found for external id: " +extId);
        }
        List<ProfessionalService> psListToBeDel = fetchProfServices(existingProf.getProfessionalId());
        LOGGER.debug("Professional services existing with this professional " +psListToBeDel.size());

        // Properties modifiable
        existingProf.setBio(profObj.getBio());
        existingProf.setExperienceYears(profObj.getExperienceYears());
        existingProf.setServingCities(profObj.getServingCities());
        existingProf.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        //Create the new list of Professional Services to be set to the Professional during modify
        List<ProfessionalService> newPSList = buildNewProfServices(existingProf.getProfessionalId(),
                                                                    existingProf.getUpdatedAt(), profObj.getExpertise());
        existingProf.setProfServices(newPSList);

        professionalDAO.updateProfessional(existingProf, psListToBeDel);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional updated successfully with Id {}, Elapsed time(ms): {}"
                    , existingProf.getProfessionalId(), timer.elapsedTimeMillis());
        }
        return profObj;
    }

    private List<ProfessionalService> buildNewProfServices(Integer profID, Timestamp profUpdatedAt, List<Integer> newExpertise) {
        List<Service> newServiceList = fetchServices(newExpertise);

        // Assign individual services to this professional's profile
        List<ProfessionalService> newPSList = new ArrayList<>(newServiceList.size());
        for(Service service: newServiceList) {
            ProfessionalService pService = new ProfessionalService();
            pService.setProfessionalId(profID);
            pService.setServiceId(service.getServiceId());
            pService.setPrice(service.getBasePrice());
            pService.setIsActive(Constants.PROF_SERVICE_ACTIVE);
            pService.setCreatedAt(profUpdatedAt);

            newPSList.add(pService);
        }
        return newPSList;
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

    private List<ProfessionalService> fetchProfServices(Integer profId) {
        Map<String, List<String>> params = new HashMap<>();
        List<String> idList = new ArrayList<>(1);
        idList.add(String.valueOf(profId));
        params.put("professionalId", idList);

        SearchCriteria search = SearchCriteria.from(new QueryParams(params));
        List<ProfessionalService> psList = profServiceDAO.query(search);
        return psList;
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
