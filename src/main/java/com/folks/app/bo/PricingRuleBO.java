package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.PricingRuleDAO;
import com.folks.app.model.PricingRule;
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
public class PricingRuleBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PricingRuleBO.class);
    
    private final PricingRuleDAO pricingRuleDAO;

    public PricingRuleBO() {
        this.pricingRuleDAO = DAOProxy.get(PricingRuleDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. PricingRuleDAO: {}", getClass().getSimpleName(), pricingRuleDAO);
        }
    }

    public PricingRule create(AppUser usr, PricingRule pricingRule) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        pricingRuleDAO.insert(pricingRule);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("PricingRule created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return pricingRule;
    }

    public void create(AppUser usr, List<PricingRule> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        pricingRuleDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} PricingRule record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public PricingRule modify(AppUser usr, PricingRule pricingRule) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        PricingRule existing = pricingRuleDAO.find(new PricingRule.PricingRulePK(pricingRule.getRuleId()));
        if (existing == null) {
            throw new IllegalArgumentException("No pricingRule found for identifier: " + pricingRule.getRuleId());
        }
        // Update attributes of existing record
        existing.setServiceId(pricingRule.getServiceId());
        existing.setCity(pricingRule.getCity());
        existing.setMultiplier(pricingRule.getMultiplier());
        existing.setStartTime(pricingRule.getStartTime());
        existing.setEndTime(pricingRule.getEndTime());

        pricingRuleDAO.update(pricingRule);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("PricingRule record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<PricingRule> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<PricingRule> rows = pricingRuleDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded pricingRule record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public PricingRule view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        PricingRule pricingRule = pricingRuleDAO.find(new PricingRule.PricingRulePK(id));
        if (pricingRule == null) {
            throw new IllegalArgumentException("No PricingRule found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched pricingRule details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return pricingRule;
    }

    public PricingRule remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        PricingRule pricingRule = pricingRuleDAO.find(new PricingRule.PricingRulePK(id));

        if (pricingRule == null) {
            throw new IllegalArgumentException("No pricingRule found for id: " + id);
        }
        pricingRuleDAO.delete(pricingRule);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted PricingRule. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return pricingRule;
    }
}
