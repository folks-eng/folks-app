package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.AvailabilityDAO;
import com.folks.app.dao.ServiceDAO;
import com.folks.app.model.AvailTimeSlot;
import com.folks.app.model.Availability;
import com.folks.app.model.Service;
import com.folks.app.model.Service.ServicePK;
import com.folks.app.util.AvailSlotUtil;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AvailabilityBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityBO.class);
    
    private final AvailabilityDAO availabilityDAO;
    private final ServiceDAO serviceDAO;

    public AvailabilityBO() {
        this.availabilityDAO = DAOProxy.get(AvailabilityDAO.class);
        this.serviceDAO = DAOProxy.get(ServiceDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized AvailabilityBO: {}. AvailabilityDAO: {}. ServiceDAO: {}", getClass().getSimpleName(), availabilityDAO, serviceDAO);
        }
    }

    public Availability create(AppUser usr, Availability availability) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        availabilityDAO.insert(availability);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Availability created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return availability;
    }

    public void create(AppUser usr, List<Availability> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        availabilityDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Availability record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Availability modify(AppUser usr, Availability availability) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Availability existing = availabilityDAO.find(new Availability.AvailabilityPK(availability.getAvailabilityId()));
        if (existing == null) {
            throw new IllegalArgumentException("No availability found for identifier: " + availability.getAvailabilityId());
        }
        // Update attributes of existing record
        existing.setProfessionalId(availability.getProfessionalId());
        existing.setDate(availability.getDate());
        existing.setStartTime(availability.getStartTime());
        existing.setEndTime(availability.getEndTime());
        existing.setIsBooked(availability.getIsBooked());

        availabilityDAO.update(availability);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Availability record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Availability> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Availability> rows = availabilityDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded availability record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }
    
    public List<AvailTimeSlot> viewSlotAvailability(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Integer serviceId = Integer.valueOf(params.param("serviceId"));
        String dt = params.params("date").get(0);

        Service service = serviceDAO.find(new ServicePK(serviceId));
        if (service == null) {
            throw new IllegalArgumentException("No service found with id " + serviceId);
        }
        List<AvailTimeSlot> slots = availabilityDAO.findAvailability(
                service.getServiceId()
                , service.getDurationMinutes()
                , Date.valueOf(LocalDate.parse(dt)));

        slots = AvailSlotUtil.addMissingIntervals(slots, "09:00:00", "18:00:00", service.getDurationMinutes());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (AvailTimeSlot slot : slots) {
            slot.setLabel(slot.getFromTime().toLocalTime().format(formatter) + " - " + slot.getToTime().toLocalTime().format(formatter));
            slot.setId("sl_" + slot.getLabel());
            slot.setStartHour(slot.getFromTime().toLocalTime().getHour());
        }
        return slots;
    }

    public List<Availability> viewProfessionalAvailability(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Integer serviceId = Integer.valueOf(params.param("serviceId"));
        String dt = params.params("date").get(0);

        Service service = serviceDAO.find(new ServicePK(serviceId));
        if (service == null) {
            throw new IllegalArgumentException("No service found with id " + serviceId);
        }
        String date = params.param("date");
        String start = params.param("start");
        String end = params.param("end");
        Boolean fair = Boolean.valueOf(params.param("fair"));
        
        List<Availability> records = availabilityDAO.findProfessional(
                serviceId
                , date
                , start
                , end);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} eligible availability record(s). Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
        return records;
    }

    public Availability view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Availability availability = availabilityDAO.find(new Availability.AvailabilityPK(id));
        if (availability == null) {
            throw new IllegalArgumentException("No Availability found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched availability details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return availability;
    }

    public Availability remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Availability availability = availabilityDAO.find(new Availability.AvailabilityPK(id));

        if (availability == null) {
            throw new IllegalArgumentException("No availability found for id: " + id);
        }
        availabilityDAO.delete(availability);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Availability. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return availability;
    }
}
