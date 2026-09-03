package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.JobStatus;
import com.folks.app.util.QueryParams;
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
 * JUnit test cases for {@link JobStatusBO}.
 *
 * <p>{@code fks_job_status} has no foreign key constraint on {@code booking_id}, so a random
 * identifier can be used in place of a real {@code Booking} row. Note that
 * {@link JobStatusBO#create} does not auto-populate {@code createdAt}, so the test data sets
 * it explicitly.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobStatusBOTest {

    private static JobStatusBO jobStatusBO;
    private static AppUser usr;

    private static String bookingId;
    private static Integer logId;

    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        usr = new AppUserImpl(new UserPrincipal(map));
        jobStatusBO = new JobStatusBO();
        bookingId = UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    public void testCreate() {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setBookingId(bookingId);
        jobStatus.setStatus("ASSIGNED");
        jobStatus.setUpdatedBy(101);
        jobStatus.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        jobStatusBO.create(usr, jobStatus);

        logId = jobStatus.getLogId();
        assertNotNull(logId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        JobStatus j1 = new JobStatus();
        j1.setBookingId(bookingId);
        j1.setStatus("IN_PROGRESS");
        j1.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        JobStatus j2 = new JobStatus();
        j2.setBookingId(bookingId);
        j2.setStatus("COMPLETED");
        j2.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        jobStatusBO.create(usr, Arrays.asList(j1, j2));

        assertNotNull(j1.getLogId());
        assertNotNull(j2.getLogId());
    }

    @Test
    @Order(3)
    public void testView() {
        JobStatus jobStatus = jobStatusBO.view(usr, logId);

        assertEquals(logId, jobStatus.getLogId());
        assertEquals(bookingId, jobStatus.getBookingId());
        assertEquals("ASSIGNED", jobStatus.getStatus());
        assertEquals(101, jobStatus.getUpdatedBy());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> jobStatusBO.view(usr, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("bookingId", Arrays.asList(bookingId));

        List<JobStatus> rows = jobStatusBO.viewAll(usr, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> logId.equals(r.getLogId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        JobStatus update = new JobStatus();
        update.setLogId(logId);
        update.setBookingId(bookingId);
        update.setStatus("CANCELLED");
        update.setUpdatedBy(102);

        JobStatus modified = jobStatusBO.modify(usr, update);
        assertEquals("CANCELLED", modified.getStatus());

        JobStatus reloaded = jobStatusBO.view(usr, logId);
        assertEquals("CANCELLED", reloaded.getStatus());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        JobStatus update = new JobStatus();
        update.setLogId(-999);
        update.setBookingId(bookingId);
        update.setStatus("X");

        assertThrows(IllegalArgumentException.class, () -> jobStatusBO.modify(usr, update));
    }

    @Test
    @Order(8)
    public void testRemove() {
        JobStatus removed = jobStatusBO.remove(usr, logId);
        assertEquals(logId, removed.getLogId());

        assertThrows(IllegalArgumentException.class, () -> jobStatusBO.view(usr, logId));
    }

    @Test
    @Order(9)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> jobStatusBO.remove(usr, -999));
    }
}
