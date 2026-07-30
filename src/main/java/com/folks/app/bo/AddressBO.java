package com.folks.app.bo;

import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.AddressDAO;
import com.folks.app.dao.UserDAO;
import com.folks.app.model.Address;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import java.util.List;
import org.javalabs.jpa.JdbcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AddressBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBO.class);
    
    private final AddressDAO addressDAO;
    private final UserDAO userDAO;

    public AddressBO() {
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        this.userDAO = DAOProxy.get(UserDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. AddressDAO: {}. UserDAO: {}", getClass().getSimpleName(), addressDAO, userDAO);
        }
    }

    public Address create(AppUser usr, Address address) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        try {
            // Get the user id.
            String externalId = usr.principal().id();
            User user = userDAO.select(externalId);

            address.setUserId(user.getUserId());

            addressDAO.insert(address);
            timer.stop();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Address created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
            }
            return address;
        }
        catch (JdbcException e) {
            LOGGER.error("Error creating address data for user " + usr.principal().id(), e);
            throw e;
        }
    }

    public void create(AppUser usr, List<Address> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        
        addressDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Address record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Address modify(AppUser usr, Address address) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Address existing = addressDAO.find(new Address.AddressPK(address.getAddressId()));
        if (existing == null) {
            throw new IllegalArgumentException("No address found for identifier: " + address.getAddressId());
        }
        // Update attributes of existing record
        existing.setUserId(address.getUserId());
        existing.setAddressLine1(address.getAddressLine1());
        existing.setAddressLine2(address.getAddressLine2());
        existing.setCity(address.getCity());
        existing.setState(address.getState());
        existing.setPincode(address.getPincode());
        existing.setLatitude(address.getLatitude());
        existing.setLongitude(address.getLongitude());
        existing.setIsDefault(address.getIsDefault());

        addressDAO.update(address);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Address record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
    }

    public List<Address> viewAll(AppUser usr, QueryParams params) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        SearchCriteria search = SearchCriteria.from(params);
        List<Address> rows = addressDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded address record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Address view(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        Address address = addressDAO.find(new Address.AddressPK(id));
        if (address == null) {
            throw new IllegalArgumentException("No Address found for id: " + id);
        }
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched address details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return address;
    }

    public Address remove(AppUser usr, Long id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // First fetch the entry, to see if this already exists.
        Address address = addressDAO.find(new Address.AddressPK(id));

        if (address == null) {
            throw new IllegalArgumentException("No address found for id: " + id);
        }
        addressDAO.delete(address);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Address. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return address;
    }
}
