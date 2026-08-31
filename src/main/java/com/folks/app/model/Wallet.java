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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_wallets")
@IdClass(Wallet.WalletPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Wallet.selectAll", query = "SELECT * FROM fks_wallets")
})
public class Wallet implements Serializable, Cloneable {

    @Id
    @Column(name = "wallet_id", nullable = false, updatable = false, length = 36)
    private String walletId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 32)
    private Integer userId;

    @Column(name = "balance", nullable = true, updatable = true, precision = 7, scale = 2)
    private Double balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Wallet() {}

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletId() {
        return this.walletId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getBalance() {
        return this.balance;
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

    public static class WalletPK {

        private String walletId;

        public WalletPK() {}

        public WalletPK(String walletId) {
            this.walletId = walletId;
        }

        public void setWalletId(String walletId) {
            this.walletId = walletId;
        }

        public String getWalletId() {
            return this.walletId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.walletId);
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
            final WalletPK other = (WalletPK)obj;
            if (! Objects.equals(this.walletId, other.walletId)) {
                return false;
            }
            return true;
        }

    }
}