package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfessionalBOTest {
    
    private static ProfessionalBO professionalBO;
    
    private static String extId;
    private static AppUser adminUser;
    
    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("name", "Admin User");
        map.put("priv", "admin");
        map.put("scope", "user:create|user:query");
        map.put("jti", UUID.randomUUID().toString());
        
        adminUser = new AppUserImpl(new UserPrincipal(map));
        professionalBO = new ProfessionalBO();
    }
 
    // @Test
    @Order(1)
    public void testCreate() {
        try {
            User user = new User();
            user.setFullName("Sudiptasish Chanda");
            user.setPhone1("8318990011");
            user.setEmail("zulu@yahoo.co.in");

            // userBO.create(adminUser, user);
            
            extId = user.getExternalId();
            assertNotNull(extId);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    // @Test
    @Order(2)
    public void testView() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", extId);
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            // User user = userBO.view(usr, extId);
            User user = null;
            
            assertEquals(extId, user.getExternalId());
            assertEquals("Sudiptasish Chanda", user.getFullName());
            assertEquals("8318990011", user.getPhone1());
            assertEquals("zulu@yahoo.co.in", user.getEmail());
            assertEquals(User.Role.CUSTOMER, user.getRole());
            assertEquals(User.Status.ACTIVE, user.getStatus());
            
            assertNotNull(user.getCreatedAt());
            assertNull(user.getPhone2());
            assertNull(user.getUpdatedAt());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
