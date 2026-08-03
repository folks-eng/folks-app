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
@Table(name = "fks_coupon_usage")
@IdClass(CouponUsage.CouponUsagePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "CouponUsage.selectAll", query = "SELECT * FROM fks_coupon_usage")
})
public class CouponUsage implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id", nullable = false, updatable = false, precision = 32)
    private Integer usageId;

    @Column(name = "coupon_id", nullable = false, updatable = true, precision = 32)
    private Integer couponId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 32)
    private Integer userId;

    @Column(name = "booking_id", nullable = false, updatable = true, length = 36)
    private String bookingId;

    @Column(name = "used_at", nullable = false, updatable = true)
    private Timestamp usedAt;

    public CouponUsage() {}

    public void setUsageId(Integer usageId) {
        this.usageId = usageId;
    }

    public Integer getUsageId() {
        return this.usageId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getCouponId() {
        return this.couponId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
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

        private Integer usageId;

        public CouponUsagePK() {}

        public CouponUsagePK(Integer usageId) {
            this.usageId = usageId;
        }

        public void setUsageId(Integer usageId) {
            this.usageId = usageId;
        }

        public Integer getUsageId() {
            return this.usageId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.usageId);
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
            if (! Objects.equals(this.usageId, other.usageId)) {
                return false;
            }
            return true;
        }

    }
}