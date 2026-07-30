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
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_coupons")
@IdClass(Coupon.CouponPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Coupon.selectAll", query = "SELECT * FROM fks_coupons")
})
public class Coupon implements Serializable, Cloneable {

    public static enum Discounttype {
        PERCENT,
        FLAT;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false, updatable = false, precision = 64)
    private Long couponId;

    @Column(name = "code", nullable = false, updatable = true, length = 50)
    private String code;

    @Column(name = "discount_type", nullable = true, updatable = true, check = @CheckConstraint(constraint = "discount_type IN ('PERCENT', 'FLAT')"))
    @Enumerated(EnumType.STRING)
    private Discounttype discountType;

    @Column(name = "discount_value", nullable = true, updatable = true, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_discount", nullable = true, updatable = true, precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    @Column(name = "expiry_date", nullable = false, updatable = true)
    private Date expiryDate;

    @Column(name = "usage_limit", nullable = true, updatable = true, precision = 32)
    private Integer usageLimit;

    public Coupon() {}

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getCouponId() {
        return this.couponId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setDiscountType(Discounttype discountType) {
        this.discountType = discountType;
    }

    public Discounttype getDiscountType() {
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

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageLimit() {
        return this.usageLimit;
    }

    public static class CouponPK {

        private Long couponId;

        public CouponPK() {}

        public CouponPK(Long couponId) {
            this.couponId = couponId;
        }

        public void setCouponId(Long couponId) {
            this.couponId = couponId;
        }

        public Long getCouponId() {
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