package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_availabilities")
@IdClass(Availability.AvailabilityPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Availability.selectAll", query = "SELECT * FROM fks_availabilities"),
    @NamedNativeQuery(name = "Availability.selectMinMaxDate", query = "SELECT MIN(date), MAX(date) FROM fks_availabilities"),
    @NamedNativeQuery(name = "Availability.selectSlotByStartTime"
            , query = """
                      SELECT a.service_id, a.name, a.duration_minutes, b.professional_id, c.date, c.start_time, c.end_time, c.is_booked
                        FROM fks_services a
                       INNER JOIN fks_professional_services b ON (a.service_id = b.service_id)
                       INNER JOIN fks_availabilities c ON (b.professional_id = c.professional_id AND c.date = ? AND c.start_time >= TIME ? AND c.end_time <= c.start_time + CEIL(a.duration_minutes / 60.0) * INTERVAL '1 hour' AND c.is_booked = 0)
                       WHERE a.service_id = ?
                      """)
})
public class Availability implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id", nullable = false, updatable = false, precision = 32)
    private Integer availabilityId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 32)
    private Integer professionalId;

    @Column(name = "date", nullable = false, updatable = true)
    private Date date;

    @Column(name = "start_time", nullable = true, updatable = true)
    private Time startTime;

    @Column(name = "end_time", nullable = true, updatable = true)
    private Time endTime;

    @Column(name = "is_booked", nullable = false, updatable = true, precision = 16)
    private Short isBooked;

    public Availability() {}

    public void setAvailabilityId(Integer availabilityId) {
        this.availabilityId = availabilityId;
    }

    public Integer getAvailabilityId() {
        return this.availabilityId;
    }

    public void setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
    }

    public Integer getProfessionalId() {
        return this.professionalId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getStartTime() {
        return this.startTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Time getEndTime() {
        return this.endTime;
    }

    public void setIsBooked(Short isBooked) {
        this.isBooked = isBooked;
    }

    public Short getIsBooked() {
        return this.isBooked;
    }

    public static class AvailabilityPK {

        private Integer availabilityId;

        public AvailabilityPK() {}

        public AvailabilityPK(Integer availabilityId) {
            this.availabilityId = availabilityId;
        }

        public void setAvailabilityId(Integer availabilityId) {
            this.availabilityId = availabilityId;
        }

        public Integer getAvailabilityId() {
            return this.availabilityId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.availabilityId);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AvailabilityPK other = (AvailabilityPK)obj;
            if (! Objects.equals(this.availabilityId, other.availabilityId)) {
                return false;
            }
            return true;
        }

    }
}