package com.folks.app.model;

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
 * @author schan280
 */

@Entity
@Table(name = "fks_documents")
@IdClass(Document.DocumentPK.class)
@NamedNativeQueries({
    @NamedNativeQuery(name = "Document.selectAll", query = "SELECT * FROM fks_documents")
})
public class Document implements Serializable, Cloneable {

    public static enum Verificationstatus {
        PENDING,
        APPROVED,
        REJECTED;
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id", nullable = false, updatable = false, precision = 64)
    private Long documentId;

    @Column(name = "user_id", nullable = false, updatable = true, precision = 64)
    private Long userId;

    @Column(name = "document_type", nullable = false, updatable = true, length = 50)
    private String documentType;

    @Column(name = "document_url", nullable = true, updatable = true, length = 1000000000)
    private String documentUrl;

    @Column(name = "verification_status", nullable = true, updatable = true, check = @CheckConstraint(constraint = "verification_status IN ('PENDING', 'APPROVED', 'REJECTED')"))
    @Enumerated(EnumType.STRING)
    private Verificationstatus verificationStatus;

    @Column(name = "uploaded_at", nullable = true, updatable = true)
    private Timestamp uploadedAt;

    public Document() {}

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getDocumentId() {
        return this.documentId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentUrl() {
        return this.documentUrl;
    }

    public void setVerificationStatus(Verificationstatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Verificationstatus getVerificationStatus() {
        return this.verificationStatus;
    }

    public void setUploadedAt(Timestamp uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Timestamp getUploadedAt() {
        return this.uploadedAt;
    }

    public static class DocumentPK {

        private Long documentId;

        public DocumentPK() {}

        public DocumentPK(Long documentId) {
            this.documentId = documentId;
        }

        public void setDocumentId(Long documentId) {
            this.documentId = documentId;
        }

        public Long getDocumentId() {
            return this.documentId;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.documentId);
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
            final DocumentPK other = (DocumentPK)obj;
            if (! Objects.equals(this.documentId, other.documentId)) {
                return false;
            }
            return true;
        }

    }
}