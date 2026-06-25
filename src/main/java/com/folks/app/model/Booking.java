package com.folks.app.model;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "fks_bookings")
@IdClass(Booking.BookingPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Booking.selectAll", query = "SELECT * FROM fks_bookings")
})
public class Booking implements Serializable, Cloneable {

    public static enum Status {
        PENDING,
        CONFIRMED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED;
    };

    @Id
    @Column(name = "booking_id", nullable = false, updatable = false, precision = 64)
    private Long bookingId;

    @Column(name = "customer_id", nullable = false, updatable = true, precision = 64)
    private BigInteger customerId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 64)
    private BigInteger professionalId;

    @Column(name = "service_id", nullable = false, updatable = true, precision = 64)
    private BigInteger serviceId;

    @Column(name = "address_id", nullable = false, updatable = true, precision = 64)
    private BigInteger addressId;

    @Column(name = "scheduled_at", nullable = false, updatable = true)
    private Timestamp scheduledAt;

    @Column(name = "status", nullable = true, updatable = true, check = @CheckConstraint(constraint = "status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')"))
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "total_amount", nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    public Booking() {}

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
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

    public void setServiceId(BigInteger serviceId) {
        this.serviceId = serviceId;
    }

    public BigInteger getServiceId() {
        return this.serviceId;
    }

    public void setAddressId(BigInteger addressId) {
        this.addressId = addressId;
    }

    public BigInteger getAddressId() {
        return this.addressId;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Timestamp getScheduledAt() {
        return this.scheduledAt;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public static class BookingPK {

        private Long bookingId;

        public BookingPK() {}

        public BookingPK(Long bookingId) {
            this.bookingId = bookingId;
        }

        public void setBookingId(Long bookingId) {
            this.bookingId = bookingId;
        }

        public Long getBookingId() {
            return this.bookingId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.bookingId);
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
            final BookingPK other = (BookingPK)obj;
            if (! Objects.equals(this.bookingId, other.bookingId)) {
                return false;
            }
            return true;
        }

    }
}