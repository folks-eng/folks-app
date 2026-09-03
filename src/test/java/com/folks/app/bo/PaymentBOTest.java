package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.model.Category;
import com.folks.app.model.Payment;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.util.DateUtil;
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
 * JUnit test cases for {@link PaymentBO}.
 *
 * <p>{@code fks_payments.booking_id} has a foreign key to {@code fks_bookings}, so a real
 * {@code Booking} row (with its own {@code User}/{@code Address}/{@code Category}/
 * {@code Service} fixture) is created in {@code setup()}. Note that
 * {@link PaymentBO#create(AppUser, Payment)} does not auto-populate {@code createdAt}, and
 * {@code transactionRef}/{@code paidAt} are {@code NOT NULL} columns, so the test data sets all
 * three explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentBOTest {

    private static PaymentBO paymentBO;
    private static AppUser adminUser;
    private static AppUser customerUsr;

    private static String bookingId;
    private static Integer paymentId;

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
        user.setFullName("Payment Test User");
        user.setPhone1("9812345501");
        user.setEmail("payment.test.user@folks.test");

        try {
            new UserBO().create(adminUser, user);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("sub", user.getExternalId());
        map.put("jti", UUID.randomUUID().toString());
        customerUsr = new AppUserImpl(new UserPrincipal(map));

        Address address = new Address();
        address.setAddressLine1("7 Church Street");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPincode(560001);
        address.setLabel("HOME");
        Address createdAddress = new AddressBO().create(customerUsr, address);

        Category category = new Category();
        category.setName("Payment Test Category");
        category.setIcon("card");
        category.setTagLine("For payment tests");
        category.setImage("card.png");
        category.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        new CategoryBO().create(adminUser, category);

        Service service = new Service();
        service.setCategoryId(category.getCategoryId());
        service.setName("Payment Test Service");
        service.setBasePrice(799.0);
        service.setCurrency("INR");
        service.setDurationMinutes((short) 45);
        service.setImage("payment-test-service.png");
        service.setReviews(0);
        new ServiceBO().create(adminUser, service);

        Booking booking = new Booking();
        booking.setServiceId(service.getServiceId());
        booking.setAddressId(createdAddress.getAddressId());
        booking.setScheduledAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        booking.setTimeSlot("16:00 - 17:00");
        booking.setTotalAmount(799.0);
        new BookingBO().create(customerUsr, booking);
        bookingId = booking.getBookingId();

        paymentBO = new PaymentBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(new BigDecimal("799.00"));
        payment.setPaymentMethod(Payment.Paymentmethod.UPI);
        payment.setPaymentStatus(Payment.Paymentstatus.INITIATED);
        payment.setTransactionRef("TXN-" + UUID.randomUUID());
        payment.setPaidAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        payment.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        paymentBO.create(customerUsr, payment);

        paymentId = payment.getPaymentId();
        assertNotNull(paymentId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        Payment p1 = new Payment();
        p1.setBookingId(bookingId);
        p1.setAmount(new BigDecimal("50.00"));
        p1.setPaymentMethod(Payment.Paymentmethod.WALLET);
        p1.setPaymentStatus(Payment.Paymentstatus.SUCCESS);
        p1.setTransactionRef("TXN-" + UUID.randomUUID());
        p1.setPaidAt(now);
        p1.setCreatedAt(now);

        Payment p2 = new Payment();
        p2.setBookingId(bookingId);
        p2.setAmount(new BigDecimal("25.00"));
        p2.setPaymentMethod(Payment.Paymentmethod.CARD);
        p2.setPaymentStatus(Payment.Paymentstatus.SUCCESS);
        p2.setTransactionRef("TXN-" + UUID.randomUUID());
        p2.setPaidAt(now);
        p2.setCreatedAt(now);

        paymentBO.create(customerUsr, Arrays.asList(p1, p2));

        assertNotNull(p1.getPaymentId());
        assertNotNull(p2.getPaymentId());
    }

    @Test
    @Order(3)
    public void testView() {
        Payment payment = paymentBO.view(customerUsr, paymentId);

        assertEquals(paymentId, payment.getPaymentId());
        assertEquals(bookingId, payment.getBookingId());
        assertEquals(Payment.Paymentmethod.UPI, payment.getPaymentMethod());
        assertEquals(Payment.Paymentstatus.INITIATED, payment.getPaymentStatus());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> paymentBO.view(customerUsr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("bookingId", Arrays.asList(bookingId));

        List<Payment> rows = paymentBO.viewAll(customerUsr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> paymentId.equals(r.getPaymentId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Payment update = new Payment();
        update.setPaymentId(paymentId);
        update.setBookingId(bookingId);
        update.setAmount(new BigDecimal("799.00"));
        update.setPaymentMethod(Payment.Paymentmethod.UPI);
        update.setPaymentStatus(Payment.Paymentstatus.SUCCESS);
        update.setTransactionRef("TXN-CONFIRMED");
        update.setPaidAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        Payment modified = paymentBO.modify(customerUsr, update);
        assertEquals(Payment.Paymentstatus.SUCCESS, modified.getPaymentStatus());
        assertEquals("TXN-CONFIRMED", modified.getTransactionRef());

        Payment reloaded = paymentBO.view(customerUsr, paymentId);
        assertEquals(Payment.Paymentstatus.SUCCESS, reloaded.getPaymentStatus());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Payment update = new Payment();
        update.setPaymentId(-999);
        update.setBookingId(bookingId);
        update.setAmount(new BigDecimal("1.00"));
        update.setTransactionRef("TXN-MISSING");
        update.setPaidAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        assertThrows(IllegalArgumentException.class, () -> paymentBO.modify(customerUsr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Payment removed = paymentBO.remove(customerUsr, paymentId);
        assertEquals(paymentId, removed.getPaymentId());

        assertThrows(IllegalArgumentException.class, () -> paymentBO.view(customerUsr, paymentId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> paymentBO.remove(customerUsr, -999));
    }
}
