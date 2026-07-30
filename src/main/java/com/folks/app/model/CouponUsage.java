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
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_coupon_usage")
@IdClass(CouponUsage.CouponUsagePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "CouponUsage.selectAll", query = "SELECT * FROM fks_coupon_usage")
})
public class CouponUsage implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id", nullable = false, updatable = false, precision = 64)
    private Long usageId;

    @Column(name = "coupon_id", nullable = false, updatable = true, precision = 64)
    private Long couponId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 64)
    private Long userId;

    @Column(name = "booking_id", nullable = false, updatable = true, length = 36)
    private String bookingId;

    @Column(name = "used_at", nullable = false, updatable = true)
    private Timestamp usedAt;

    public CouponUsage() {}

    public Long getUsageId() {
        return usageId;
    }

    public void setUsageId(Long usageId) {
        this.usageId = usageId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getCouponId() {
        return this.couponId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setUsedAt(Timestamp usedAt) {
        this.usedAt = usedAt;
    }

    public Timestamp getUsedAt() {
        return this.usedAt;
    }

    public static class CouponUsagePK {

        private Long id;

        public CouponUsagePK() {}

        public CouponUsagePK(Long id) {
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
            final CouponUsagePK other = (CouponUsagePK)obj;
            if (! Objects.equals(this.id, other.id)) {
                return false;
            }
            return true;
        }

    }
}