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
@Table(name = "fks_audit_logs")
@IdClass(AuditLog.AuditLogPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "AuditLog.selectAll", query = "SELECT * FROM fks_audit_logs")
})
public class AuditLog implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false, updatable = false, precision = 32)
    private Integer logId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 32)
    private Integer userId;

    @Column(name = "action", nullable = false, updatable = true, length = 64)
    private String action;

    @Column(name = "entity_type", nullable = false, updatable = true, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false, updatable = true, precision = 32)
    private Integer entityId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public AuditLog() {}

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public Integer getLogId() {
        return this.logId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getEntityId() {
        return this.entityId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class AuditLogPK {

        private Integer logId;

        public AuditLogPK() {}

        public AuditLogPK(Integer logId) {
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
            final AuditLogPK other = (AuditLogPK)obj;
            if (! Objects.equals(this.logId, other.logId)) {
                return false;
            }
            return true;
        }

    }
}