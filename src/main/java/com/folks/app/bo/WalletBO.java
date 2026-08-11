package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.WalletDAO;
import com.folks.app.model.Wallet;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class WalletBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletBO.class);
    
    private final WalletDAO walletDAO;

    public WalletBO() {
        this.walletDAO = DAOProxy.get(WalletDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. WalletDAO: {}", getClass().getSimpleName(), walletDAO);
        }
    }

    public Wallet create(AppUser usr, Wallet wallet) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        
        walletDAO.insert(wallet);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Wallet created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return wallet;
    }

    public void create(AppUser usr, List<Wallet> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        walletDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Wallet record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Wallet modify(AppUser usr, Wallet wallet) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Wallet existing = walletDAO.find(new Wallet.WalletPK(wallet.getWalletId()));
        if (existing == null) {
            throw new IllegalArgumentException("No wallet found for identifier: " + wallet.getWalletId());
        }
        // Update attributes of existing record
        existing.setUserId(wallet.getUserId());
        existing.setBalance(wallet.getBalance());

        walletDAO.update(wallet);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Wallet record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Wallet> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Wallet> rows = walletDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded wallet record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Wallet view(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Wallet wallet = walletDAO.find(new Wallet.WalletPK(id));
        if (wallet == null) {
            throw new IllegalArgumentException("No Wallet found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched wallet details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return wallet;
    }

    public Wallet remove(AppUser usr, String id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Wallet wallet = walletDAO.find(new Wallet.WalletPK(id));

        if (wallet == null) {
            throw new IllegalArgumentException("No wallet found for id: " + id);
        }
        walletDAO.delete(wallet);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Wallet. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return wallet;
    }
}
