package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.dao.AvailabilityDAO;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.event.AvailabilityGenEvent;
import com.folks.app.model.Availability;
import com.folks.app.model.Professional;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AvailabilityGenBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityGenBO.class);
    
    private final AvailabilityDAO availabilityDAO;
    private final ProfessionalDAO professionalDAO;
    
    private final AvailabilityHelper helper = new AvailabilityHelper();

    public AvailabilityGenBO() {
        this.availabilityDAO = DAOProxy.get(AvailabilityDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized AvailabilityBO: {}. AvailabilityDAO: {}. ProfessionalDAO: {}"
                    , getClass().getSimpleName(), availabilityDAO, professionalDAO);
        }
    }
    
    public Map<String, Integer> generate(AvailabilityGenEvent availEvent) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
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
        
        int profCount = 0;
        int availCount = 0;
        List<Integer> professionalIds = null;
        
        if (availEvent.getProfessionalIds() != null && !availEvent.getProfessionalIds().isEmpty()) {
            professionalIds = availEvent.getProfessionalIds();
            List<Availability> availabilities = new ArrayList<>(professionalIds.size() * availEvent.getNumberOfDays() * 9);
            
            // Check if the professional ids exist.
            SearchCriteria search = SearchCriteria.from(new QueryParams(Map.of("professionalId", professionalIds.stream().map(String::valueOf).toList())));
            List<Professional> professionals = professionalDAO.query(search);
            if (professionals.isEmpty()) {
                LOGGER.warn("No professional found for these ids: {}", professionalIds);
                return null;
            }
            for (Professional professional : professionals) {
                if (professional.getIsVerified() == 0) {
                    LOGGER.warn("Professional {} is not yet verified. Cannot generate availability for him", professional.getProfessionalId());
                    continue;
                }
                // Check if they have any availability calendar data generated for them.
                // For new professional onboarding, no availability should be present.
                // Therefore, take the minDate as from date.
                List<Availability> tmp = helper.generateAvailability(professional.getProfessionalId(), minDate, availEvent.getNumberOfDays());
                availabilities.addAll(tmp);
                availCount += tmp.size();
                profCount ++;
            }
            // Insert the availability records.
            availabilityDAO.insert(availabilities);
        }
        else {
            int limit = 2000;
            List<Availability> availabilities = new ArrayList<>(550 * availEvent.getNumberOfDays() * 9);

            for (int offset = 0; ; offset += limit) {
                professionalIds = professionalDAO.findProfessionalIds(offset, limit);
                if (professionalIds.isEmpty()) {
                    break;
                }
                profCount += professionalIds.size();

                // Generate calendar events for the next few days.
                for (Integer professionalId : professionalIds) {
                    List<Availability> tmp = helper.generateAvailability(professionalId, maxDate, availEvent.getNumberOfDays());
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
        }
        return Map.of("profCount", profCount, "availCount", availCount);
    }
}
