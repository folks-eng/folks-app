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
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_neighbourhoods")
@IdClass(Neighbourhood.NeighbourhoodPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Neighbourhood.selectAll", query = "SELECT * FROM fks_neighbourhoods")
})
public class Neighbourhood implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "neighbourhood_id", nullable = false, updatable = false, precision = 32)
    private Integer neighbourhoodId;

    @Column(name = "city_id", nullable = false, updatable = true, precision = 32)
    private Integer cityId;

    @Column(name = "locality", nullable = false, updatable = true, length = 80)
    private String locality;

    @Column(name = "pincode", nullable = false, updatable = true, precision = 32)
    private Integer pincode;

    @Column(name = "latitude", nullable = true, updatable = true, precision = 20, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true, updatable = true, precision = 20, scale = 6)
    private BigDecimal longitude;

    @Column(name = "is_serviceable", nullable = false, updatable = true, precision = 16)
    private Short isServiceable;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;
    
    @Transient
    private String province;
    
    @Transient
    private String city;

    public Neighbourhood() {}

    public void setNeighbourhoodId(Integer neighbourhoodId) {
        this.neighbourhoodId = neighbourhoodId;
    }

    public Integer getNeighbourhoodId() {
        return this.neighbourhoodId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCityId() {
        return this.cityId;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public Integer getPincode() {
        return this.pincode;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setIsServiceable(Short isServiceable) {
        this.isServiceable = isServiceable;
    }

    public Short getIsServiceable() {
        return this.isServiceable;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static class NeighbourhoodPK {

        private Integer neighbourhoodId;

        public NeighbourhoodPK() {}

        public NeighbourhoodPK(Integer neighbourhoodId) {
            this.neighbourhoodId = neighbourhoodId;
        }

        public void setNeighbourhoodId(Integer neighbourhoodId) {
            this.neighbourhoodId = neighbourhoodId;
        }

        public Integer getNeighbourhoodId() {
            return this.neighbourhoodId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.neighbourhoodId);
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
            final NeighbourhoodPK other = (NeighbourhoodPK)obj;
            if (! Objects.equals(this.neighbourhoodId, other.neighbourhoodId)) {
                return false;
            }
            return true;
        }

    }
}