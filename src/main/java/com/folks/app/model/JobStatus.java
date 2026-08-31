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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_job_status")
@IdClass(JobStatus.JobStatusPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "JobStatus.selectAll", query = "SELECT * FROM fks_job_status")
})
public class JobStatus implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false, updatable = false, precision = 32)
    private Integer logId;

    @Column(name = "booking_id", nullable = false, updatable = true, length = 36)
    private String bookingId;

    @Column(name = "status", nullable = false, updatable = true, length = 32)
    private String status;

    @Column(name = "updated_by", nullable = true, updatable = true, precision = 32)
    private Integer updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public JobStatus() {}

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getLogId() {
        return this.logId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getUpdatedBy() {
        return this.updatedBy;
    }

    public static class JobStatusPK {

        private Integer logId;

        public JobStatusPK() {}

        public JobStatusPK(Integer logId) {
            this.logId = logId;
        }

        public void setLogId(Integer logId) {
            this.logId = logId;
        }

        public Integer getLogId() {
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