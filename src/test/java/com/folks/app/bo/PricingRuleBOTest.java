package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.PricingRule;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link PricingRuleBO}.
 *
 * <p>{@code fks_pricing_rules} has no foreign key constraint on {@code service_id}, so a plain
 * integer can be used in place of a real {@code Service} row. Note that
 * {@link PricingRuleBO#create} does not auto-populate {@code createdAt}, so the test data sets
 * it explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PricingRuleBOTest {

    private static PricingRuleBO pricingRuleBO;
    private static AppUser usr;

    private static Integer ruleId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        pricingRuleBO = new PricingRuleBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        PricingRule rule = new PricingRule();
        rule.setServiceId(501);
        rule.setCity("Bengaluru");
        rule.setMultiplier(new BigDecimal("1.25"));
        rule.setStartTime(Timestamp.valueOf("2027-01-01 18:00:00"));
        rule.setEndTime(Timestamp.valueOf("2027-01-01 22:00:00"));
        rule.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        pricingRuleBO.create(usr, rule);

        ruleId = rule.getRuleId();
        assertNotNull(ruleId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        PricingRule r1 = new PricingRule();
        r1.setServiceId(502);
        r1.setCity("Mumbai");
        r1.setMultiplier(new BigDecimal("1.50"));
        r1.setStartTime(Timestamp.valueOf("2027-02-01 09:00:00"));
        r1.setEndTime(Timestamp.valueOf("2027-02-01 12:00:00"));
        r1.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        PricingRule r2 = new PricingRule();
        r2.setServiceId(503);
        r2.setCity("Delhi");
        r2.setMultiplier(new BigDecimal("1.10"));
        r2.setStartTime(Timestamp.valueOf("2027-03-01 09:00:00"));
        r2.setEndTime(Timestamp.valueOf("2027-03-01 12:00:00"));
        r2.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        pricingRuleBO.create(usr, Arrays.asList(r1, r2));

        assertNotNull(r1.getRuleId());
        assertNotNull(r2.getRuleId());
    }

    @Test
    @Order(3)
    public void testView() {
        PricingRule rule = pricingRuleBO.view(usr, ruleId);

        assertEquals(ruleId, rule.getRuleId());
        assertEquals(501, rule.getServiceId());
        assertEquals("Bengaluru", rule.getCity());
        assertEquals(0, new BigDecimal("1.25").compareTo(rule.getMultiplier()));
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> pricingRuleBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("city", Arrays.asList("Bengaluru"));

        List<PricingRule> rows = pricingRuleBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> ruleId.equals(r.getRuleId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        PricingRule update = new PricingRule();
        update.setRuleId(ruleId);
        update.setServiceId(501);
        update.setCity("Bengaluru");
        update.setMultiplier(new BigDecimal("1.75"));
        update.setStartTime(Timestamp.valueOf("2027-01-01 19:00:00"));
        update.setEndTime(Timestamp.valueOf("2027-01-01 23:00:00"));

        PricingRule modified = pricingRuleBO.modify(usr, update);
        assertEquals(0, new BigDecimal("1.75").compareTo(modified.getMultiplier()));

        PricingRule reloaded = pricingRuleBO.view(usr, ruleId);
        assertEquals(0, new BigDecimal("1.75").compareTo(reloaded.getMultiplier()));
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        PricingRule update = new PricingRule();
        update.setRuleId(-999);
        update.setServiceId(1);
        update.setCity("X");
        update.setMultiplier(BigDecimal.ONE);
        update.setStartTime(new Timestamp(DateUtil.currentUTCDate().getTime()));
        update.setEndTime(new Timestamp(DateUtil.currentUTCDate().getTime()));

        assertThrows(IllegalArgumentException.class, () -> pricingRuleBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        PricingRule removed = pricingRuleBO.remove(usr, ruleId);
        assertEquals(ruleId, removed.getRuleId());

        assertThrows(IllegalArgumentException.class, () -> pricingRuleBO.view(usr, ruleId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> pricingRuleBO.remove(usr, -999));
    }
}
