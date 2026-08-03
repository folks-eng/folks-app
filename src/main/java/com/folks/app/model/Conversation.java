package com.folks.app.model;

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
import java.sql.Timestamp;
import java.util.Objects;


/**
 * This class is auto generated with jpa-lite framework.
 *
 * @author Sudiptasish Chanda
 */

@Entity
@Table(name = "fks_conversations")
@IdClass(Conversation.ConversationPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Conversation.selectAll", query = "SELECT * FROM fks_conversations")
})
public class Conversation implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id", nullable = false, updatable = false, precision = 32)
    private Integer conversationId;

    @Column(name = "booking_id", nullable = false, updatable = true, length = 36)
    private String bookingId;

    @Column(name = "created_at", nullable = false, updatable = true)
    private Timestamp createdAt;

    public Conversation() {}

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getConversationId() {
        return this.conversationId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingId() {
        return this.bookingId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public static class ConversationPK {

        private Integer conversationId;

        public ConversationPK() {}

        public ConversationPK(Integer conversationId) {
            this.conversationId = conversationId;
        }

        public void setConversationId(Integer conversationId) {
            this.conversationId = conversationId;
        }

        public Integer getConversationId() {
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