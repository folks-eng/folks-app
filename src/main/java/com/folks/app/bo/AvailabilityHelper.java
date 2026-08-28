package com.folks.app.bo;

import com.folks.app.model.AvailTimeSlot;
import com.folks.app.model.Availability;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 *
 * @author schan280
 */
public class AvailabilityHelper {
    
    AvailabilityHelper() {}
    
    List<AvailTimeSlot> addMissingIntervals(
            List<AvailTimeSlot> slots
            , String dayStart
            , String dayEnd
            , int durationMin) {

        List<AvailTimeSlot> result = new ArrayList<>();

        LocalTime start = LocalTime.parse(dayStart);
        LocalTime end = LocalTime.parse(dayEnd);
        Duration duration = Duration.ofHours((long)Math.ceil(durationMin / 60.0));
        
        for (AvailTimeSlot slot : slots) {
            LocalTime from = slot.getFromTime().toLocalTime();
            LocalTime to = slot.getToTime().toLocalTime();
            
            for (LocalTime tmp = start; tmp.isBefore(from); tmp = tmp.plusHours(1)) {
                AvailTimeSlot booked = new AvailTimeSlot();
                booked.setFromTime(Time.valueOf(tmp));
                booked.setToTime(Time.valueOf(tmp.plusHours(duration.toHours())));
                booked.setMessage("Booked");

                result.add(booked);
                start = start.plusHours(1);
            }
            result.add(slot);
            start = start.plusHours(1);
        }
        for (LocalTime tmp = start; tmp.isBefore(end); tmp = tmp.plusHours(1)) {
            if (tmp.plusHours(duration.toHours()).isAfter(end)) {
                break;
            }
            AvailTimeSlot booked = new AvailTimeSlot();
            booked.setFromTime(Time.valueOf(tmp));
            booked.setToTime(Time.valueOf(tmp.plusHours(duration.toHours())));
            booked.setMessage("Booked");

            result.add(booked);
            start = start.plusHours(1);
        }
        return result;
    }
    
    List<Availability> generateAvailability(
            Integer professionalId,
            Date startDate,
            int numberOfDays) {

        List<Availability> availabilities = new ArrayList<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        for (int day = 0; day < numberOfDays; day++) {
            Date currentDate = calendar.getTime();

            // Generate slots from 9 AM to 5 PM
            for (int hour = 9; hour < 17; hour++) {
                Availability availability = new Availability();

                availability.setProfessionalId(professionalId);
                availability.setDate(new java.sql.Date(currentDate.getTime()));

                availability.setStartTime(Time.valueOf(String.format("%02d:00:00", hour)));
                availability.setEndTime(Time.valueOf(String.format("%02d:00:00", hour + 1)));

                // 0 = available, 1 = booked
                availability.setIsBooked((short) 0);

                availabilities.add(availability);
            }
            // Move to next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return availabilities;
    }
}
