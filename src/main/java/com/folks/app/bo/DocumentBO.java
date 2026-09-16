package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.DocumentDAO;
import com.folks.app.dao.ProfessionalDAO;
import com.folks.app.model.Document;
import com.folks.app.model.Professional;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class DocumentBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBO.class);
    
    private final DocumentDAO documentDAO;
    private final ProfessionalDAO professionalDAO;
    
    public DocumentBO() {
        this.documentDAO = DAOProxy.get(DocumentDAO.class);
        this.professionalDAO = DAOProxy.get(ProfessionalDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Document Business Object: {}. DocumentDAO: {}. UserDAO: {}", getClass().getSimpleName(), documentDAO, userDAO);
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
        existing.setDocumentType(document.getDocumentType());
        existing.setDocumentUrl(document.getDocumentUrl());
        existing.setVerificationStatus(document.getVerificationStatus());
        existing.setCreatedAt(document.getCreatedAt());

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
        
        List<Document> rows = null;
        
        if (User.isAdmin(usr.principal().priv())) {
            SearchCriteria search = SearchCriteria.from(params);
            rows = documentDAO.queryWithProfAttr(search);
        }
        else {
            // Fetch the user.
            Professional professional = professionalDAO.findByExtId(usr.principal().sub());
            if (professional == null) {
                throw new IllegalArgumentException("No such professional found");
            }
            // We need to fetch the documents for the current professional only.
            SearchCriteria search = SearchCriteria.from(params, "professionalId", professional.getProfessionalId());
            rows = documentDAO.query(search);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} document record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Document view(AppUser usr, Integer id) {
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

    public Document remove(AppUser usr, Integer id) {
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
