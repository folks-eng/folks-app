package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.dao.AvailabilityDAO;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.dao.ServiceDAO;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.model.Availability;
import com.folks.app.model.Professional;
import com.folks.app.model.Service;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.jpa.JdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AvailabilityMgmtBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityMgmtBO.class);
    
    private final AvailabilityDAO availabilityDAO;
    private final ProfessionalDAO professionalDAO;
    private final ServiceDAO serviceDAO;
    
    private final AvailabilityHelper helper = new AvailabilityHelper();

    public AvailabilityMgmtBO() {
        this.availabilityDAO = DAOProxy.get(AvailabilityDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized AvailabilityMgmtBO: {}. AvailabilityDAO: {}. ProfessionalDAO: {}. ServiceDAO: {}"
                    , getClass().getSimpleName(), availabilityDAO, professionalDAO, serviceDAO);
        }
    }

    public List<Availability> viewProfessionalAvailability(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Integer serviceId = Integer.valueOf(params.param("serviceId"));
        Integer neighbourhoodId = Integer.valueOf(params.param("neighbourhoodId"));
        
        Service service = serviceDAO.find(new Service.ServicePK(serviceId));
        if (service == null) {
            throw new IllegalArgumentException("No service found with id " + serviceId);
        }
        String date = params.param("date");
        String start = params.param("start");
        String end = params.param("end");
        
        List<Availability> records = availabilityDAO.findProfessional(
                serviceId
                , neighbourhoodId
                , date
                , start
                , end);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} eligible availability record(s). Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
        return records;
    }
    
    public Map<String, Integer> generate(AvailabilityGenEvent availEvent) {
        try {
            Object[] dates = availabilityDAO.findMinMaxDate();
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Retrieved minimum date {} and maximum date {}", dates[0], dates[1]);
            }
            Date today = DateUtil.currentUTCDate();
            Date minDate = (Date)dates[0];
            Date maxDate = (Date)dates[1];

            if (availEvent.getProfessionalIds() == null && minDate.equals(today) && maxDate.after(today)) {
                LOGGER.warn("Availability has already been generated. Ignoring the request.");
                return null;
            }

            if (availEvent.getProfessionalIds() != null && !availEvent.getProfessionalIds().isEmpty()) {
                int numberOfDays = availEvent.getNumberOfDays();
                if (minDate.before(today)) {
                    minDate = today;
                    numberOfDays = (int)Math.ceil((maxDate.getTime() - minDate.getTime()) / (1000.0 * 3600 * 24));
                }
                return generateForProfessionals(availEvent.getProfessionalIds(), minDate, numberOfDays);
            }
            else {
                return generateForAll(minDate, maxDate, availEvent.getNumberOfDays());
            }
        }
        catch (JdbcException e) {
            LOGGER.error("Error generating availability records for professionals: " + availEvent.getProfessionalIds(), e);
        }
        return Map.of("profCount", 0, "availCount", 0);
    }
    
    private Map<String, Integer> generateForProfessionals(List<Integer> professionalIds, Date minDate, Integer numberOfDays) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        List<Availability> availabilities = new ArrayList<>(professionalIds.size() * numberOfDays * 9);
        int profCount = 0;
        int availCount = 0;
            
        // Check if the professional ids exist.
        Map<String, List<Object>> params = new HashMap<>();
        params.put("professional_id", professionalIds.stream().map(id -> (Object)id).toList());
        
        List<Professional> professionals = professionalDAO.query(SearchCriteria.from(params));
        
        if (professionals.isEmpty()) {
            LOGGER.warn("No professional found for these ids: {}", professionalIds);
            return Map.of();
        }
        for (Professional professional : professionals) {
            if (professional.getIsVerified() == 0) {
                LOGGER.warn("Professional {} is not yet verified. Cannot generate availability for him", professional.getProfessionalId());
                continue;
            }
            // Check if they have any availability calendar data generated for them.
            // For new professional onboarding, no availability should be present.
            // Therefore, take the minDate as from date.
            List<Availability> tmp = helper.generateAvailability(professional.getProfessionalId(), minDate, numberOfDays);
            availabilities.addAll(tmp);
            availCount += tmp.size();
            profCount ++;
        }
        // Insert the availability records.
        availabilityDAO.insert(availabilities);
        
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Generated {} calendar record(s) for {} supplied professional(s). Elapsed time(ms): {}"
                    , availCount, profCount, timer.elapsedTimeMillis());
        }
        
        return Map.of("profCount", profCount, "availCount", availCount);
    }
    
    private Map<String, Integer> generateForAll(Date minDate, Date maxDate, Integer numberOfDays) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        List<Integer> professionalIds;
        int profCount = 0;
        int availCount = 0;
        
        int limit = 2000;
        List<Availability> availabilities = new ArrayList<>(550 * numberOfDays * 9);

        for (int offset = 0; ; offset += limit) {
            professionalIds = professionalDAO.findProfessionalIds(offset, limit);
            if (professionalIds.isEmpty()) {
                break;
            }
            profCount += professionalIds.size();

            // Generate calendar events for the next few days.
            for (Integer professionalId : professionalIds) {
                List<Availability> tmp = helper.generateAvailability(professionalId, maxDate, numberOfDays);
                availabilities.addAll(tmp);
                availCount += tmp.size();
            }
            // Insert the availability records.
            availabilityDAO.insert(availabilities);
            availabilities.clear();
        }
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Generated {} calendar record(s) for {} professional(s). Elapsed time(ms): {}"
                    , availCount, profCount, timer.elapsedTimeMillis());
        }
        return Map.of("profCount", profCount, "availCount", availCount);
    }
}
