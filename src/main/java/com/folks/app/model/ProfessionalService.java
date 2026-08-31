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
@Table(name = "fks_professional_services")
@IdClass(ProfessionalService.ProfessionalServicePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "ProfessionalService.selectAll", query = "SELECT * FROM fks_professional_services")
})
public class ProfessionalService implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, precision = 32)
    private Integer id;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 32)
    private Integer professionalId;

    @Column(name = "service_id", nullable = false, updatable = true, precision = 32)
    private Integer serviceId;

    @Column(name = "price", nullable = false, updatable = true, precision = 7, scale = 2)
    private Double price;

    @Column(name = "is_active", nullable = false, updatable = true, precision = 16)
    private Short isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public ProfessionalService() {}

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

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceId() {
        return this.serviceId;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

    public Short getIsActive() {
        return this.isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class ProfessionalServicePK {

        private Integer id;

        public ProfessionalServicePK() {}

        public ProfessionalServicePK(Integer id) {
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
            final ProfessionalServicePK other = (ProfessionalServicePK)obj;
            if (! Objects.equals(this.id, other.id)) {
                return false;
            }
            return true;
        }

    }
}