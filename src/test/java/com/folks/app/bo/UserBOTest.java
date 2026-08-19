package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.folks.app.util.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserBOTest {
    
    private static UserBO userBO;
    
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
        userBO = new UserBO();
    }
 
    @Test
    @Order(1)
    public void testCreate() {
        try {
            User user = new User();
            user.setFullName("Sudiptasish Chanda");
            user.setPhone1("8318990011");
            user.setEmail("zulu@yahoo.co.in");

            userBO.create(adminUser, user);
            
            extId = user.getExternalId();
            assertNotNull(extId);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    public void testView() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", extId);
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            User user = userBO.view(usr, extId);
            
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
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    public void testViewWithAdmin() {
        try {
            User user = userBO.view(adminUser, extId);
            fail("Admin should not have privilege to view");
        }
        catch (Exception e) {
            assertTrue(e instanceof IllegalAccessException);
        }
    }
    
    @Test
    @Order(4)
    public void testViewAll() {
        try {
            Map<String, List<String>> params = new HashMap<>();
            
            params.put("fullName", Arrays.asList("Sudiptasish Chanda"));
            params.put("email", Arrays.asList("zulu@yahoo.com"));
            params.put("role", Arrays.asList("CUSTOMER"));
            
            List<User> users = userBO.viewAll(adminUser, new QueryParams(params));
            assertTrue(users.isEmpty());
            
            params.put("email", Arrays.asList("zulu@yahoo.co.in"));
            users = userBO.viewAll(adminUser, new QueryParams(params));
            assertEquals(1, users.size());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(5)
    public void testDelete() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", extId);
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            // Delete this user
            userBO.remove(usr, extId);
            
            // Now query again
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                () -> {
                    User user = userBO.view(usr, extId);
                }
            );
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
