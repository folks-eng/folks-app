package com.folks.app.bo;

import com.folks.app.model.AvailTimeSlot;
import com.folks.app.model.Availability;
import java.sql.Time;
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

        Time start = Time.valueOf(dayStart);
        Time end = Time.valueOf(dayEnd);
        Long durationHr = (long)Math.ceil(durationMin / 60.0);
        
        for (AvailTimeSlot slot : slots) {
            Time from = slot.getFromTime();
            Time to = slot.getToTime();
            
            for (Time tmp = start; tmp.before(from); tmp = new Time(tmp.getTime() + 60 * 60 * 1000)) {
                AvailTimeSlot booked = new AvailTimeSlot();
                booked.setFromTime(tmp);
                booked.setToTime(new Time(tmp.getTime() + durationHr * 60 * 60 * 1000));
                booked.setMessage("Booked");

                result.add(booked);
                start = new Time(start.getTime() + 60 * 60 * 1000);
            }
            result.add(slot);
            start = new Time(start.getTime() + 60 * 60 * 1000);
        }
        for (Time tmp = start; tmp.before(end); tmp = new Time(tmp.getTime() + 60 * 60 * 1000)) {
            Time toTime = new Time(tmp.getTime() + durationHr * 60 * 60 * 1000);
            if (toTime.after(end)) {
                break;
            }
            AvailTimeSlot booked = new AvailTimeSlot();
            booked.setFromTime(tmp);
            booked.setToTime(toTime);
            booked.setMessage("Booked");

            result.add(booked);
            start = new Time(start.getTime() + 60 * 60 * 1000);
        }
        return result;
    }
    
    List<Availability> generateAvailability(
            Integer professionalId,
            Date currentDate,
            int numberOfDays) {

        List<Availability> availabilities = new ArrayList<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        for (int day = 0; day < numberOfDays; day++) {
            Date startDate = calendar.getTime();

            // Generate slots from 9 AM to 5 PM
            for (int hour = 9; hour < 18; hour++) {
                Availability availability = new Availability();

                availability.setProfessionalId(professionalId);
                availability.setDate(new java.sql.Date(startDate.getTime()));

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
