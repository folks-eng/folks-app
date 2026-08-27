package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_services")
@IdClass(Service.ServicePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Service.selectAll", query = "SELECT * FROM fks_services"),
    @NamedNativeQuery(name = "Service.selectByIds"
            , query = "SELECT *"
                    + "  FROM fks_services "
                    + " WHERE service_id IN (:ids)"),
        @NamedNativeQuery(name = "Service.selectByCatIds"
                , query = "SELECT *"
                + "  FROM fks_services "
                + " WHERE category_id IN (:ids)")
})
public class Service implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id", nullable = false, updatable = false, precision = 32)
    private Integer serviceId;

    @Column(name = "category_id", nullable = false, updatable = true, precision = 32)
    private Integer categoryId;

    @Column(name = "name", nullable = false, updatable = true, length = 128)
    private String name;

    @Column(name = "description", nullable = true, updatable = true, length = 1000000000)
    private String description;

    @Column(name = "base_price", nullable = false, updatable = true, precision = 7, scale = 2)
    private Double basePrice;

    @Column(name = "currency", nullable = false, updatable = true, length = 3)
    private String currency;

    @Column(name = "duration_minutes", nullable = true, updatable = true, precision = 16)
    private Short durationMinutes;

    @Column(name = "image", nullable = false, updatable = true, length = 128)
    private String image;

    @Column(name = "rating_avg", nullable = true, updatable = true, precision = 3, scale = 2)
    private Double ratingAvg;

    @Column(name = "reviews", nullable = false, updatable = true, precision = 32)
    private Integer reviews;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey()
            , name = "category_id"
            , table = "fks_categories"
            , referencedColumnName = "category_id")
    private Category category;

    public Service() {}

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceId() {
        return this.serviceId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId() {
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

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getBasePrice() {
        return this.basePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDurationMinutes(Short durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Short getDurationMinutes() {
        return this.durationMinutes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public Integer getReviews() {
        return reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static class ServicePK {

        private Integer serviceId;

        public ServicePK() {}

        public ServicePK(Integer serviceId) {
            this.serviceId = serviceId;
        }

        public void setServiceId(Integer serviceId) {
            this.serviceId = serviceId;
        }

        public Integer getServiceId() {
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