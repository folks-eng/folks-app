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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_job_status")
@IdClass(JobStatus.JobStatusPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "JobStatus.selectAll", query = "SELECT * FROM fks_job_status")
})
public class JobStatus implements Serializable, Cloneable {

    @Id
    @Column(name = "log_id", nullable = false, updatable = false, precision = 64)
    private Long logId;

    @Column(name = "booking_id", nullable = false, updatable = true, precision = 64)
    private BigInteger bookingId;

    @Column(name = "status", nullable = false, updatable = true, length = 32)
    private String status;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    @Column(name = "updated_by", nullable = true, updatable = true, precision = 64)
    private BigInteger updatedBy;

    public JobStatus() {}

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getLogId() {
        return this.logId;
    }

    public void setBookingId(BigInteger bookingId) {
        this.bookingId = bookingId;
    }

    public BigInteger getBookingId() {
        return this.bookingId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedBy(BigInteger updatedBy) {
        this.updatedBy = updatedBy;
    }

    public BigInteger getUpdatedBy() {
        return this.updatedBy;
    }

    public static class JobStatusPK {

        private Long logId;

        public JobStatusPK() {}

        public JobStatusPK(Long logId) {
            this.logId = logId;
        }

        public void setLogId(Long logId) {
            this.logId = logId;
        }

        public Long getLogId() {
            return this.logId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.logId);
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
            final JobStatusPK other = (JobStatusPK)obj;
            if (! Objects.equals(this.logId, other.logId)) {
                return false;
            }
            return true;
        }

    }
}