package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Address;
import com.folks.app.model.Booking;
import com.folks.app.model.Category;
import com.folks.app.model.Coupon;
import com.folks.app.model.CouponUsage;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.sql.Date;
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
 * JUnit test cases for {@link CouponUsageBO}.
 *
 * <p>{@code fks_coupon_usage} has foreign keys on {@code coupon_id}, {@code user_id} and
 * {@code booking_id}, so real {@code Coupon}, {@code User} and {@code Booking} rows (the latter
 * needing its own {@code Address}/{@code Category}/{@code Service} fixture) are created in
 * {@code setup()}. Note that neither {@link CouponUsageBO#create(AppUser, CouponUsage)} nor
 * {@link CouponBO#create(AppUser, Coupon)} auto-populate {@code createdAt}/{@code usedAt}, so
 * the test data sets them explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouponUsageBOTest {

    private static CouponUsageBO couponUsageBO;
    private static AppUser adminUser;
    private static AppUser customerUsr;

    private static Integer userId;
    private static Integer couponId;
    private static String bookingId;
    private static Integer usageId;

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
        user.setFullName("Coupon Usage Test User");
        user.setPhone1("9812345701");
        user.setEmail("coupon.usage.test.user@folks.test");

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
        address.setAddressLine1("14 Lake View Road");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPincode(560037);
        address.setLabel("HOME");
        Address createdAddress = new AddressBO().create(customerUsr, address);

        Category category = new Category();
        category.setName("Coupon Usage Category");
        category.setIcon("tag");
        category.setTagLine("For coupon usage tests");
        category.setImage("tag.png");
        category.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        new CategoryBO().create(adminUser, category);

        Service service = new Service();
        service.setCategoryId(category.getCategoryId());
        service.setName("Coupon Usage Service");
        service.setBasePrice(299.0);
        service.setCurrency("INR");
        service.setDurationMinutes((short) 30);
        service.setImage("coupon-usage-service.png");
        service.setReviews(0);
        new ServiceBO().create(adminUser, service);

        Booking booking = new Booking();
        booking.setServiceId(service.getServiceId());
        booking.setAddressId(createdAddress.getAddressId());
        booking.setScheduledAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        booking.setTimeSlot("14:00 - 15:00");
        booking.setTotalAmount(299.0);
        new BookingBO().create(customerUsr, booking);
        bookingId = booking.getBookingId();

        Coupon coupon = new Coupon();
        coupon.setCode("SAVE10");
        coupon.setDiscountType("PERCENTAGE");
        coupon.setExpiryDate(Date.valueOf("2027-12-31"));
        coupon.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        new CouponBO().create(adminUser, coupon);
        couponId = coupon.getCouponId();

        couponUsageBO = new CouponUsageBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        CouponUsage usage = new CouponUsage();
        usage.setCouponId(couponId);
        usage.setUserId(userId);
        usage.setBookingId(bookingId);
        usage.setUsedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        usage.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        couponUsageBO.create(customerUsr, usage);

        usageId = usage.getUsageId();
        assertNotNull(usageId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

        CouponUsage u1 = new CouponUsage();
        u1.setCouponId(couponId);
        u1.setUserId(userId);
        u1.setBookingId(bookingId);
        u1.setUsedAt(now);
        u1.setCreatedAt(now);

        CouponUsage u2 = new CouponUsage();
        u2.setCouponId(couponId);
        u2.setUserId(userId);
        u2.setBookingId(bookingId);
        u2.setUsedAt(now);
        u2.setCreatedAt(now);

        couponUsageBO.create(customerUsr, Arrays.asList(u1, u2));

        assertNotNull(u1.getUsageId());
        assertNotNull(u2.getUsageId());
    }

    @Test
    @Order(3)
    public void testView() {
        CouponUsage usage = couponUsageBO.view(customerUsr, usageId);

        assertEquals(usageId, usage.getUsageId());
        assertEquals(couponId, usage.getCouponId());
        assertEquals(userId, usage.getUserId());
        assertEquals(bookingId, usage.getBookingId());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> couponUsageBO.view(customerUsr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("userId", Arrays.asList(String.valueOf(userId)));

        List<CouponUsage> rows = couponUsageBO.viewAll(customerUsr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> usageId.equals(r.getUsageId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Timestamp newUsedAt = new Timestamp(DateUtil.currentUTCDate().getTime() - 60000L);

        CouponUsage update = new CouponUsage();
        update.setUsageId(usageId);
        update.setCouponId(couponId);
        update.setUserId(userId);
        update.setBookingId(bookingId);
        update.setUsedAt(newUsedAt);

        CouponUsage modified = couponUsageBO.modify(customerUsr, update);
        assertEquals(newUsedAt, modified.getUsedAt());

        CouponUsage reloaded = couponUsageBO.view(customerUsr, usageId);
        assertEquals(newUsedAt, reloaded.getUsedAt());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        CouponUsage update = new CouponUsage();
        update.setUsageId(-999);
        update.setCouponId(couponId);
        update.setUserId(userId);
        update.setBookingId(bookingId);
        update.setUsedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        assertThrows(IllegalArgumentException.class, () -> couponUsageBO.modify(customerUsr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        CouponUsage removed = couponUsageBO.remove(customerUsr, usageId);
        assertEquals(usageId, removed.getUsageId());

        assertThrows(IllegalArgumentException.class, () -> couponUsageBO.view(customerUsr, usageId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> couponUsageBO.remove(customerUsr, -999));
    }
}
