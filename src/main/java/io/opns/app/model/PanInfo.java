package io.opns.app.model;

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
@Table(name = "pan_info")
@IdClass(PanInfo.PanInfoPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "PanInfo.selectAll", query = "SELECT * FROM pan_info")
})
public class PanInfo implements Serializable, Cloneable {

    @Id
    @Column(name = "pan_hash", nullable = false, updatable = false, length = 32)
    private String panHash;

    @Column(name = "pan_ref_id", nullable = false, updatable = true, length = 64)
    private String panRefId;

    @Column(name = "status", nullable = false, updatable = true, length = 16)
    private String status;

    @Column(name = "created_date", nullable = false, updatable = true)
    private Timestamp createdDate;

    @Column(name = "last_accessed_date", nullable = true, updatable = true)
    private Timestamp lastAccessedDate;

    @Column(name = "access_count", nullable = false, updatable = true, precision = 64)
    private BigInteger accessCount;

    public PanInfo() {}

    public void setPanHash(String panHash) {
        this.panHash = panHash;
    }

    public String getPanHash() {
        return this.panHash;
    }

    public void setPanRefId(String panRefId) {
        this.panRefId = panRefId;
    }

    public String getPanRefId() {
        return this.panRefId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getCreatedDate() {
        return this.createdDate;
    }

    public void setLastAccessedDate(Timestamp lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }

    public Timestamp getLastAccessedDate() {
        return this.lastAccessedDate;
    }

    public void setAccessCount(BigInteger accessCount) {
        this.accessCount = accessCount;
    }

    public BigInteger getAccessCount() {
        return this.accessCount;
    }

    public static class PanInfoPK {

        private String panHash;

        public PanInfoPK() {}

        public PanInfoPK(String panHash) {
            this.panHash = panHash;
        }

        public void setPanHash(String panHash) {
            this.panHash = panHash;
        }

        public String getPanHash() {
            return this.panHash;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.panHash);
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
            final PanInfoPK other = (PanInfoPK)obj;
            if (! Objects.equals(this.panHash, other.panHash)) {
                return false;
            }
            return true;
        }

    }
}