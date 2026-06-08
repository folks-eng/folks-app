package io.opns.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import io.opns.app.auth.AppUser;
import io.opns.app.dao.AuditLogDAO;
import io.opns.app.model.AuditLog;
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
public class AuditLogBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogBO.class);
    
    private final AuditLogDAO auditLogDAO;

    public AuditLogBO() {
        this.auditLogDAO = DAOProxy.get(AuditLogDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. AuditLogDAO: {}", getClass().getSimpleName(), auditLogDAO);
        }
    }

    public AuditLog create(AppUser usr, AuditLog auditLog) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        auditLogDAO.insert(auditLog);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("AuditLog created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return auditLog;
    }

    public void create(AppUser usr, List<AuditLog> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (AuditLog auditLog : records) {
            if (auditLog.getCreatedAt() == null) {
                auditLog.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        auditLogDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} AuditLog record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public AuditLog modify(AppUser usr, AuditLog auditLog) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        AuditLog existing = auditLogDAO.find(new AuditLog.AuditLogPK(auditLog.getLogId()));
        if (existing == null) {
            throw new IllegalArgumentException("No auditLog found for identifier: " + auditLog.getLogId());
        }
        // Update attributes of existing record
        existing.setUserId(auditLog.getUserId());
        existing.setAction(auditLog.getAction());
        existing.setEntityType(auditLog.getEntityType());
        existing.setEntityId(auditLog.getEntityId());

        auditLogDAO.update(auditLog);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("AuditLog record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<AuditLog> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<AuditLog> rows = auditLogDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded auditLog record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public AuditLog view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        AuditLog auditLog = auditLogDAO.find(new AuditLog.AuditLogPK(id));
        if (auditLog == null) {
            throw new IllegalArgumentException("No AuditLog found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched auditLog details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return auditLog;
    }

    public AuditLog remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        AuditLog auditLog = auditLogDAO.find(new AuditLog.AuditLogPK(id));

        if (auditLog == null) {
            throw new IllegalArgumentException("No auditLog found for id: " + id);
        }
        auditLogDAO.delete(auditLog);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted AuditLog. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return auditLog;
    }
}
