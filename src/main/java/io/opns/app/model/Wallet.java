package io.opns.app.model;

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
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_wallets")
@IdClass(Wallet.WalletPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Wallet.selectAll", query = "SELECT * FROM fks_wallets")
})
public class Wallet implements Serializable, Cloneable {

    @Id
    @Column(name = "wallet_id", nullable = false, updatable = false, precision = 64)
    private Long walletId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 64)
    private BigInteger userId;

    @Column(name = "balance", nullable = true, updatable = true, precision = 10, scale = 2)
    private BigDecimal balance;

    public Wallet() {}

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getWalletId() {
        return this.walletId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public BigInteger getUserId() {
        return this.userId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public static class WalletPK {

        private Long walletId;

        public WalletPK() {}

        public WalletPK(Long walletId) {
            this.walletId = walletId;
        }

        public void setWalletId(Long walletId) {
            this.walletId = walletId;
        }

        public Long getWalletId() {
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