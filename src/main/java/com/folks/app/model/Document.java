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
 * @author Sudiptasish Chanda
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
    @Column(name = "document_id", nullable = false, updatable = false, precision = 32)
    private Integer documentId;
    
    @Column(name = "user_id", nullable = false, updatable = false, precision = 32)
    private Integer userId;
    
    @Column(name = "application_id", nullable = false, updatable = false, length = 36)
    private String applicationId;

    @Column(name = "document_type", nullable = false, updatable = false, length = 50)
    private String documentType;

    @Column(name = "document_number", nullable = false, updatable = true, length = 50)
    private String documentNumber;

    @Column(name = "document_url", nullable = true, updatable = true, length = 1000000000)
    private String documentUrl;

    @Column(name = "name_on_document", nullable = false, updatable = false, length = 50)
    private String nameOnDocument;

    @Column(name = "verification_status", nullable = true, updatable = true, check = @CheckConstraint(constraint = "verification_status IN ('PENDING', 'APPROVED', 'REJECTED')"))
    @Enumerated(EnumType.STRING)
    private Verificationstatus verificationStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", nullable = true, updatable = true)
    private Timestamp updatedAt;

    public Document() {}

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public Integer getDocumentId() {
        return this.documentId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getNameOnDocument() {
        return nameOnDocument;
    }

    public void setNameOnDocument(String nameOnDocument) {
        this.nameOnDocument = nameOnDocument;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return this.documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public void setCreatedAt(Timestamp ts) {
        this.createdAt = ts;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class DocumentPK {

        private Integer documentId;

        public DocumentPK() {}

        public DocumentPK(Integer documentId) {
            this.documentId = documentId;
        }

        public void setDocumentId(Integer documentId) {
            this.documentId = documentId;
        }

        public Integer getDocumentId() {
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