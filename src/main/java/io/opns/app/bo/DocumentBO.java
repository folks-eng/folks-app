package io.opns.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import io.opns.app.auth.AppUser;
import io.opns.app.dao.DocumentDAO;
import io.opns.app.model.Document;
import io.opns.app.util.QueryParams;
import io.opns.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class DocumentBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBO.class);
    
    private final DocumentDAO documentDAO;

    public DocumentBO() {
        this.documentDAO = DAOProxy.get(DocumentDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. DocumentDAO: {}", getClass().getSimpleName(), documentDAO);
        }
    }

    public Document create(AppUser usr, Document document) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        documentDAO.insert(document);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Document created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return document;
    }

    public void create(AppUser usr, List<Document> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        documentDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Document record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Document modify(AppUser usr, Document document) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Document existing = documentDAO.find(new Document.DocumentPK(document.getDocumentId()));
        if (existing == null) {
            throw new IllegalArgumentException("No document found for identifier: " + document.getDocumentId());
        }
        // Update attributes of existing record
        existing.setUserId(document.getUserId());
        existing.setDocumentType(document.getDocumentType());
        existing.setDocumentUrl(document.getDocumentUrl());
        existing.setVerificationStatus(document.getVerificationStatus());
        existing.setUploadedAt(document.getUploadedAt());

        documentDAO.update(document);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Document record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Document> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Document> rows = documentDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded document record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Document view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Document document = documentDAO.find(new Document.DocumentPK(id));
        if (document == null) {
            throw new IllegalArgumentException("No Document found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched document details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return document;
    }

    public Document remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Document document = documentDAO.find(new Document.DocumentPK(id));

        if (document == null) {
            throw new IllegalArgumentException("No document found for id: " + id);
        }
        documentDAO.delete(document);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Document. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return document;
    }
}
