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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_cities")
@IdClass(City.CityPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "City.selectAll", query = "SELECT * FROM fks_cities")
})
public class City implements Serializable, Cloneable {

    public static enum Status {
        PLANNED,
        ACTIVE,
        PAUSED,
        INACTIVE;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id", nullable = false, updatable = false, precision = 32)
    private Integer cityId;

    @Column(name = "province_id", nullable = false, updatable = true, precision = 32)
    private Integer provinceId;

    @Column(name = "city_name", nullable = false, updatable = true, length = 50)
    private String cityName;

    @Column(name = "image_key", nullable = true, updatable = true, length = 128)
    private String imageKey;

    @Column(name = "status", nullable = false, updatable = true, check = @CheckConstraint(constraint = "status IN ('PLANNED', 'ACTIVE', 'PAUSED', 'INACTIVE')"))
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "launched_at", nullable = true, updatable = true)
    private Date launchedAt;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public City() {}

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCityId() {
        return this.cityId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getProvinceId() {
        return this.provinceId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return this.cityName;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    public String getImageKey() {
        return this.imageKey;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setLaunchedAt(Date launchedAt) {
        this.launchedAt = launchedAt;
    }

    public Date getLaunchedAt() {
        return this.launchedAt;
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

    public static class CityPK {

        private Integer cityId;

        public CityPK() {}

        public CityPK(Integer cityId) {
            this.cityId = cityId;
        }

        public void setCityId(Integer cityId) {
            this.cityId = cityId;
        }

        public Integer getCityId() {
            return this.cityId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.cityId);
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
            final CityPK other = (CityPK)obj;
            if (! Objects.equals(this.cityId, other.cityId)) {
                return false;
            }
            return true;
        }

    }
}