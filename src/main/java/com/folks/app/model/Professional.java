package com.folks.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_professionals")
@IdClass(Professional.ProfessionalPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Professional.selectAll", query = "SELECT * FROM fks_professionals")
})
public class Professional implements Serializable, Cloneable {

    @Id
    @Column(name = "professional_id", nullable = false, updatable = false, precision = 64)
    private Long professionalId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 64)
    private BigInteger userId;

    @Column(name = "bio", nullable = true, updatable = true, length = 1000000000)
    private String bio;

    @Column(name = "experience_years", nullable = false, updatable = true, precision = 32)
    private Integer experienceYears;

    @Column(name = "rating_avg", nullable = true, updatable = true, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "is_verified", nullable = false, updatable = true, precision = 16)
    private Short isVerified;

    public Professional() {}

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public Long getProfessionalId() {
        return this.professionalId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getUserId() {
        return this.userId;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return this.bio;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Integer getExperienceYears() {
        return this.experienceYears;
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

    public static class ProfessionalPK {

        private Long professionalId;

        public ProfessionalPK() {}

        public ProfessionalPK(Long professionalId) {
            this.professionalId = professionalId;
        }

        public void setProfessionalId(Long professionalId) {
            this.professionalId = professionalId;
        }

        public Long getProfessionalId() {
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