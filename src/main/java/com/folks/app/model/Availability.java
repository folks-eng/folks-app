package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_availability")
@IdClass(Availability.AvailabilityPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Availability.selectAll", query = "SELECT * FROM fks_availability")
})
public class Availability implements Serializable, Cloneable {

    @Id
    @Column(name = "availability_id", nullable = false, updatable = false, precision = 64)
    private Long availabilityId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 64)
    private BigInteger professionalId;

    @Column(name = "date", nullable = false, updatable = true)
    private Date date;

    @Column(name = "start_time", nullable = true, updatable = true)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = true, updatable = true)
    private Timestamp endTime;

    @Column(name = "is_booked", nullable = false, updatable = true, precision = 16)
    private Short isBooked;

    public Availability() {}

    public void setAvailabilityId(Long availabilityId) {
        this.availabilityId = availabilityId;
    }

    public Long getAvailabilityId() {
        return this.availabilityId;
    }

    public void setProfessionalId(BigInteger professionalId) {
        this.professionalId = professionalId;
    }

    public BigInteger getProfessionalId() {
        return this.professionalId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return this.date;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getStartTime() {
        return this.startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }

    public void setIsBooked(Short isBooked) {
        this.isBooked = isBooked;
    }

    public Short getIsBooked() {
        return this.isBooked;
    }

    public static class AvailabilityPK {

        private Long availabilityId;

        public AvailabilityPK() {}

        public AvailabilityPK(Long availabilityId) {
            this.availabilityId = availabilityId;
        }

        public void setAvailabilityId(Long availabilityId) {
            this.availabilityId = availabilityId;
        }

        public Long getAvailabilityId() {
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