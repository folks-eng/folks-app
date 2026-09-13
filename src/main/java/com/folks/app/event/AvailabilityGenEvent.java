package com.folks.app.event;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sudip
 */
public class AvailabilityGenEvent {
    
    private List<Integer> professionalIds;
    private Integer numberOfDays = 5;
    
    public static AvailabilityGenEvent from(Map<String, Object> payload) {
        AvailabilityGenEvent event = new AvailabilityGenEvent();
        
        if (payload.containsKey("numberOfDays")) {
            event.numberOfDays = (Integer)payload.get("numberOfDays");
        }
        if (payload.containsKey("professionalIds")) {
            event.professionalIds = (List<Integer>)payload.get("professionalIds");
        }
        if (payload.containsKey("professionalId")) {
            event.professionalIds = Arrays.asList((Integer)payload.get("professionalId"));
        }
        return event;
    }

    public List<Integer> getProfessionalIds() {
        return professionalIds;
    }

    public Integer getNumberOfDays() {
        return numberOfDays;
    }

    @Override
    public String toString() {
        return "[ProfessionalIds: " + professionalIds + ", NumberOfDays: " + numberOfDays + "]";
    }
}
