package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Availability;
import com.folks.app.model.Professional;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.sql.Date;
import java.sql.Time;
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
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link AvailabilityBO}.
 *
 * <p>{@code fks_availabilities.professional_id} has a foreign key to
 * {@code fks_professionals}, so a real {@code User}/{@code Professional} pair is created via
 * {@link UserBO}/{@link ProfessionalBO} in {@code setup()}.
 *
 * <p>{@link AvailabilityBO#generate(AppUser, java.util.Map)},
 * {@link AvailabilityBO#viewSlotAvailability(AppUser, QueryParams)} and
 * {@link AvailabilityBO#viewProfessionalAvailability(AppUser, QueryParams)} are backed by
 * complex window-function/CTE SQL (see {@code AvailabilityQueryGen}) that scans every
 * professional/availability row in the database. Since the {@code DBExtension} shares a single
 * H2 instance across the whole test suite, a success-path fixture for those queries would be
 * extremely fragile (it would need to remain the exclusive matching data across every other test
 * class run in the same JVM). This class therefore sticks to the well-defined CRUD surface plus
 * the argument-validation path of the two query methods, and leaves {@code generate()} untested.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AvailabilityBOTest {

    private static AvailabilityBO availabilityBO;
    private static AppUser adminUser;

    private static Integer professionalId;
    private static Integer availabilityId;

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
        user.setFullName("Availability Test Pro");
        user.setPhone1("9812345201");
        user.setEmail("avail.test.pro@folks.test");

        try {
            new UserBO().create(adminUser, user);

            Professional professional = new Professional();
            professional.setUserId(user.getUserId());
            professional.setExperienceYears((short) 3);
            professional.setServingCities("Bengaluru");
            professional.setIsVerified((short) 1);
            
            new ProfessionalBO().create(adminUser, professional);
            professionalId = professional.getProfessionalId();
        }
        catch (Exception e) {
            fail(e);
        }

        availabilityBO = new AvailabilityBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        Availability availability = new Availability();
        availability.setProfessionalId(professionalId);
        availability.setDate(Date.valueOf("2026-09-10"));
        availability.setStartTime(Time.valueOf("09:00:00"));
        availability.setEndTime(Time.valueOf("10:00:00"));
        availability.setIsBooked((short) 0);

        Availability created = availabilityBO.create(adminUser, availability);

        availabilityId = created.getAvailabilityId();
        assertNotNull(availabilityId);
        assertNotNull(created.getCreatedAt());
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        Availability a1 = new Availability();
        a1.setProfessionalId(professionalId);
        a1.setDate(Date.valueOf("2026-09-10"));
        a1.setStartTime(Time.valueOf("10:00:00"));
        a1.setEndTime(Time.valueOf("11:00:00"));
        a1.setIsBooked((short) 0);

        Availability a2 = new Availability();
        a2.setProfessionalId(professionalId);
        a2.setDate(Date.valueOf("2026-09-10"));
        a2.setStartTime(Time.valueOf("11:00:00"));
        a2.setEndTime(Time.valueOf("12:00:00"));
        a2.setIsBooked((short) 0);

        availabilityBO.create(adminUser, Arrays.asList(a1, a2));

        assertNotNull(a1.getAvailabilityId());
        assertNotNull(a2.getAvailabilityId());
    }

    @Test
    @Order(3)
    public void testView() {
        Availability availability = availabilityBO.view(adminUser, availabilityId);

        assertEquals(availabilityId, availability.getAvailabilityId());
        assertEquals(professionalId, availability.getProfessionalId());
        assertEquals((short) 0, availability.getIsBooked());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> availabilityBO.view(adminUser, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("professionalId", Arrays.asList(String.valueOf(professionalId)));

        List<Availability> rows = availabilityBO.viewAll(adminUser, new QueryParams(params));

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> availabilityId.equals(r.getAvailabilityId())));
        assertTrue(rows.stream().allMatch(r -> professionalId.equals(r.getProfessionalId())));
    }

    @Test
    @Order(6)
    public void testModify() {
        Availability update = new Availability();
        update.setAvailabilityId(availabilityId);
        update.setProfessionalId(professionalId);
        update.setDate(Date.valueOf("2026-09-10"));
        update.setStartTime(Time.valueOf("09:00:00"));
        update.setEndTime(Time.valueOf("10:00:00"));
        update.setIsBooked((short) 1);

        Availability modified = availabilityBO.modify(adminUser, update);
        assertEquals((short) 1, modified.getIsBooked());

        Availability reloaded = availabilityBO.view(adminUser, availabilityId);
        assertEquals((short) 1, reloaded.getIsBooked());
    }

    @Test
    @Order(7)
    public void testModifyNotFound() {
        Availability update = new Availability();
        update.setAvailabilityId(-999);
        update.setProfessionalId(professionalId);
        update.setDate(Date.valueOf("2026-09-10"));
        update.setIsBooked((short) 0);

        assertThrows(IllegalArgumentException.class, () -> availabilityBO.modify(adminUser, update));
    }

    @Test
    @Order(8)
    public void testViewSlotAvailabilityServiceNotFound() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("serviceId", Arrays.asList("-999"));
        params.put("date", Arrays.asList("2026-09-10"));

        assertThrows(IllegalArgumentException.class,
                () -> availabilityBO.viewSlotAvailability(adminUser, new QueryParams(params)));
    }

    @Test
    @Order(9)
    public void testViewProfessionalAvailabilityServiceNotFound() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("serviceId", Arrays.asList("-999"));
        params.put("date", Arrays.asList("2026-09-10"));
        params.put("start", Arrays.asList("09:00:00"));
        params.put("end", Arrays.asList("18:00:00"));

        assertThrows(IllegalArgumentException.class,
                () -> availabilityBO.viewProfessionalAvailability(adminUser, new QueryParams(params)));
    }

    @Test
    @Order(10)
    public void testRemove() {
        Availability removed = availabilityBO.remove(adminUser, availabilityId);
        assertEquals(availabilityId, removed.getAvailabilityId());

        assertThrows(IllegalArgumentException.class, () -> availabilityBO.view(adminUser, availabilityId));
    }

    @Test
    @Order(11)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> availabilityBO.remove(adminUser, -999));
    }
}
