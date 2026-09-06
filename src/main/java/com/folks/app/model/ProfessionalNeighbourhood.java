package com.folks.app.model;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "fks_professional_neighbourhoods")
@IdClass(ProfessionalNeighbourhood.ProfessionalNeighbourhoodPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "ProfessionalNeighbourhood.selectAll", query = "SELECT * FROM fks_professional_neighbourhoods")
})
public class ProfessionalNeighbourhood implements Serializable, Cloneable {

    public static enum Status {
        PACTIVE,
        INACTIVE;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, precision = 32)
    private Integer id;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 32)
    private Integer professionalId;

    @Column(name = "neighbourhood_id", nullable = false, updatable = true, precision = 32)
    private Integer neighbourhoodId;

    @Column(name = "status", nullable = false, updatable = true, check = @CheckConstraint(constraint = "status IN ('PACTIVE', 'INACTIVE')"))
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public ProfessionalNeighbourhood() {}

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
    }

    public Integer getProfessionalId() {
        return this.professionalId;
    }

    public void setNeighbourhoodId(Integer neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }

    public Integer getNeighbourhoodId() {
        return this.neighbourhoodId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public static class ProfessionalNeighbourhoodPK {

        private Integer id;

        public ProfessionalNeighbourhoodPK() {}

        public ProfessionalNeighbourhoodPK(Integer id) {
            this.id = id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return this.id;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.id);
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
            final ProfessionalNeighbourhoodPK other = (ProfessionalNeighbourhoodPK)obj;
            if (! Objects.equals(this.id, other.id)) {
                return false;
            }
            return true;
        }

    }
}