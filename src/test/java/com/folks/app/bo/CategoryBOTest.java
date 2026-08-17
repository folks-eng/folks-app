package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Category;
import com.folks.app.util.QueryParams;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
public class CategoryBOTest {
    
    private static CategoryBO categoryBO;
    
    @BeforeAll
    public static void setup() {
        categoryBO = new CategoryBO();
    }
    
    @Test
    @Order(1)
    public void testView() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        AppUser usr = new AppUserImpl(new UserPrincipal(map));

        Category category = categoryBO.view(usr, 1);
        assertNotNull(category);
    }
    
    @Test
    @Order(2)
    public void testViewAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        AppUser usr = new AppUserImpl(new UserPrincipal(map));

        List<Category> categories = categoryBO.viewAll(usr, new QueryParams(new HashMap<>()));
        assertTrue(! categories.isEmpty());
    }
    
    @Test
    @Order(3)
    public void testViewAllHierarchy() {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", UUID.randomUUID().toString());
        map.put("jti", UUID.randomUUID().toString());

        AppUser usr = new AppUserImpl(new UserPrincipal(map));

        List<String> ids = List.of("1", "2", "3", "4", "5");
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", ids);
        
        List<Category> categories = categoryBO.viewAllHierarchy(usr, new QueryParams(params));
        assertTrue(! categories.isEmpty());
        assertEquals(5, categories.size());
    }
}
