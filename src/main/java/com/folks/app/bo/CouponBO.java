package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.CouponDAO;
import com.folks.app.model.Coupon;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class CouponBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CouponBO.class);
    
    private final CouponDAO couponDAO;

    public CouponBO() {
        this.couponDAO = DAOProxy.get(CouponDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. CouponDAO: {}", getClass().getSimpleName(), couponDAO);
        }
    }

    public Coupon create(AppUser usr, Coupon coupon) throws IllegalAccessException {
        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        couponDAO.insert(coupon);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Coupon created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return coupon;
    }

    public void create(AppUser usr, List<Coupon> records) throws IllegalAccessException {
        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        couponDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Coupon record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Coupon modify(AppUser usr, Coupon coupon) throws IllegalAccessException {
        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Coupon existing = couponDAO.find(new Coupon.CouponPK(coupon.getCouponId()));
        if (existing == null) {
            throw new IllegalArgumentException("No coupon found for identifier: " + coupon.getCouponId());
        }
        // Update attributes of existing record
        existing.setCode(coupon.getCode());
        existing.setDiscountType(coupon.getDiscountType());
        existing.setDiscountValue(coupon.getDiscountValue());
        existing.setMaxDiscount(coupon.getMaxDiscount());
        existing.setExpiryDate(coupon.getExpiryDate());
        existing.setUsageLimit(coupon.getUsageLimit());

        couponDAO.update(coupon);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Coupon record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Coupon> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Coupon> rows = couponDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded coupon record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Coupon view(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Coupon coupon = couponDAO.find(new Coupon.CouponPK(id));
        if (coupon == null) {
            throw new IllegalArgumentException("No Coupon found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched coupon details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return coupon;
    }

    public Coupon remove(AppUser usr, Integer id) throws IllegalAccessException {
        // Only admin has the privilege to create user.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Coupon coupon = couponDAO.find(new Coupon.CouponPK(id));

        if (coupon == null) {
            throw new IllegalArgumentException("No coupon found for id: " + id);
        }
        couponDAO.delete(coupon);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Coupon. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return coupon;
    }
}
