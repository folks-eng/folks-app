package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.cache.impl.NeighbourhoodCache;
import com.folks.app.cache.impl.ServiceCache;
import com.folks.app.dao.DocumentDAO;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.model.Address;
import com.folks.app.model.Document;
import com.folks.app.model.Neighbourhood;
import com.folks.app.model.Professional;
import com.folks.app.model.ProfessionalNeighbourhood;
import com.folks.app.model.ProfessionalProfile;
import com.folks.app.model.ProfessionalService;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.AddressUtil;
import com.folks.app.util.Constants;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class ProfessionalMgmtBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfessionalMgmtBO.class);

    private final ProfessionalDAO professionalDAO;
    private final DocumentDAO documentDAO;

    public ProfessionalMgmtBO() {
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        this.documentDAO = DAOProxy.get(DocumentDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized ProfessionalMgmtBO: {}. ProfessionalDAO: {}. DocumentDAO: {}"
                    , getClass().getSimpleName(), professionalDAO, documentDAO);
        }
    }

    public void register(ProfessionalProfile profProfile) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Timestamp createdAt = new Timestamp(DateUtil.currentUTCDate().getTime());
        User user = profProfile.getUser();

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
        AddressUtil.enrich(localAddress);
        
        user.setAddresses(List.of(localAddress));
        
        
        // Build the document parts.
        List<Document> documents = profProfile.getDocuments();
        for (Document doc : documents) {
            doc.setUserId(user.getUserId());
            doc.setApplicationId(profProfile.getApplicationId());
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
        
        // Build the professional serving localities.
        List<Integer> nbhoodIds = profProfile.getNeighbourhoodIds();
        
        // -1 indicates "ALl Localities", in which case fetch all localities based on the city id.
        if (nbhoodIds.get(0).equals(-1)) {
            nbhoodIds = new ArrayList<>(250);
            for (Neighbourhood nbhood : NeighbourhoodCache.getCache().getAllValues()) {
                if (nbhood.getCityId().equals(nbhood.getCityId())) {
                    nbhoodIds.add(nbhood.getNeighbourhoodId());
                }
            }
        }
        List<ProfessionalNeighbourhood> profLocalities = new ArrayList<>(nbhoodIds.size());

        for (Integer nbhoodId : nbhoodIds) {
            ProfessionalNeighbourhood profLocality = new ProfessionalNeighbourhood();
            profLocality.setNeighbourhoodId(nbhoodId);
            profLocality.setStatus(ProfessionalNeighbourhood.Status.ACTIVE);
            profLocality.setCreatedAt(createdAt);

            profLocalities.add(profLocality);
        }
        professional.setProfNeighbourhoods(profLocalities);
        
        professionalDAO.insertProfile(professional);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Professional onboarded successfully with Id {}, Elapsed time(ms): {}"
                    , professional.getProfessionalId(), timer.elapsedTimeMillis());
        }
    }
    
    public Professional approveProfessional(AppUser usr, Map<String, String> payload) throws IllegalAccessException {
        ensureAdmin(usr);
        
        // Approval process:
        // 1. Update the verification status in document store.
        // 2. Update the verified status in professional store.
        // 3. Generate professional calendar.
        String extId = payload.get("externalId");
        String applicationId = payload.get("applicationId");
        String status = payload.get("status");          // APPROVED/ REJECTED
        
        Professional professional = professionalDAO.findByExtId(extId);
        if (professional == null) {
            throw new IllegalArgumentException("No such professional with id " + extId + " exists");
        }
        // Fetch the document
        Timestamp updatedAt = new Timestamp(DateUtil.currentUTCDate().getTime());
        List<Document> documents = documentDAO.query(SearchCriteria.from(professional.getUserId()));
        
        for (Document document : documents) {
            if (document.getApplicationId().equals(applicationId)) {
                document.setVerificationStatus(Document.Verificationstatus.APPROVED);
                document.setUpdatedAt(updatedAt);
                documentDAO.update(document);
            }
        }
        // Update profession status
        professional.setIsVerified((short)1);
        professionalDAO.update(professional);
        
        return professional;
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
