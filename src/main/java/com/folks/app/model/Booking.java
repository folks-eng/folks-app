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
import jakarta.persistence.Transient;
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
    @Column(name = "booking_id", nullable = false, updatable = false, length = 36)
    private String bookingId;

    @Column(name = "customer_id", nullable = false, updatable = true, precision = 32)
    private Integer customerId;

    @Column(name = "professional_id", nullable = false, updatable = true, precision = 32)
    private Integer professionalId;

    @Column(name = "service_id", nullable = false, updatable = true, precision = 32)
    private Integer serviceId;

    @Column(name = "address_id", nullable = false, updatable = true, precision = 32)
    private Integer addressId;

    @Column(name = "scheduled_at", nullable = false, updatable = true)
    private Date scheduledAt;

    @Column(name = "time_slot", nullable = true, updatable = true, length = 24)
    private String timeSlot;

    @Column(name = "status", nullable = false, updatable = true, length = 16, check = @CheckConstraint(constraint = "status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')"))
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "status_msg", nullable = true, updatable = true, length = 255)
    private String statusMsg;

    @Column(name = "total_amount", nullable = false, updatable = true, precision = 20, scale = 6)
    private BigDecimal totalAmount;

    @Column(name = "payment_method", nullable = true, updatable = true, check = @CheckConstraint(constraint = "payment_method IN ('CARD', 'UPI', 'WALLET', 'COD')"))
    @Enumerated(EnumType.STRING)
    private Payment.Paymentmethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;
    
    @Transient
    private String address;

    @Transient
    private String serviceName;
    
    public Booking() {}

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

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getServiceId() {
        return this.serviceId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getAddressId() {
        return this.addressId;
    }

    public void setScheduledAt(Date scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Date getScheduledAt() {
        return this.scheduledAt;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    public Payment.Paymentmethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Payment.Paymentmethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public static class BookingPK {

        private String bookingId;

        public BookingPK() {}

        public BookingPK(String bookingId) {
            this.bookingId = bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getBookingId() {
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