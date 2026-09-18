package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.javalabs.decl.vertx.container.ResourceAlreadyExistsException;
import org.javalabs.decl.vertx.container.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
public class AddressBOTest {
    
    private static UserBO userBO;
    
    private static AppUser ADMIN_USER;
    
    @BeforeAll
    public static void setup() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("name", "Admin User");
        map.put("priv", "admin");
        map.put("scope", "user:create|user:query");
        map.put("jti", UUID.randomUUID().toString());
        
        ADMIN_USER = new AppUserImpl(new UserPrincipal(map));
        userBO = new UserBO();
    }
    
    private AppUser user(String externalId, String name) {
        Map<String, Object> map = new HashMap<>();
        
        map.put("sub", externalId);
        map.put("name", name);
        map.put("jti", UUID.randomUUID().toString());
        
        return new AppUserImpl(new UserPrincipal(map));
    }
    
    private User createNew() {
        String rd = UUID.randomUUID().toString().substring(0, 8);
        
        long randomDigits = ThreadLocalRandom.current().nextLong(7000000000L, 9999999999L);
        String randomUser = "User - " + rd;
        String randomEmail = "user_" + rd + "@yahoo.co.in";

        User user = new User();
        user.setFullName(randomUser);
        user.setPhone1(String.valueOf(randomDigits));
        user.setEmail(randomEmail);
        
        return user;
    }
 
    @Test
    public void testCreateUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // Verify the user is created.
            // Prepare the AppUser for this user.
            AppUser usr = user(user.getExternalId(), user.getFullName());
            User current = userBO.view(usr, user.getExternalId());
            assertNotNull(current);
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            assertEquals(user.getPhone1(), current.getPhone1());
            assertEquals(user.getEmail(), current.getEmail());
            assertEquals(User.Role.CUSTOMER, current.getRole());
            assertEquals(User.Status.ACTIVE, current.getStatus());
            assertEquals(df.format(new Date()), df.format(current.getCreatedAt()));
            assertNull(current.getPasswordHash());
            assertNull(current.getUpdatedAt());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
 
    @Test
    public void testCreateDuplicateUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // Try to create the user again.
            ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> {
                userBO.create(ADMIN_USER, user);
            });
            assertEquals("User for " + user.getPhone1() + " or " + user.getEmail() + " already exists", ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
 
    @Test
    public void testCreateUserWithMissingPhone() {
        try {
            User user = createNew();
            user.setPhone1(null);
            
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                userBO.create(ADMIN_USER, user);
            });
            assertEquals("Primary mobile number is required.", ex.getMessage());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
 
    @Test
    public void testCreateUserWithMissingEmail() {
        try {
            User user = createNew();
            user.setEmail(null);
            
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                userBO.create(ADMIN_USER, user);
            });
            assertEquals("Email address is required.", ex.getMessage());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
 
    @Test
    public void testCreateUserAsNonAdmin() {
        try {
            AppUser usr = user(UUID.randomUUID().toString(), "Socretes");
            User user = createNew();
            
            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                userBO.create(usr, user);
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
 
    @Test
    public void testCreateUserWithMissingScope() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", UUID.randomUUID().toString());
            map.put("name", "Admin User");
            map.put("priv", "admin");
            map.put("jti", UUID.randomUUID().toString());

            AppUser tmpAdminUser = new AppUserImpl(new UserPrincipal(map));
            User user = createNew();

            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                userBO.create(tmpAdminUser, user);
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testViewAll() {
        try {
            User user_1 = createNew();
            User user_2 = createNew();
            User user_3 = createNew();
            User user_4 = createNew();
            User user_5 = createNew();

            userBO.create(ADMIN_USER, List.of(user_1, user_2, user_3, user_4, user_5));
            
            // Now query.
            List<User> users = userBO.viewAll(ADMIN_USER, new QueryParams());
            assertTrue(users.size() >= 5);
            
            // Query using mobile
            Map<String, List<String>> params = new HashMap<>();
            params.put("phone1", List.of(user_2.getPhone1()));
            users = userBO.viewAll(ADMIN_USER, new QueryParams(params));
            
            assertEquals(1, users.size());
            assertEquals(user_2.getFullName(), users.get(0).getFullName());
            assertEquals(user_2.getPhone1(), users.get(0).getPhone1());
            assertEquals(user_2.getEmail(), users.get(0).getEmail());
            
            params.put("phone1", List.of(user_1.getPhone1(), user_5.getPhone1()));
            users = userBO.viewAll(ADMIN_USER, new QueryParams(params));
            
            assertEquals(2, users.size());
            
            params.put("phone1", List.of(user_1.getPhone1(), user_5.getPhone1()));
            params.put("email", List.of(user_1.getEmail()));
            users = userBO.viewAll(ADMIN_USER, new QueryParams(params));
            
            assertEquals(1, users.size());
            assertEquals(user_1.getFullName(), users.get(0).getFullName());
            
            params.put("phone1", List.of(user_1.getPhone1(), user_5.getPhone1()));
            params.put("email", List.of(user_4.getEmail()));
            users = userBO.viewAll(ADMIN_USER, new QueryParams(params));
            
            assertEquals(0, users.size());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testViewAllWithoutAdmin() {
        try {
            AppUser usr = user(UUID.randomUUID().toString(), "Socretes");
            User user = createNew();
            
            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                userBO.create(usr, user);
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (Exception e) {
            assertTrue(e instanceof IllegalAccessException);
        }
    }
    
    @Test
    public void testView() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // View the user
            AppUser usr = user(user.getExternalId(), user.getFullName());
            
            User current = userBO.view(usr, user.getExternalId());
            assertNotNull(current);
            assertEquals(user.getFullName(), current.getFullName());
            assertEquals(user.getEmail(), current.getEmail());
            assertEquals(user.getPhone1(), current.getPhone1());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testViewOtherUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // View the user
            AppUser usr = user(UUID.randomUUID().toString(), "Random Name");
            
            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                userBO.view(usr, user.getExternalId());
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testModifyUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // Verify the user is created.
            // Prepare the AppUser for this user.
            AppUser usr = user(user.getExternalId(), user.getFullName());
            User current = userBO.view(usr, user.getExternalId());
            assertNotNull(current);
            assertNull(current.getPhone2());
            
            User cloned = user.cloneMe();
            cloned.setFullName("Folks User");
            cloned.setPhone2("9999999999");
            
            userBO.modify(usr, cloned);
            
            User existing = userBO.view(usr, user.getExternalId());
            assertEquals("Folks User", existing.getFullName());
            assertEquals("9999999999", existing.getPhone2());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testModifyOtherUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // Verify the user is created.
            // Prepare the AppUser for this user.
            AppUser usr = user(user.getExternalId(), user.getFullName());
            User current = userBO.view(usr, user.getExternalId());
            assertNotNull(current);
            assertNull(current.getPhone2());
            
            User cloned = user.cloneMe();
            cloned.setFullName("Folks User");
            cloned.setPhone2("9999999999");
            
            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                AppUser usr2 = user(UUID.randomUUID().toString(), user.getFullName());
                userBO.modify(usr2, cloned);
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testRemove() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            // Verify the user is created.
            // Prepare the AppUser for this user.
            AppUser usr = user(user.getExternalId(), user.getFullName());
            userBO.remove(usr, user.getExternalId());
            
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                userBO.view(usr, user.getExternalId());
            });
            assertEquals("No User found for id: " + user.getExternalId(), ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testRemoveNonExistingUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            String invalidExtId = UUID.randomUUID().toString();
            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
                userBO.remove(ADMIN_USER, invalidExtId);
            });
            assertEquals("No User found for id: " + invalidExtId, ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testRemoveOtherUser() {
        try {
            User user = createNew();
            userBO.create(ADMIN_USER, user);
            
            Integer userId = user.getUserId();
            assertNotNull(userId);
            
            User rogueUser = createNew();
            userBO.create(ADMIN_USER, rogueUser);
            
            IllegalAccessException ex = assertThrows(IllegalAccessException.class, () -> {
                AppUser usr2 = user(rogueUser.getExternalId(), user.getFullName());
                userBO.remove(usr2, user.getExternalId());
            });
            assertEquals("Access to this resource is restricted", ex.getMessage());
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }
}
