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
@Table(name = "fks_reviews")
@IdClass(Review.ReviewPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Review.selectAll", query = "SELECT * FROM fks_reviews")
})
public class Review implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false, updatable = false, precision = 32)
    private Integer reviewId;

    @Column(name = "booking_id", nullable = false, updatable = true, length = 36)
    private String bookingId;

    @Column(name = "customer_id", nullable = false, updatable = true, precision = 32)
    private Integer customerId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 32)
    private Integer professionalId;

    @Column(name = "rating", nullable = true, updatable = true, precision = 16)
    private Short rating;

    @Column(name = "comment", nullable = true, updatable = true, length = 1000000000)
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Review() {}

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public Integer getReviewId() {
        return this.reviewId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
    }

    public Integer getProfessionalId() {
        return this.professionalId;
    }

    public void setRating(Short rating) {
        this.rating = rating;
    }

    public Short getRating() {
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class ReviewPK {

        private Integer reviewId;

        public ReviewPK() {}

        public ReviewPK(Integer reviewId) {
            this.reviewId = reviewId;
        }

        public void setReviewId(Integer reviewId) {
            this.reviewId = reviewId;
        }

        public Integer getReviewId() {
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