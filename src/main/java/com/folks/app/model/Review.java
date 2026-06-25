package com.folks.app.model;

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
@Table(name = "fks_reviews")
@IdClass(Review.ReviewPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Review.selectAll", query = "SELECT * FROM fks_reviews")
})
public class Review implements Serializable, Cloneable {

    @Id
    @Column(name = "review_id", nullable = false, updatable = false, precision = 64)
    private Long reviewId;

    @Column(name = "booking_id", nullable = false, updatable = true, precision = 64)
    private BigInteger bookingId;

    @Column(name = "customer_id", nullable = false, updatable = true, precision = 64)
    private BigInteger customerId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 64)
    private BigInteger professionalId;

    @Column(name = "rating", nullable = true, updatable = true, precision = 32)
    private Integer rating;

    @Column(name = "comment", nullable = true, updatable = true, length = 1000000000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    public Review() {}

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getReviewId() {
        return this.reviewId;
    }

    public void setBookingId(BigInteger bookingId) {
        this.bookingId = bookingId;
    }

    public BigInteger getBookingId() {
        return this.bookingId;
    }

    public void setCustomerId(BigInteger customerId) {
        this.customerId = customerId;
    }

    public BigInteger getCustomerId() {
        return this.customerId;
    }

    public void setProfessionalId(BigInteger professionalId) {
        this.professionalId = professionalId;
    }

    public BigInteger getProfessionalId() {
        return this.professionalId;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return this.rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return this.comment;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public static class ReviewPK {

        private Long reviewId;

        public ReviewPK() {}

        public ReviewPK(Long reviewId) {
            this.reviewId = reviewId;
        }

        public void setReviewId(Long reviewId) {
            this.reviewId = reviewId;
        }

        public Long getReviewId() {
            return this.reviewId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.reviewId);
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
            final ReviewPK other = (ReviewPK)obj;
            if (! Objects.equals(this.reviewId, other.reviewId)) {
                return false;
            }
            return true;
        }

    }
}