package com.folks.app.dao;

import com.folks.app.ext.DBExtension;
import com.folks.app.model.User;
import com.folks.app.util.QueryParams;
import com.folks.app.util.SearchCriteria;
import jakarta.persistence.NoResultException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.javalabs.decl.util.DateUtil;
import org.javalabs.jpa.DAOProxy;
import org.javalabs.jpa.JdbcException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAOTest.class);

    private static UserDAO userDAO;
    
    private static Integer userId;
    private static String extId;
    
    @BeforeAll
    public static void init() throws Exception {
        try {
            userDAO = DAOProxy.get(UserDAO.class);
        }
        catch (Exception e) {
            LOGGER.error("Error in UserDAOTest::init()", e);
            throw new RuntimeException(e);
        }
    }
    
    @Test
    @Order(1)
    public void testInsert() {
        try {
            User user = new User();
            user.setExternalId(UUID.randomUUID().toString());
            user.setFullName("Sudiptasish Chanda");
            user.setPhone1("8318990011");
            user.setEmail("zulu@yahoo.co.in");
            user.setRole(User.Role.CUSTOMER);
            user.setStatus(User.Status.ACTIVE);
            user.setCreatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));

            userDAO.insert(user);

            userId = user.getUserId();
            extId = user.getExternalId();
            
            assertNotNull(userId);
            assertNotNull(extId);
        }
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(2)
    public void testFind() {
        try {
            User user = userDAO.find(new User.UserPK(userId));
            assertNotNull(user);
            
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
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(3)
    public void testSelect() {
        try {
            User user = userDAO.select(extId);
            assertNotNull(user);
            
            assertEquals("Sudiptasish Chanda", user.getFullName());
            assertEquals("8318990011", user.getPhone1());
            assertEquals("zulu@yahoo.co.in", user.getEmail());
            assertEquals(User.Role.CUSTOMER, user.getRole());
            assertEquals(User.Status.ACTIVE, user.getStatus());
            
            assertNotNull(user.getCreatedAt());
            assertNull(user.getUpdatedAt());
        }
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(4)
    public void testSelectNotResult() {
        try {
            User user = userDAO.select(UUID.randomUUID().toString());
            fail("Should not find any user");
        }
        catch (NoResultException e) {
            assertTrue(e instanceof NoResultException);
        }
    }
    
    @Test
    @Order(5)
    public void testUpdate() {
        try {
            User user = userDAO.find(new User.UserPK(userId));
            assertNotNull(user);
            
            user.setPhone2("6000000000");
            user.setPasswordHash("sample.password");
            user.setEmail("zulu@yahoo.com");
            user.setUpdatedAt(new Timestamp(DateUtil.currentUTCDate().getTime()));
            
            userDAO.update(user);
            
            // Now select again ...
            user = userDAO.find(new User.UserPK(userId));
            
            assertEquals(extId, user.getExternalId());
            assertEquals("Sudiptasish Chanda", user.getFullName());
            assertEquals("8318990011", user.getPhone1());
            assertEquals("6000000000", user.getPhone2());
            assertEquals("zulu@yahoo.com", user.getEmail());
            assertEquals(User.Role.CUSTOMER, user.getRole());
            assertEquals(User.Status.ACTIVE, user.getStatus());
            
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
        }
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(6)
    public void testQuery() {
        try {
            Map<String, List<String>> params = new HashMap<>();
            
            params.put("phone2", Arrays.asList("6000000000"));
            params.put("email", Arrays.asList("zulu@yahoo.com"));
            params.put("role", Arrays.asList("CUSTOMER"));
            
            SearchCriteria search = SearchCriteria.from(new QueryParams(params));
            List<User> users = userDAO.query(search);
            
            assertTrue(users.size() == 1);
            
            User user = users.get(0);
            assertEquals(extId, user.getExternalId());
            assertEquals("Sudiptasish Chanda", user.getFullName());
            assertEquals("8318990011", user.getPhone1());
            assertEquals("6000000000", user.getPhone2());
            assertEquals("zulu@yahoo.com", user.getEmail());
            assertEquals(User.Role.CUSTOMER, user.getRole());
            assertEquals(User.Status.ACTIVE, user.getStatus());
            
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
            
            // Perform a second search
            params = new HashMap<>();
            
            params.put("phone2", Arrays.asList("6000000001"));
            search = SearchCriteria.from(new QueryParams(params));
            users = userDAO.query(search);
            
            assertTrue(users.isEmpty());
        }
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
    @Test
    @Order(7)
    public void testDelete() {
        try {
            User user = userDAO.find(new User.UserPK(userId));
            assertNotNull(user);
            
            userDAO.delete(user);
            
        }
        catch (JdbcException e) {
            fail(e.getMessage());
        }
    }
    
}
