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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AddressBO extends AbstractBO {
    
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
        
        // Fetch the user from db and associate it with the address object.
        User user = fetchUser(usr);
        address.setUserId(user.getUserId());

        addressDAO.insert(address);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Address created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return address;
    }

    public void create(AppUser usr, List<Address> records) {
        // Only admin is allowed to create addresses in bulk.
        ensureAdmin(usr);
        
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        addressDAO.insert(records);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Created {} Address record(s) successfully. Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
    }

    public Address modify(AppUser usr, Address address) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user.
        User user = fetchUser(usr);
        
        // First fetch the entry, to see if this already exists.
        Address existing = fetchAddress(address.getAddressId());
        ensureAuthorized(address, user.getUserId());
        
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

        // Fetch the user.
        User user = fetchUser(usr);

        // We need to fetch the addresses for the current user only.
        SearchCriteria search = SearchCriteria.from(params, user.getUserId());
        List<Address> rows = addressDAO.query(search);

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded address record(s). Elapsed time(ms): {}", rows.size(), timer.elapsedTimeMillis());
        }
        return rows;
    }

    public Address view(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user.
        User user = fetchUser(usr);

        Address address = fetchAddress(id);
        ensureAuthorized(address, user.getUserId());
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched address details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return address;
    }

    public Address remove(AppUser usr, Integer id) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user.
        User user = fetchUser(usr);

        // First fetch the entry, to see if this already exists.
        Address address = fetchAddress(id);
        ensureAuthorized(address, user.getUserId());
        
        addressDAO.delete(address);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Deleted Address. Id: {}. Elapsed time(ms): {}", id, timer.elapsedTimeMillis());
        }
        return address;
    }
    
    /**
     * Retrieves the address associated with the specified address identifier.
     * 
     * <p>
     * The address is looked up from the underlying data store using its primary key. If no address exists for
     * the supplied identifier, an exception is raised.
     * 
     * @param id    The unique identifier of the address to retrieve
     * @return      The address associated with the specified identifier
     * @throws IllegalArgumentException     If no address is found for the specified identifier
     */
    private Address fetchAddress(Integer id) {
        Address address = addressDAO.find(new Address.AddressPK(id));
        if (address == null) {
            throw new IllegalArgumentException("No address found for id: " + id);
        }
        return address;
    }

    
    /**
     * Retrieves the user associated with the authenticated application user.
     * 
     * <p>
     * The user's external identifier is obtained from the {@code sub} claim of the JWT principal and is used to
     * query the user data store.
     * 
     * <p>
     * The user may first be looked up from a distributed cache to avoid an * unnecessary database query.
     * If the user is not available in the cache, the persistent data store is queried as a fallback.
     *
     * @param usr   The authenticated application user containing the JWT principal
     * @return User The user associated with the external identifier
     * 
     * @throws IllegalArgumentException if no user exists for the external identifier
     */
    private User fetchUser(AppUser usr) {
        // Query the user based on external_id.
        // external_id will be part of jwt token as 'sub'.
        String extId = usr.principal().sub();

        User user = userDAO.select(extId);
        if (user == null) {
            throw new IllegalArgumentException("No User found for id: " + extId);
        }
        return user;
    }
    
    /**
     * Ensures that the specified address belongs to the user requesting the operation.
     * 
     * <p>
     * Authorization is determined by comparing the requesting user's identifier with the user identifier associated
     * with the address. If the identifiers do not match, the user is considered unauthorized to modify the address.
     * 
     * @param address   The address against which authorization is verified
     * @param userId    The identifier of the user requesting the operation
     * @throws IllegalAccessException   If the address does not belong to the requesting user
     */
    private void ensureAuthorized(Address address, Integer userId) {
        try {
            // Check if this address is associated with the user that has requested a change.
            if (! userId.equals(address.getUserId())) {
                throw new IllegalAccessException(UNAUTHORIZED_MSG);
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
