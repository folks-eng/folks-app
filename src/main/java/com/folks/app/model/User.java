package com.folks.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_users")
@IdClass(User.UserPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "User.selectAll", query = "SELECT * FROM fks_users")
})
public class User implements Serializable, Cloneable {

    public static enum Role {
        CUSTOMER,
        PROFESSIONAL,
        ADMIN;
    };

    public static enum Status {
        ACTIVE,
        INACTIVE,
        BLOCKED;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false, precision = 32)
    @JsonIgnore
    private Integer userId;

    @Column(name = "external_id", nullable = false, updatable = true, length = 36)
    private String externalId;

    @Column(name = "full_name", nullable = false, updatable = true, length = 96)
    private String fullName;

    @Column(name = "email", nullable = false, updatable = true, length = 128)
    private String email;

    @Column(name = "phone1", nullable = false, updatable = true, length = 20)
    private String phone1;

    @Column(name = "phone2", nullable = true, updatable = true, length = 20)
    private String phone2;

    @Column(name = "password_hash", nullable = true, updatable = true, length = 1000000000)
    @JsonIgnore
    private String passwordHash;

    @Column(name = "role", nullable = false, updatable = true, check = @CheckConstraint(constraint = "role IN ('CUSTOMER', 'PROFESSIONAL', 'ADMIN')"))
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "status", nullable = false, updatable = true, check = @CheckConstraint(constraint = "status IN ('ACTIVE', 'INACTIVE', 'BLOCKED')"))
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public User() {}

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone1() {
        return this.phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone2() {
        return this.phone2;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return this.role;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
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

    public static class UserPK {

        private Integer userId;

        public UserPK() {}

        public UserPK(Integer userId) {
            this.userId = userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getUserId() {
            return this.userId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.userId);
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
            final UserPK other = (UserPK)obj;
            if (! Objects.equals(this.userId, other.userId)) {
                return false;
            }
            return true;
        }

    }
}