package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.cache.impl.ServiceCache;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Category;
import com.folks.app.model.Professional;
import com.folks.app.model.ProfessionalService;
import com.folks.app.model.Service;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.sql.Timestamp;
import java.util.ArrayList;
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
 * JUnit test cases for {@link ProfessionalServiceBO}.
 *
 * <p>{@code fks_professional_services} has foreign keys on {@code professional_id} and
 * {@code service_id}, so a real {@code User}/{@code Professional} and a
 * {@code Category}/{@code Service} fixture are created in {@code setup()}. Note that
 * {@link ProfessionalServiceBO#create(AppUser, ProfessionalService)} does not auto-populate
 * {@code createdAt}, so the test data sets it explicitly.
 *
 * <p>{@link ProfessionalServiceBO#viewAll} resolves the calling professional via
 * {@code usr.principal().sub()} and then enriches each row's {@code serviceName} from the
 * in-memory {@link ServiceCache}, which is normally warmed by application startup. Since this
 * suite never starts the full application, the test pre-populates the cache itself, the same way
 * {@code AuthBOTest} pre-populates {@code UserRoleCache} for the client-credentials flow.
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfessionalServiceBOTest {

    private static ProfessionalServiceBO professionalServiceBO;
    private static AppUser adminUser;
    private static AppUser professionalUsr;

    private static Integer professionalId;
    private static Integer serviceId;
    private static Integer serviceId2;
    private static Integer professionalServiceId;

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
        user.setFullName("Professional Service Test Pro");
        user.setPhone1("9812345601");
        user.setEmail("prof.service.test.pro@folks.test");

        try {
            new UserBO().create(adminUser, user);

            Professional professional = new Professional();
            professional.setUserId(user.getUserId());
            professional.setExperienceYears((short) 5);
            professional.setServingCities("Bengaluru");
            professional.setIsVerified((short) 1);

            new ProfessionalBO().create(adminUser, professional);
            professionalId = professional.getProfessionalId();
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }

        Map<String, Object> map = new HashMap<>();
        map.put("sub", user.getExternalId());
        map.put("jti", UUID.randomUUID().toString());
        professionalUsr = new AppUserImpl(new UserPrincipal(map));

        Category category = new Category();
        category.setName("Professional Service Test Category");
        category.setIcon("brush");
        category.setTagLine("For professional service tests");
        category.setImage("brush.png");
        category.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
        new CategoryBO().create(adminUser, category);

        List<Service> services = new ArrayList<>();
        Service service = new Service();
        service.setCategoryId(category.getCategoryId());
        service.setName("Haircut");
        service.setBasePrice(199.0);
        service.setCurrency("INR");
        service.setDurationMinutes((short) 30);
        service.setImage("haircut.png");
        service.setReviews(0);
        services.add(service);
        
        service = new Service();
        service.setCategoryId(category.getCategoryId());
        service.setName("Ladies Haircut");
        service.setBasePrice(299.0);
        service.setCurrency("INR");
        service.setDurationMinutes((short) 60);
        service.setImage("haircut.png");
        service.setReviews(0);
        services.add(service);
        
        new ServiceBO().create(adminUser, services);
        serviceId = services.get(0).getServiceId();
        serviceId2 = services.get(1).getServiceId();
        
        professionalServiceBO = new ProfessionalServiceBO();
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertNotNull(serviceId);
        
        ProfessionalService ps = new ProfessionalService();
        ps.setProfessionalId(professionalId);
        ps.setServiceId(serviceId);
        ps.setPrice(179.0);
        ps.setIsActive((short) 1);
        ps.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

        professionalServiceBO.create(adminUser, ps);

        professionalServiceId = ps.getId();
        assertNotNull(professionalServiceId);
    }

    @Test
    @Order(2)
    public void testCreateBulk() {
        assertNotNull(serviceId2);
        
        try {
            Timestamp now = new Timestamp(DateUtil.currentUTCDate().getTime());

            ProfessionalService ps1 = new ProfessionalService();
            ps1.setProfessionalId(professionalId);
            ps1.setServiceId(serviceId2);
            ps1.setPrice(189.0);
            ps1.setIsActive((short) 0);
            ps1.setCreatedAt(now);

            professionalServiceBO.create(adminUser, Arrays.asList(ps1));

            assertNotNull(ps1.getId());
        }
        catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @Order(3)
    public void testView() {
        ProfessionalService ps = professionalServiceBO.view(adminUser, professionalServiceId);

        assertEquals(professionalServiceId, ps.getId());
        assertEquals(professionalId, ps.getProfessionalId());
        assertEquals(serviceId, ps.getServiceId());
        assertEquals(179.0, ps.getPrice());
    }

    @Test
    @Order(4)
    public void testViewNotFound() {
        assertThrows(IllegalArgumentException.class, () -> professionalServiceBO.view(adminUser, -999));
    }

    @Test
    @Order(5)
    public void testViewAll() {
        Service service = new Service();
        service.setServiceId(serviceId);
        service.setName("Haircut");
        ServiceCache.getCache().add(serviceId, service);
        
        service = new Service();
        service.setServiceId(serviceId2);
        service.setName("Ladies Haircut");
        ServiceCache.getCache().add(serviceId2, service);

        Map<String, List<String>> params = new HashMap<>();
        List<ProfessionalService> rows = professionalServiceBO.viewAll(professionalUsr, new QueryParams(params));
        assertEquals(2, rows.size());

        assertFalse(rows.isEmpty());
        assertTrue(rows.stream().anyMatch(r -> professionalServiceId.equals(r.getId())));
        assertTrue(rows.stream().allMatch(r -> professionalId.equals(r.getProfessionalId())));
        assertTrue(rows.stream().anyMatch(r -> "Haircut".equals(r.getServiceName())));
    }

    @Test
    @Order(6)
    public void testViewAllUnknownProfessional() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());
        AppUser stranger = new AppUserImpl(new UserPrincipal(map));

        Map<String, List<String>> params = new HashMap<>();
        assertThrows(IllegalArgumentException.class,
                () -> professionalServiceBO.viewAll(stranger, new QueryParams(params)));
    }

    @Test
    @Order(7)
    public void testModify() {
        ProfessionalService update = new ProfessionalService();
        update.setId(professionalServiceId);
        update.setProfessionalId(professionalId);
        update.setServiceId(serviceId);
        update.setPrice(159.0);
        update.setIsActive((short) 0);

        ProfessionalService modified = professionalServiceBO.modify(adminUser, update);
        assertEquals(159.0, modified.getPrice());
        assertEquals((short) 0, modified.getIsActive());

        ProfessionalService reloaded = professionalServiceBO.view(adminUser, professionalServiceId);
        assertEquals(159.0, reloaded.getPrice());
    }

    @Test
    @Order(8)
    public void testModifyNotFound() {
        ProfessionalService update = new ProfessionalService();
        update.setId(-999);
        update.setProfessionalId(professionalId);
        update.setServiceId(serviceId);
        update.setPrice(1.0);
        update.setIsActive((short) 1);

        assertThrows(IllegalArgumentException.class, () -> professionalServiceBO.modify(adminUser, update));
    }

    @Test
    @Order(9)
    public void testRemove() {
        ProfessionalService removed = professionalServiceBO.remove(adminUser, professionalServiceId);
        assertEquals(professionalServiceId, removed.getId());

        assertThrows(IllegalArgumentException.class, () -> professionalServiceBO.view(adminUser, professionalServiceId));
    }

    @Test
    @Order(10)
    public void testRemoveNotFound() {
        assertThrows(IllegalArgumentException.class, () -> professionalServiceBO.remove(adminUser, -999));
    }
}
