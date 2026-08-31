package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.Service;
import com.folks.app.util.QueryParams;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author schan280
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceBOTest {
    
    private static ServiceBO serviceBO;
    
    @BeforeAll
    public static void setup() {
        serviceBO = new ServiceBO();
    }
    
    @Test
    //@Order(1)
    public void testViewByCat() {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("sub", UUID.randomUUID().toString());
            map.put("jti", UUID.randomUUID().toString());

            AppUser usr = new AppUserImpl(new UserPrincipal(map));

            Map<String, List<String>> param = new HashMap<>();
            param.put("categoryId", Arrays.asList("107"));
            
            List<Service> services = serviceBO.viewAll(usr, new QueryParams(param));

            assertTrue(services.isEmpty());
        }
        catch (Exception e) {
            fail(e);
        }
    }

}
