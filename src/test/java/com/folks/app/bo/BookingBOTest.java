package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.model.Category;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.jpa.util.MD5HashGenerator;
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
 * JUnit test cases for {@link BookingBO}.
 *
 * <p>{@code fks_bookings} has foreign keys on {@code service_id} and {@code address_id}, so a
 * real {@code User}/{@code Address}/{@code Category}/{@code Service} fixture is built in
 * {@code setup()} via {@link UserBO}, {@link AddressBO}, {@link CategoryBO} and
 * {@link ServiceBO}. Note that {@link BookingBO#create(AppUser, Booking)} ignores whatever
 * {@code customerId}/{@code professionalId}/{@code status}/{@code bookingId} the caller sets and
 * derives them itself (customer from the JWT principal, professional as the placeholder
 * {@code -1}, status {@code PENDING}, and the booking id as an MD5 digest); the bulk
 * {@link BookingBO#create(AppUser, List)} overload does not do any of that, so the bulk test
 * populates those fields itself.
 *
 * <p>{@link BookingBO#freeProfessional(Booking)} depends on matching an existing
 * {@code Availability} row via a generated {@code SearchCriteria} built from the booking's time
 * slot, which this suite cannot fabricate reliably without seeing the DAO's exact SQL; it is left
 * untested here. {@link BookingBO#assignProfessional(Booking)} is exercised only for its
 * guard-clause (rejecting a non-{@code PENDING} booking), since the successful DB update is
 * likewise implemented by a native query this suite has no visibility into.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingBOTest {

    private static BookingBO bookingBO;
    private static AppUser adminUser;
    private static AppUser customerUsr;

    private static Integer userId;
    private static Integer addressId;
    private static Integer serviceId;
    private static String bookingId;

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
        user.setFullName("Booking Test User");
        user.setPhone1("9812345301");
        user.setEmail("booking.test.user@folks.test");

        try {
            new UserBO().create(adminUser, user);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
        userId = user.getUserId();

        Map<String, Object> map = new HashMap<>();
        map.put("sub", user.getExternalId());
        map.put("jti", UUID.randomUUID().toString());
        customerUsr = new AppUserImpl(new UserPrincipal(map));

        Address address = new Address();
        address.setAddressLine1("42 Residency Road");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPincode(560025);
        address.setLabel("HOME");

        Address createdAddress = new AddressBO().create(customerUsr, address);
        addressId = createdAddress.getAddressId();

        Category category = new Category();
        category.setName("Home Services");
        category.setIcon("home");
        category.setTagLine("Everything for your home");
        category.setImage("home.png");
        category.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        new CategoryBO().create(adminUser, category);

        Service service = new Service();
        service.setCategoryId(category.getCategoryId());
        service.setName("Deep Cleaning");
        service.setBasePrice(499.0);
        service.setCurrency("INR");
        service.setDurationMinutes((short) 60);
        service.setImage("deep-cleaning.png");
        service.setReviews(0);
        new ServiceBO().create(adminUser, service);
        serviceId = service.getServiceId();

        bookingBO = new BookingBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Booking booking = new Booking();
        booking.setServiceId(serviceId);
        booking.setAddressId(addressId);
        booking.setScheduledAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        booking.setTimeSlot("09:00 - 10:00");
        booking.setTotalAmount(499.0);

        Booking created = bookingBO.create(customerUsr, booking);

        bookingId = created.getBookingId();
        assertNotNull(bookingId);
        assertEquals(userId, created.getCustomerId());
        assertEquals(-1, created.getProfessionalId());
        assertEquals(Booking.Status.PENDING, created.getStatus());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Timestamp scheduledAt = new Timestamp(DateUtil.currentUTCDate().getTime());

        Booking b1 = new Booking();
        b1.setBookingId(MD5HashGenerator.digest(String.valueOf(userId), String.valueOf(serviceId), String.valueOf(addressId), String.valueOf(scheduledAt), "10:00 - 11:00"));
        b1.setCustomerId(userId);
        b1.setProfessionalId(-1);
        b1.setServiceId(serviceId);
        b1.setAddressId(addressId);
        b1.setScheduledAt(scheduledAt);
        b1.setTimeSlot("10:00 - 11:00");
        b1.setStatus(Booking.Status.PENDING);
        b1.setTotalAmount(499.0);

        Booking b2 = new Booking();
        b2.setBookingId(MD5HashGenerator.digest(String.valueOf(userId), String.valueOf(serviceId), String.valueOf(addressId), String.valueOf(scheduledAt), "11:00 - 12:00"));
        b2.setCustomerId(userId);
        b2.setProfessionalId(-1);
        b2.setServiceId(serviceId);
        b2.setAddressId(addressId);
        b2.setScheduledAt(scheduledAt);
        b2.setTimeSlot("11:00 - 12:00");
        b2.setStatus(Booking.Status.PENDING);
        b2.setTotalAmount(499.0);

        bookingBO.create(customerUsr, Arrays.asList(b1, b2));

        assertNotNull(bookingBO.view(customerUsr, b1.getBookingId()));
        assertNotNull(bookingBO.view(customerUsr, b2.getBookingId()));
    }

    @Test
    @Order(3)
    public void testView() {
        Booking booking = bookingBO.view(customerUsr, bookingId);

        assertEquals(bookingId, booking.getBookingId());
        assertEquals(serviceId, booking.getServiceId());
        assertEquals(addressId, booking.getAddressId());
        assertEquals("09:00 - 10:00", booking.getTimeSlot());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> bookingBO.view(customerUsr, "no-such-booking"));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        List<Booking> rows = bookingBO.viewAll(customerUsr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> bookingId.equals(r.getBookingId())));
        assertTrue(rows.stream().allMatch(r -> userId.equals(r.getCustomerId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Booking update = new Booking();
        update.setBookingId(bookingId);
        update.setCustomerId(userId);
        update.setProfessionalId(-1);
        update.setServiceId(serviceId);
        update.setAddressId(addressId);
        update.setScheduledAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        update.setTimeSlot("09:00 - 10:00");
        update.setStatus(Booking.Status.CONFIRMED);
        update.setTotalAmount(599.0);

        Booking modified = bookingBO.modify(customerUsr, update);
        assertEquals(Booking.Status.CONFIRMED, modified.getStatus());
        assertEquals(599.0, modified.getTotalAmount());

        Booking reloaded = bookingBO.view(customerUsr, bookingId);
        assertEquals(Booking.Status.CONFIRMED, reloaded.getStatus());
    }

    @Test
    @Order(7)
    public void testModifyAlreadyConfirmed() {
        Booking update = new Booking();
        update.setBookingId(bookingId);
        update.setCustomerId(userId);
        update.setProfessionalId(-1);
        update.setServiceId(serviceId);
        update.setAddressId(addressId);
        update.setScheduledAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        update.setTimeSlot("09:00 - 10:00");
        update.setStatus(Booking.Status.CONFIRMED);
        update.setTotalAmount(699.0);

        assertThrows(IllegalArgumentException.class, () -> bookingBO.modify(customerUsr, update));
    }

    @Test
    @Order(8)
    public void testModifyNotFound() {
        Booking update = new Booking();
        update.setBookingId("no-such-booking");
        update.setCustomerId(userId);
        update.setStatus(Booking.Status.PENDING);

        assertThrows(IllegalArgumentException.class, () -> bookingBO.modify(customerUsr, update));
    }

    @Test
    @Order(9)
    public void testAssignProfessionalRejectsNonPending() {
        // At this point bookingId is CONFIRMED (see testModify), so it can no longer accept a
        // professional assignment.
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setProfessionalId(101);

        assertThrows(IllegalArgumentException.class, () -> bookingBO.assignProfessional(booking));
    }

    @Test
    @Order(10)
    public void testAssignProfessionalNotFound() {
        Booking booking = new Booking();
        booking.setBookingId("no-such-booking");
        booking.setProfessionalId(101);

        assertThrows(IllegalArgumentException.class, () -> bookingBO.assignProfessional(booking));
    }

    @Test
    @Order(11)
    public void testRemove() {
        Booking removed = bookingBO.remove(customerUsr, bookingId);
        assertEquals(Booking.Status.CANCELLED, removed.getStatus());
        assertEquals("Cancelled by user", removed.getStatusMsg());

        Booking reloaded = bookingBO.view(customerUsr, bookingId);
        assertEquals(Booking.Status.CANCELLED, reloaded.getStatus());
    }

    @Test
    @Order(12)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> bookingBO.remove(customerUsr, "no-such-booking"));
    }
}
