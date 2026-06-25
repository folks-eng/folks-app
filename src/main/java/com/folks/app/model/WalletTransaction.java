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
@Table(name = "fks_wallet_transactions")
@IdClass(WalletTransaction.WalletTransactionPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "WalletTransaction.selectAll", query = "SELECT * FROM fks_wallet_transactions")
})
public class WalletTransaction implements Serializable, Cloneable {

    public static enum Type {
        CREDIT,
        DEBIT;
    };

    @Id
    @Column(name = "txn_id", nullable = false, updatable = false, precision = 64)
    private Long txnId;

    @Column(name = "wallet_id", nullable = false, updatable = true, precision = 64)
    private BigInteger walletId;

    @Column(name = "amount", nullable = false, updatable = true, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "type", nullable = true, updatable = true, check = @CheckConstraint(constraint = "type IN ('CREDIT', 'DEBIT')"))
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    public WalletTransaction() {}

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public Long getTxnId() {
        return this.txnId;
    }

    public void setWalletId(BigInteger walletId) {
        this.walletId = walletId;
    }

    public BigInteger getWalletId() {
        return this.walletId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public static class WalletTransactionPK {

        private Long txnId;

        public WalletTransactionPK() {}

        public WalletTransactionPK(Long txnId) {
            this.txnId = txnId;
        }

        public void setTxnId(Long txnId) {
            this.txnId = txnId;
        }

        public Long getTxnId() {
            return this.txnId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.txnId);
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
            final WalletTransactionPK other = (WalletTransactionPK)obj;
            if (! Objects.equals(this.txnId, other.txnId)) {
                return false;
            }
            return true;
        }

    }
}