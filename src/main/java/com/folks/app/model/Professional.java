package com.folks.app.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_professionals")
@IdClass(Professional.ProfessionalPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Professional.selectAll", query = "SELECT * FROM fks_professionals")
})
public class Professional implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "professional_id", nullable = false, updatable = false, precision = 32)
    private Integer professionalId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 32)
    private Integer userId;

    @Column(name = "bio", nullable = true, updatable = true, length = 1000000000)
    private String bio;

    @Column(name = "experience_years", nullable = false, updatable = true, precision = 16)
    private Short experienceYears;

    @Column(name = "serving_cities", nullable = true, updatable = true, length = 256)
    private String servingCities;

    @Column(name = "rating_avg", nullable = true, updatable = true, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "is_verified", nullable = false, updatable = true, precision = 16)
    private Short isVerified;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    @Transient
    private User user;
    
    @Transient
    private List<ProfessionalService> profServices;

    public Professional() {}

    public void setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
    }

    public Integer getProfessionalId() {
        return this.professionalId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return this.bio;
    }

    public void setExperienceYears(Short experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Short getExperienceYears() {
        return this.experienceYears;
    }

    public String getServingCities() {
        return servingCities;
    }

    public void setServingCities(String servingCities) {
        this.servingCities = servingCities;
    }

    public void setRatingAvg(BigDecimal ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public BigDecimal getRatingAvg() {
        return this.ratingAvg;
    }

    public void setIsVerified(Short isVerified) {
        this.isVerified = isVerified;
    }

    public Short getIsVerified() {
        return this.isVerified;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ProfessionalService> getProfServices() {
        return profServices;
    }

    public void setProfServices(List<ProfessionalService> profServices) {
        this.profServices = profServices;
    }

    public static class ProfessionalPK {

        private Integer professionalId;

        public ProfessionalPK() {}

        public ProfessionalPK(Integer professionalId) {
            this.professionalId = professionalId;
        }

        public void setProfessionalId(Integer professionalId) {
            this.professionalId = professionalId;
        }

        public Integer getProfessionalId() {
            return this.professionalId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.professionalId);
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
            final ProfessionalPK other = (ProfessionalPK)obj;
            if (! Objects.equals(this.professionalId, other.professionalId)) {
                return false;
            }
            return true;
        }

    }
}