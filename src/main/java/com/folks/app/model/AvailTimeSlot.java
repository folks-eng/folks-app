package com.folks.app.model;

import java.sql.Time;

/**
 *
 * @author schan280
 */
public class AvailTimeSlot {
    
    private String id;
    private String label;
    private Integer startHour;
    private Time fromTime;
    private Time toTime;
    private Integer slot = 0;
    private String message;
    
    public AvailTimeSlot() {}

    public AvailTimeSlot(Time fromTime, Time toTime, Integer slot, String message) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.slot = slot;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Time getFromTime() {
        return fromTime;
    }

    public void setFromTime(Time fromTime) {
        this.fromTime = fromTime;
    }

    public Time getToTime() {
        return toTime;
    }

    public void setToTime(Time toTime) {
        this.toTime = toTime;
    }

    public Integer getSlot() {
        return slot;
    }

    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}
