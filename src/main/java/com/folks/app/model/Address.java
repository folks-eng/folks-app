package com.folks.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_addresses")
@IdClass(Address.AddressPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Address.selectAll"
            , query = "SELECT b.*"
                    + "  FROM fks_users a"
                    + " INNER JOIN fks_addresses b ON (a.user_id = b.user_id)"
                    + " WHERE a.external_id = ?")
})
public class Address implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false, updatable = false, precision = 32)
    private Integer addressId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 32)
    @JsonIgnore
    private Integer userId;

    @Column(name = "address_line1", nullable = false, updatable = true, length = 128)
    private String addressLine1;

    @Column(name = "address_line2", nullable = true, updatable = true, length = 128)
    private String addressLine2;

    @Column(name = "city", nullable = false, updatable = true, length = 64)
    private String city;

    @Column(name = "state", nullable = false, updatable = true, length = 64)
    private String state;

    @Column(name = "pincode", nullable = false, updatable = true, precision = 16)
    private Integer pincode;

    @Column(name = "latitude", nullable = true, updatable = true, precision = 20, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = true, updatable = true, precision = 20, scale = 6)
    private BigDecimal longitude;

    @Column(name = "is_default", nullable = false, updatable = true, precision = 16)
    private Short isDefault;

    public Address() {}

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getAddressId() {
        return this.addressId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
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

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public Integer getPincode() {
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

        private Integer addressId;

        public AddressPK() {}

        public AddressPK(Integer addressId) {
            this.addressId = addressId;
        }

        public void setAddressId(Integer addressId) {
            this.addressId = addressId;
        }

        public Integer getAddressId() {
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