package com.folks.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_addresses")
@IdClass(Address.AddressPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Address.selectAll", query = "SELECT * FROM fks_addresses")
})
public class Address implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false, updatable = false, precision = 64)
    private Long addressId;

    @Column(name = "user_id", nullable = false, updatable = false, precision = 64)
    private BigInteger userId;

    @Column(name = "address_line1", nullable = false, updatable = true, length = 255)
    private String addressLine1;

    @Column(name = "address_line2", nullable = false, updatable = true, length = 255)
    private String addressLine2;

    @Column(name = "city", nullable = false, updatable = true, length = 64)
    private String city;

    @Column(name = "state", nullable = false, updatable = true, length = 64)
    private String state;

    @Column(name = "pincode", nullable = false, updatable = true, length = 20)
    private String pincode;

    @Column(name = "latitude", nullable = true, updatable = true, precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true, updatable = true, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "is_default", nullable = false, updatable = true, precision = 16)
    private Short isDefault;

    public Address() {}

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Long getAddressId() {
        return this.addressId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getUserId() {
        return this.userId;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPincode() {
        return this.pincode;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public void setIsDefault(Short isDefault) {
        this.isDefault = isDefault;
    }

    public Short getIsDefault() {
        return this.isDefault;
    }

    public static class AddressPK {

        private Long addressId;

        public AddressPK() {}

        public AddressPK(Long addressId) {
            this.addressId = addressId;
        }

        public void setAddressId(Long addressId) {
            this.addressId = addressId;
        }

        public Long getAddressId() {
            return this.addressId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.addressId);
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
            final AddressPK other = (AddressPK)obj;
            if (! Objects.equals(this.addressId, other.addressId)) {
                return false;
            }
            return true;
        }

    }
}