package io.opns.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import io.opns.app.auth.AppUser;
import io.opns.app.dao.PanInfoDAO;
import io.opns.app.model.PanInfo;
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
public class PanInfoBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PanInfoBO.class);
    
    private final PanInfoDAO panInfoDAO;

    public PanInfoBO() {
        this.panInfoDAO = DAOProxy.get(PanInfoDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. PanInfoDAO: {}", getClass().getSimpleName(), panInfoDAO);
        }
    }

    public PanInfo create(AppUser usr, PanInfo panInfo) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (panInfo.getCreatedDate() == null) {
            panInfo.setCreatedDate(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        panInfoDAO.insert(panInfo);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("PanInfo created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return panInfo;
    }

    public void create(AppUser usr, List<PanInfo> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (PanInfo panInfo : records) {
            if (panInfo.getCreatedDate() == null) {
                panInfo.setCreatedDate(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        panInfoDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} PanInfo record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public PanInfo modify(AppUser usr, PanInfo panInfo) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        PanInfo existing = panInfoDAO.find(new PanInfo.PanInfoPK(panInfo.getPanHash()));
        if (existing == null) {
            throw new IllegalArgumentException("No panInfo found for identifier: " + panInfo.getPanHash());
        }
        // Update attributes of existing record
        existing.setPanRefId(panInfo.getPanRefId());
        existing.setStatus(panInfo.getStatus());
        existing.setAccessCount(panInfo.getAccessCount());

        panInfoDAO.update(panInfo);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("PanInfo record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<PanInfo> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<PanInfo> rows = panInfoDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded panInfo record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public PanInfo view(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        PanInfo panInfo = panInfoDAO.find(new PanInfo.PanInfoPK(id));
        if (panInfo == null) {
            throw new IllegalArgumentException("No PanInfo found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched panInfo details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return panInfo;
    }

    public PanInfo remove(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        PanInfo panInfo = panInfoDAO.find(new PanInfo.PanInfoPK(id));

        if (panInfo == null) {
            throw new IllegalArgumentException("No panInfo found for id: " + id);
        }
        panInfoDAO.delete(panInfo);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted PanInfo. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return panInfo;
    }
}
