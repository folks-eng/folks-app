package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Address;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link AddressBO}.
 *
 * <p>{@code fks_addresses.user_id} has a foreign key to {@code fks_users}, so two real
 * {@code User} rows are created via {@link UserBO} in {@code setup()}: one whose address is
 * under test, and a second used to exercise the unauthorized-access paths.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressBOTest {

    private static AddressBO addressBO;
    private static AppUser adminUser;
    private static AppUser customerUsr;
    private static AppUser otherCustomerUsr;

    private static Integer userId;
    private static Integer otherUserId;
    private static Integer addressId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("sub", UUID.randomUUID().toString());
        adminMap.put("name", "Admin User");
        adminMap.put("priv", "admin");
        adminMap.put("scope", "user:create|user:query");
        adminMap.put("jti", UUID.randomUUID().toString());
        adminUser = new AppUserImpl(new UserPrincipal(adminMap));

        User user = new User();
        user.setFullName("Address Test User");
        user.setPhone1("9812345001");
        user.setEmail("addr.test.user@folks.test");

        User otherUser = new User();
        otherUser.setFullName("Address Test User Two");
        otherUser.setPhone1("9812345002");
        otherUser.setEmail("addr.test.user2@folks.test");

        try {
            new UserBO().create(adminUser, user);
            new UserBO().create(adminUser, otherUser);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
        userId = user.getUserId();
        otherUserId = otherUser.getUserId();

        Map<String, Object> map = new HashMap<>();
        map.put("sub", user.getExternalId());
        map.put("jti", UUID.randomUUID().toString());
        customerUsr = new AppUserImpl(new UserPrincipal(map));

        Map<String, Object> otherMap = new HashMap<>();
        otherMap.put("sub", otherUser.getExternalId());
        otherMap.put("jti", UUID.randomUUID().toString());
        otherCustomerUsr = new AppUserImpl(new UserPrincipal(otherMap));

        addressBO = new AddressBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Address address = new Address();
        address.setAddressLine1("221B Baker Street");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPincode(560001);
        address.setLabel("HOME");

        Address created = addressBO.create(customerUsr, address);

        addressId = created.getAddressId();
        assertNotNull(addressId);
        assertEquals(userId, created.getUserId());
        assertEquals((short) 1, created.getIsDefault());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulkRequiresAdmin() {
        Address address = new Address();
        address.setUserId(userId);
        address.setAddressLine1("Bulk Street");
        address.setCity("Pune");
        address.setState("Maharashtra");
        address.setPincode(411001);
        address.setLabel("OFFICE");
        address.setIsDefault((short) 0);
        address.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        assertThrows(IllegalAccessException.class,
                () -> addressBO.create(customerUsr, List.of(address)));
    }

    @Test
    @Order(3)
    public void testCreateBulkAsAdmin() throws IllegalAccessException {
        Address a1 = new Address();
        a1.setUserId(userId);
        a1.setAddressLine1("MG Road");
        a1.setCity("Bengaluru");
        a1.setState("Karnataka");
        a1.setPincode(560002);
        a1.setLabel("OFFICE");
        a1.setIsDefault((short) 0);
        a1.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        addressBO.create(adminUser, List.of(a1));

        assertNotNull(a1.getAddressId());
        Address fetched = addressBO.view(customerUsr, a1.getAddressId());
        assertEquals("MG Road", fetched.getAddressLine1());
    }

    @Test
    @Order(4)
    public void testView() throws IllegalAccessException {
        Address address = addressBO.view(customerUsr, addressId);

        assertEquals(addressId, address.getAddressId());
        assertEquals("221B Baker Street", address.getAddressLine1());
        assertEquals("Bengaluru", address.getCity());
        assertEquals("Karnataka", address.getState());
        assertEquals(560001, address.getPincode());
    }

    @Test
    @Order(5)
    public void testViewUnauthorized() {
        assertThrows(IllegalAccessException.class, () -> addressBO.view(otherCustomerUsr, addressId));
    }

    @Test
    @Order(6)
    public void testViewNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> addressBO.view(customerUsr, -999));
    }

    @Test
    @Order(7)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        List<Address> rows = addressBO.viewAll(customerUsr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> addressId.equals(r.getAddressId())));
        assertTrue(rows.stream().allMatch(r -> userId.equals(r.getUserId())));
    }

    @Test
    @Order(8)
    public void testModify() throws IllegalAccessException {
        Address update = new Address();
        update.setAddressId(addressId);
        update.setUserId(userId);
        update.setAddressLine1("221B Baker Street, Apt 4");
        update.setAddressLine2("Near Central Park");
        update.setCity("Bengaluru");
        update.setState("Karnataka");
        update.setPincode(560003);

        Address modified = addressBO.modify(customerUsr, update);
        assertEquals("221B Baker Street, Apt 4", modified.getAddressLine1());
        assertEquals(560003, modified.getPincode());
        assertNotNull(modified.getUpdatedAt());

        Address reloaded = addressBO.view(customerUsr, addressId);
        assertEquals("Near Central Park", reloaded.getAddressLine2());
    }

    @Test
    @Order(9)
    public void testModifyUnauthorized() {
        Address update = new Address();
        update.setAddressId(addressId);
        update.setUserId(userId);
        update.setAddressLine1("Hijacked");
        update.setCity("Bengaluru");
        update.setState("Karnataka");
        update.setPincode(560004);

        assertThrows(IllegalAccessException.class, () -> addressBO.modify(otherCustomerUsr, update));
    }

    @Test
    @Order(10)
    public void testRemoveUnauthorized() {
        assertThrows(IllegalAccessException.class, () -> addressBO.remove(otherCustomerUsr, addressId));
    }

    @Test
    @Order(11)
    public void testRemove() throws IllegalAccessException {
        Address removed = addressBO.remove(customerUsr, addressId);
        assertEquals(addressId, removed.getAddressId());

        assertThrows(ResourceNotFoundException.class, () -> addressBO.view(customerUsr, addressId));
    }

    @Test
    @Order(12)
    public void testRemoveNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> addressBO.remove(customerUsr, -999));
    }
}
