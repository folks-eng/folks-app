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
@Table(name = "fks_messages")
@IdClass(Message.MessagePK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Message.selectAll", query = "SELECT * FROM fks_messages")
})
public class Message implements Serializable, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false, updatable = false, precision = 32)
    private Integer messageId;

    @Column(name = "conversation_id", nullable = false, updatable = true, precision = 32)
    private Integer conversationId;

    @Column(name = "sender_id", nullable = false, updatable = true, precision = 32)
    private Integer senderId;

    @Column(name = "message_text", nullable = true, updatable = true, length = 1000000000)
    private String messageText;

    @Column(name = "sent_at", nullable = false, updatable = true)
    private Timestamp sentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Message() {}

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getMessageId() {
        return this.messageId;
    }

    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    public Integer getConversationId() {
        return this.conversationId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getSenderId() {
        return this.senderId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return this.messageText;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    public Timestamp getSentAt() {
        return this.sentAt;
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

    public static class MessagePK {

        private Integer messageId;

        public MessagePK() {}

        public MessagePK(Integer messageId) {
            this.messageId = messageId;
        }

        public void setMessageId(Integer messageId) {
            this.messageId = messageId;
        }

        public Integer getMessageId() {
            return this.messageId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.messageId);
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
            final MessagePK other = (MessagePK)obj;
            if (! Objects.equals(this.messageId, other.messageId)) {
                return false;
            }
            return true;
        }

    }
}