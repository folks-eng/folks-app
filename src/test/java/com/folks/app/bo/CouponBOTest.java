package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Coupon;
import com.folks.app.util.QueryParams;
import java.math.BigDecimal;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link CouponBO}.
 *
 * <p>{@code fks_coupons} has no foreign key constraints. Note that unlike several other BO
 * classes, {@link CouponBO#create} does not auto-populate {@code createdAt}, so the test data
 * sets it explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CouponBOTest {

    private static CouponBO couponBO;
    private static AppUser usr;

    private static Integer couponId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        couponBO = new CouponBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Coupon coupon = new Coupon();
        coupon.setCode("SAVE10");
        coupon.setDiscountType("PERCENTAGE");
        coupon.setDiscountValue(new BigDecimal("10.00"));
        coupon.setMaxDiscount(new BigDecimal("100.00"));
        coupon.setExpiryDate(Date.valueOf("2027-12-31"));
        coupon.setUsageLimit((short) 100);
        coupon.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        couponBO.create(usr, coupon);

        couponId = coupon.getCouponId();
        assertNotNull(couponId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Coupon c1 = new Coupon();
        c1.setCode("WELCOME50");
        c1.setDiscountType("FLAT");
        c1.setDiscountValue(new BigDecimal("50.00"));
        c1.setExpiryDate(Date.valueOf("2027-06-30"));
        c1.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        Coupon c2 = new Coupon();
        c2.setCode("FESTIVE20");
        c2.setDiscountType("PERCENTAGE");
        c2.setDiscountValue(new BigDecimal("20.00"));
        c2.setExpiryDate(Date.valueOf("2027-11-15"));
        c2.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        couponBO.create(usr, Arrays.asList(c1, c2));

        assertNotNull(c1.getCouponId());
        assertNotNull(c2.getCouponId());
    }

    @Test
    @Order(3)
    public void testView() {
        Coupon coupon = couponBO.view(usr, couponId);

        assertEquals(couponId, coupon.getCouponId());
        assertEquals("SAVE10", coupon.getCode());
        assertEquals("PERCENTAGE", coupon.getDiscountType());
        assertEquals(0, new BigDecimal("10.00").compareTo(coupon.getDiscountValue()));
        assertEquals((short) 100, coupon.getUsageLimit());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> couponBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("code", Arrays.asList("SAVE10"));

        List<Coupon> rows = couponBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> couponId.equals(r.getCouponId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Coupon update = new Coupon();
        update.setCouponId(couponId);
        update.setCode("SAVE15");
        update.setDiscountType("PERCENTAGE");
        update.setDiscountValue(new BigDecimal("15.00"));
        update.setMaxDiscount(new BigDecimal("150.00"));
        update.setExpiryDate(Date.valueOf("2028-01-31"));
        update.setUsageLimit((short) 50);

        Coupon modified = couponBO.modify(usr, update);
        assertEquals("SAVE15", modified.getCode());

        Coupon reloaded = couponBO.view(usr, couponId);
        assertEquals("SAVE15", reloaded.getCode());
        assertEquals(0, new BigDecimal("15.00").compareTo(reloaded.getDiscountValue()));
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Coupon update = new Coupon();
        update.setCouponId(-999);
        update.setCode("X");

        assertThrows(IllegalArgumentException.class, () -> couponBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        Coupon removed = couponBO.remove(usr, couponId);
        assertEquals(couponId, removed.getCouponId());

        assertThrows(IllegalArgumentException.class, () -> couponBO.view(usr, couponId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> couponBO.remove(usr, -999));
    }
}
