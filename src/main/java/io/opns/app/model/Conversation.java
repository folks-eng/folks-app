package io.opns.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author schan280
 */

@Entity
@Table(name = "fks_conversations")
@IdClass(Conversation.ConversationPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Conversation.selectAll", query = "SELECT * FROM fks_conversations")
})
public class Conversation implements Serializable, Cloneable {

    @Id
    @Column(name = "conversation_id", nullable = false, updatable = false, precision = 64)
    private Long conversationId;

    @Column(name = "booking_id", nullable = false, updatable = true, precision = 64)
    private BigInteger bookingId;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    public Conversation() {}

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getConversationId() {
        return this.conversationId;
    }

    public void setBookingId(BigInteger bookingId) {
        this.bookingId = bookingId;
    }

    public BigInteger getBookingId() {
        return this.bookingId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public static class ConversationPK {

        private Long conversationId;

        public ConversationPK() {}

        public ConversationPK(Long conversationId) {
            this.conversationId = conversationId;
        }

        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }

        public Long getConversationId() {
            return this.conversationId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.conversationId);
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
            final ConversationPK other = (ConversationPK)obj;
            if (! Objects.equals(this.conversationId, other.conversationId)) {
                return false;
            }
            return true;
        }

    }
}