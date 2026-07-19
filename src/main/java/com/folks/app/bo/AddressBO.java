package com.folks.app.bo;

import com.folks.app.model.User;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.folks.app.auth.AppUser;
import com.folks.app.dao.AddressDAO;
import com.folks.app.model.Address;
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
public class AddressBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBO.class);
    
    private final AddressDAO addressDAO;

    public AddressBO() {
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. AddressDAO: {}", getClass().getSimpleName(), addressDAO);
        }
    }

    public Address create(AppUser usr, Address address) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        validate(address);

        addressDAO.insert(address);
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Address created successfully with id {}. Elapsed time(ms): {}", address.getAddressId(),
                    timer.elapsedTimeMillis());
        }
        return address;
    }

    private void validate(Address addr) {
        String line1 = addr.getAddressLine1();
        if (line1 == null || line1.trim().isEmpty()) {
            throw new IllegalArgumentException("Address line1 is required.");
        }
        String city = addr.getCity();
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City is required.");
        }
        String state = addr.getState();
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException("State is required.");
        }
        String pinCode = addr.getPincode();
        if (pinCode == null || pinCode.trim().isEmpty()) {
            throw new IllegalArgumentException("PinCode is required.");
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

        addressDAO.update(existing);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Address record modified successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return existing;
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

    public void create(AppUser usr, List<Address> records) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        addressDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Address record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
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

}
