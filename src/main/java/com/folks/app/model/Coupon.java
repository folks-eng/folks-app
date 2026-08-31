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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_coupons")
@IdClass(Coupon.CouponPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Coupon.selectAll", query = "SELECT * FROM fks_coupons")
})
public class Coupon implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false, updatable = false, precision = 32)
    private Integer couponId;

    @Column(name = "code", nullable = false, updatable = true, length = 50)
    private String code;

    @Column(name = "discount_type", nullable = false, updatable = true, length = 16)
    private String discountType;

    @Column(name = "discount_value", nullable = true, updatable = true, precision = 7, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount", nullable = true, updatable = true, precision = 7, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "expiry_date", nullable = false, updatable = true)
    private Date expiryDate;

    @Column(name = "usage_limit", nullable = true, updatable = true, precision = 16)
    private Short usageLimit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Coupon() {}

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    public Integer getCouponId() {
        return this.couponId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountType() {
        return this.discountType;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getDiscountValue() {
        return this.discountValue;
    }

    public void setMaxDiscount(BigDecimal maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public BigDecimal getMaxDiscount() {
        return this.maxDiscount;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() {
        return this.expiryDate;
    }

    public void setUsageLimit(Short usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Short getUsageLimit() {
        return this.usageLimit;
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

    public static class CouponPK {

        private Integer couponId;

        public CouponPK() {}

        public CouponPK(Integer couponId) {
            this.couponId = couponId;
        }

        public void setCouponId(Integer couponId) {
            this.couponId = couponId;
        }

        public Integer getCouponId() {
            return this.couponId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.couponId);
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
            final CouponPK other = (CouponPK)obj;
            if (! Objects.equals(this.couponId, other.couponId)) {
                return false;
            }
            return true;
        }

    }
}