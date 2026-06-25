package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.CouponUsageDAO;
import com.folks.app.model.CouponUsage;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CouponUsageBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponUsageBO.class);
    
    private final CouponUsageDAO couponUsageDAO;

    public CouponUsageBO() {
        this.couponUsageDAO = DAOProxy.get(CouponUsageDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. CouponUsageDAO: {}", getClass().getSimpleName(), couponUsageDAO);
        }
    }

    public CouponUsage create(AppUser usr, CouponUsage couponUsage) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        couponUsageDAO.insert(couponUsage);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("CouponUsage created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return couponUsage;
    }

    public void create(AppUser usr, List<CouponUsage> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        couponUsageDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} CouponUsage record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public CouponUsage modify(AppUser usr, CouponUsage couponUsage) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        CouponUsage existing = couponUsageDAO.find(new CouponUsage.CouponUsagePK(couponUsage.getId()));
        if (existing == null) {
            throw new IllegalArgumentException("No couponUsage found for identifier: " + couponUsage.getId());
        }
        // Update attributes of existing record
        existing.setCouponId(couponUsage.getCouponId());
        existing.setUserId(couponUsage.getUserId());
        existing.setBookingId(couponUsage.getBookingId());
        existing.setUsedAt(couponUsage.getUsedAt());

        couponUsageDAO.update(couponUsage);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("CouponUsage record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<CouponUsage> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<CouponUsage> rows = couponUsageDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded couponUsage record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public CouponUsage view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        CouponUsage couponUsage = couponUsageDAO.find(new CouponUsage.CouponUsagePK(id));
        if (couponUsage == null) {
            throw new IllegalArgumentException("No CouponUsage found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched couponUsage details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return couponUsage;
    }

    public CouponUsage remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        CouponUsage couponUsage = couponUsageDAO.find(new CouponUsage.CouponUsagePK(id));

        if (couponUsage == null) {
            throw new IllegalArgumentException("No couponUsage found for id: " + id);
        }
        couponUsageDAO.delete(couponUsage);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted CouponUsage. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return couponUsage;
    }
}
