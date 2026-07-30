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
@Table(name = "fks_services")
@IdClass(Service.ServicePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Service.selectAll", query = "SELECT * FROM fks_services")
})
public class Service implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false, updatable = false, precision = 64)
    private Long serviceId;

    @Column(name = "category_id", nullable = false, updatable = true, precision = 64)
    private Long categoryId;

    @Column(name = "name", nullable = false, updatable = true, length = 128)
    private String name;

    @Column(name = "description", nullable = true, updatable = true, length = 1000000000)
    private String description;

    @Column(name = "base_price", nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "duration_minutes", nullable = true, updatable = true, precision = 32)
    private Integer durationMinutes;

    public Service() {}

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getServiceId() {
        return this.serviceId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getBasePrice() {
        return this.basePrice;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getDurationMinutes() {
        return this.durationMinutes;
    }

    public static class ServicePK {

        private Long serviceId;

        public ServicePK() {}

        public ServicePK(Long serviceId) {
            this.serviceId = serviceId;
        }

        public void setServiceId(Long serviceId) {
            this.serviceId = serviceId;
        }

        public Long getServiceId() {
            return this.serviceId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.serviceId);
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
            final ServicePK other = (ServicePK)obj;
            if (! Objects.equals(this.serviceId, other.serviceId)) {
                return false;
            }
            return true;
        }

    }
}