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
import java.math.BigDecimal;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
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
    @Column(name = "id", nullable = false, updatable = false, precision = 64)
    private Long id;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 64)
    private Long professionalId;

    @Column(name = "service_id", nullable = false, updatable = true, precision = 64)
    private Long serviceId;

    @Column(name = "price", nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false, updatable = true, precision = 16)
    private Short isActive;

    public ProfessionalService() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public Long getProfessionalId() {
        return this.professionalId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

    public Short getIsActive() {
        return this.isActive;
    }

    public static class ProfessionalServicePK {

        private Long id;

        public ProfessionalServicePK() {}

        public ProfessionalServicePK(Long id) {
            this.id = id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
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