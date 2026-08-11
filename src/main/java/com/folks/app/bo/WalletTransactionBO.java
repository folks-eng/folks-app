package com.folks.app.bo;

import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.WalletTransactionDAO;
import com.folks.app.model.WalletTransaction;
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
public class WalletTransactionBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletTransactionBO.class);
    
    private final WalletTransactionDAO walletTransactionDAO;

    public WalletTransactionBO() {
        this.walletTransactionDAO = DAOProxy.get(WalletTransactionDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. WalletTransactionDAO: {}", getClass().getSimpleName(), walletTransactionDAO);
        }
    }

    public WalletTransaction create(AppUser usr, WalletTransaction walletTransaction) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        if (walletTransaction.getCreatedAt() == null) {
            walletTransaction.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        }

        walletTransactionDAO.insert(walletTransaction);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("WalletTransaction created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return walletTransaction;
    }

    public void create(AppUser usr, List<WalletTransaction> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        for (WalletTransaction walletTransaction : records) {
            if (walletTransaction.getCreatedAt() == null) {
                walletTransaction.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            }
        }
        walletTransactionDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} WalletTransaction record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public WalletTransaction modify(AppUser usr, WalletTransaction walletTransaction) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        WalletTransaction existing = walletTransactionDAO.find(new WalletTransaction.WalletTransactionPK(walletTransaction.getTxnId()));
        if (existing == null) {
            throw new IllegalArgumentException("No walletTransaction found for identifier: " + walletTransaction.getTxnId());
        }
        // Update attributes of existing record
        existing.setWalletId(walletTransaction.getWalletId());
        existing.setAmount(walletTransaction.getAmount());
        existing.setType(walletTransaction.getType());

        walletTransactionDAO.update(walletTransaction);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("WalletTransaction record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<WalletTransaction> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<WalletTransaction> rows = walletTransactionDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded walletTransaction record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public WalletTransaction view(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        WalletTransaction walletTransaction = walletTransactionDAO.find(new WalletTransaction.WalletTransactionPK(id));
        if (walletTransaction == null) {
            throw new IllegalArgumentException("No WalletTransaction found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched walletTransaction details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return walletTransaction;
    }

    public WalletTransaction remove(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        WalletTransaction walletTransaction = walletTransactionDAO.find(new WalletTransaction.WalletTransactionPK(id));

        if (walletTransaction == null) {
            throw new IllegalArgumentException("No walletTransaction found for id: " + id);
        }
        walletTransactionDAO.delete(walletTransaction);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted WalletTransaction. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return walletTransaction;
    }
}
