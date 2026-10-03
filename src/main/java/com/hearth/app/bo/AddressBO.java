package com.hearth.app.bo;

import com.hearth.app.util.Constants;
import org.javalabs.decl.util.StopWatch;
import org.javalabs.jpa.DAOProxy;
import com.hearth.app.auth.AppUser;
import com.hearth.app.cache.impl.NeighbourhoodCache;
import com.hearth.app.dao.AddressDAO;
import com.hearth.app.dao.BookingDAO;
import com.hearth.app.model.Address;
import com.hearth.app.model.Booking;
import com.hearth.app.model.User;
import com.hearth.app.util.AddressUtil;
import com.hearth.app.util.QueryParams;
import com.hearth.app.util.SearchCriteria;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AddressBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AddressBO.class);
    
    private final AddressDAO addressDAO;
    private final BookingDAO bookingDAO;
    
    public AddressBO() {
        this.addressDAO = DAOProxy.get(AddressDAO.class);
        this.bookingDAO = DAOProxy.get(BookingDAO.class);
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Initialized Handler: {}. AddressDAO: {}. UserDAO: {}", getClass().getSimpleName(), addressDAO, userDAO);
        }
    }

    public Address create(AppUser usr, Address address) {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user from db and associate it with the address object.
        User user = fetchUser(usr);
        
        // Check if the neighbourhoodId is valid.
        if (! NeighbourhoodCache.getCache().contains(address.getNeighbourhoodId())) {
            throw new IllegalArgumentException("Invalid neighbourhood id: " + address.getNeighbourhoodId()
                    + ". Provide a valid neighbourhood.");
        }
        
        // Fetch existing address(s) from the db first, and check if an address exist for the same neighbourhood.
        SearchCriteria search = SearchCriteria.from(new QueryParams(new HashMap<>()), user.getUserId());
        List<Address> records = addressDAO.query(search);
        if (! records.isEmpty()) {
            for (Address record : records) {
                if (record.getNeighbourhoodId().equals(address.getNeighbourhoodId())
                        && AddressUtil.compare(record.getAddressLine1(), address.getAddressLine1())) {
                    throw new IllegalArgumentException("You already have this address registered");
                }
            }
        }
        // All good, proceed ...
        address.setUserId(user.getUserId());
        address.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        if (address.getIsDefault() == null) {
            address.setIsDefault(Constants.IS_DEFAULT_ADDR);     // All addresses are set to default.
        }
        AddressUtil.enrich(address);

        addressDAO.insert(address);
        timer.stop();

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Address created successfully. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return address;
    }

    public void create(AppUser usr, List<Address> records) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();
        
        // Fetch the user from db and associate it with the address object.
        User user = fetchUser(usr);
        Timestamp createdAt = new Timestamp(DateUtil.currentUTCDate().getTime());
        
        // No duplicate check is done for bulk address insert.
        for (Address address : records) {
            if (! NeighbourhoodCache.getCache().contains(address.getNeighbourhoodId())) {
                throw new IllegalArgumentException("Invalid neighbourhood id: " + address.getNeighbourhoodId()
                        + "Provide a valid neighbourhood.");
            }
            address.setUserId(user.getUserId());
            address.setCreatedAt(createdAt);
            if (address.getIsDefault() == null) {
                address.setIsDefault(Constants.IS_DEFAULT_ADDR);     // All addresses are set to default.
            }
            AddressUtil.enrich(address);
        }
        
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
        ensureAuthorized(existing, user.getUserId());
        
        // Update attributes of existing record
        existing.setUserId(address.getUserId());
        existing.setAddressLine1(address.getAddressLine1());
        existing.setAddressLine2(address.getAddressLine2());
        existing.setNeighbourhoodId(address.getNeighbourhoodId());
        existing.setLatitude(address.getLatitude());
        existing.setLongitude(address.getLongitude());
        existing.setLabel(address.getLabel());
        existing.setIsDefault(address.getIsDefault());       // The UI is not sending it today.
        existing.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        
        AddressUtil.enrich(existing);

        addressDAO.update(existing);
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
        List<Address> records = addressDAO.query(search);
        
        for (Address record : records) {
            AddressUtil.enrich(record);
        }

        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched {} expanded address record(s). Elapsed time(ms): {}", records.size(), timer.elapsedTimeMillis());
        }
        return records;
    }

    public Address view(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user.
        User user = fetchUser(usr);

        Address address = fetchAddress(id);
        AddressUtil.enrich(address);
        
        ensureAuthorized(address, user.getUserId());
        
        timer.stop();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fetched address details. Elapsed time(ms): {}", timer.elapsedTimeMillis());
        }
        return address;
    }

    public Address remove(AppUser usr, Integer id) throws IllegalAccessException {
        StopWatch timer = StopWatch.newTimer();
        timer.start();

        // Fetch the user.
        User user = fetchUser(usr);

        // First fetch the entry, to see if this already exists.
        Address address = fetchAddress(id);
        ensureAuthorized(address, user.getUserId());
        
        // Check if the address has any booking associated to it.
        // We need to fetch the addresses for the current user only.
        Map<String, List<Object>> params = new HashMap<>();
        params.put("customer_id", List.of(user.getUserId()));
        params.put("address_id", List.of(id));
        params.put("status", List.of(Booking.Status.PENDING));
        
        SearchCriteria search = SearchCriteria.from(params);
        List<Booking> bookings = bookingDAO.query(search);
        if (! bookings.isEmpty()) {
            throw new IllegalArgumentException("You already have pending booking(s) for this address. Delete the booking(s) first");
        }
        
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
            throw new ResourceNotFoundException("No address found for id: " + id);
        }
        return address;
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
    private void ensureAuthorized(Address address, Integer userId) throws IllegalAccessException {
        // Check if this address is associated with the user that has requested the change.
        if (! userId.equals(address.getUserId())) {
            throw new IllegalAccessException(UNAUTHORIZED_MSG);
        }
    }
}
