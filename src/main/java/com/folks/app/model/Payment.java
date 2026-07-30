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
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_payments")
@IdClass(Payment.PaymentPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Payment.selectAll", query = "SELECT * FROM fks_payments")
})
public class Payment implements Serializable, Cloneable {

    public static enum Paymentmethod {
        CARD,
        UPI,
        WALLET,
        COD;
    };

    public static enum Paymentstatus {
        INITIATED,
        SUCCESS,
        FAILED,
        REFUNDED;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false, updatable = false, precision = 64)
    private Long paymentId;

    @Column(name = "booking_id", nullable = false, updatable = false, length = 36)
    private String bookingId;

    @Column(name = "amount", nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", nullable = true, updatable = true, check = @CheckConstraint(constraint = "payment_method IN ('CARD', 'UPI', 'WALLET', 'COD')"))
    @Enumerated(EnumType.STRING)
    private Paymentmethod paymentMethod;

    @Column(name = "payment_status", nullable = true, updatable = true, check = @CheckConstraint(constraint = "payment_status IN ('INITIATED', 'SUCCESS', 'FAILED', 'REFUNDED')"))
    @Enumerated(EnumType.STRING)
    private Paymentstatus paymentStatus;

    @Column(name = "transaction_ref", nullable = false, updatable = true, length = 128)
    private String transactionRef;

    @Column(name = "paid_at", nullable = false, updatable = true)
    private Timestamp paidAt;

    public Payment() {}

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() {
        return this.paymentId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setPaymentMethod(Paymentmethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Paymentmethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public void setPaymentStatus(Paymentstatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Paymentstatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public String getTransactionRef() {
        return this.transactionRef;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public Timestamp getPaidAt() {
        return this.paidAt;
    }

    public static class PaymentPK {

        private Long paymentId;

        public PaymentPK() {}

        public PaymentPK(Long paymentId) {
            this.paymentId = paymentId;
        }

        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }

        public Long getPaymentId() {
            return this.paymentId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.paymentId);
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
            final PaymentPK other = (PaymentPK)obj;
            if (! Objects.equals(this.paymentId, other.paymentId)) {
                return false;
            }
            return true;
        }

    }
}