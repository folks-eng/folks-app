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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_pricing_rules")
@IdClass(PricingRule.PricingRulePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "PricingRule.selectAll", query = "SELECT * FROM fks_pricing_rules")
})
public class PricingRule implements Serializable, Cloneable {

    @Id
    @Column(name = "rule_id", nullable = false, updatable = false, precision = 64)
    private Long ruleId;

    @Column(name = "service_id", nullable = false, updatable = true, precision = 64)
    private BigInteger serviceId;

    @Column(name = "city", nullable = false, updatable = true, length = 100)
    private String city;

    @Column(name = "multiplier", nullable = false, updatable = true, precision = 5, scale = 2)
    private BigDecimal multiplier;

    @Column(name = "start_time", nullable = false, updatable = true)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false, updatable = true)
    private Timestamp endTime;

    public PricingRule() {}

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleId() {
        return this.ruleId;
    }

    public void setServiceId(BigInteger serviceId) {
        this.serviceId = serviceId;
    }

    public BigInteger getServiceId() {
        return this.serviceId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }

    public BigDecimal getMultiplier() {
        return this.multiplier;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getStartTime() {
        return this.startTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getEndTime() {
        return this.endTime;
    }

    public static class PricingRulePK {

        private Long ruleId;

        public PricingRulePK() {}

        public PricingRulePK(Long ruleId) {
            this.ruleId = ruleId;
        }

        public void setRuleId(Long ruleId) {
            this.ruleId = ruleId;
        }

        public Long getRuleId() {
            return this.ruleId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.ruleId);
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
            final PricingRulePK other = (PricingRulePK)obj;
            if (! Objects.equals(this.ruleId, other.ruleId)) {
                return false;
            }
            return true;
        }

    }
}