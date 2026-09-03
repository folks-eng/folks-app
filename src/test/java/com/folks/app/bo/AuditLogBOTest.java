package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.AuditLog;
import com.folks.app.util.QueryParams;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
 * JUnit test cases for {@link AuditLogBO}.
 *
 * <p>{@code fks_audit_logs} has no foreign key constraints, so this test does not need to
 * set up any dependent User/Booking fixtures.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuditLogBOTest {

    private static AuditLogBO auditLogBO;
    private static AppUser usr;

    private static Integer logId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        auditLogBO = new AuditLogBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        AuditLog log = new AuditLog();
        log.setUserId(101);
        log.setAction("LOGIN");
        log.setEntityType("USER");
        log.setEntityId(101);

        auditLogBO.create(usr, log);

        logId = log.getLogId();
        assertNotNull(logId);
        assertNotNull(log.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        AuditLog log1 = new AuditLog();
        log1.setUserId(102);
        log1.setAction("LOGOUT");
        log1.setEntityType("USER");
        log1.setEntityId(102);

        AuditLog log2 = new AuditLog();
        log2.setUserId(103);
        log2.setAction("UPDATE");
        log2.setEntityType("BOOKING");
        log2.setEntityId(55);

        auditLogBO.create(usr, Arrays.asList(log1, log2));

        assertNotNull(log1.getLogId());
        assertNotNull(log2.getLogId());
        assertNotNull(log1.getCreatedAt());
        assertNotNull(log2.getCreatedAt());
    }

    @Test
    @Order(3)
    public void testView() {
        AuditLog log = auditLogBO.view(usr, logId);

        assertEquals(logId, log.getLogId());
        assertEquals(101, log.getUserId());
        assertEquals("LOGIN", log.getAction());
        assertEquals("USER", log.getEntityType());
        assertEquals(101, log.getEntityId());
        assertNotNull(log.getCreatedAt());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> auditLogBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("action", Arrays.asList("LOGIN"));

        List<AuditLog> rows = auditLogBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> logId.equals(r.getLogId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        AuditLog update = new AuditLog();
        update.setLogId(logId);
        update.setUserId(101);
        update.setAction("LOGIN_FAILED");
        update.setEntityType("USER");
        update.setEntityId(101);

        AuditLog modified = auditLogBO.modify(usr, update);
        assertEquals("LOGIN_FAILED", modified.getAction());

        AuditLog reloaded = auditLogBO.view(usr, logId);
        assertEquals("LOGIN_FAILED", reloaded.getAction());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        AuditLog update = new AuditLog();
        update.setLogId(-999);
        update.setUserId(1);
        update.setAction("X");
        update.setEntityType("X");
        update.setEntityId(1);

        assertThrows(IllegalArgumentException.class, () -> auditLogBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        AuditLog removed = auditLogBO.remove(usr, logId);
        assertEquals(logId, removed.getLogId());

        assertThrows(IllegalArgumentException.class, () -> auditLogBO.view(usr, logId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> auditLogBO.remove(usr, -999));
    }
}
