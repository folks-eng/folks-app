package com.folks.app.bo;

import com.folks.app.auth.AppUser;
import com.folks.app.auth.AppUserImpl;
import com.folks.app.auth.UserPrincipal;
import com.folks.app.cache.impl.UserRoleCache;
import com.folks.app.ext.DBExtension;
import com.folks.app.model.AuthGrant;
import com.folks.app.model.User;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit test cases for {@link AuthBO}.
 *
 * <p>Covers three independent code paths:
 * <ul>
 *     <li>{@link AuthBO#extract(String)} - pure Basic-Auth header decoding, no DB involved.</li>
 *     <li>{@link AuthBO#authenticate(String, String, String, String, String)} - looks a user up
 *         by phone/email; needs a real {@code User} row created via {@link UserBO}.</li>
 *     <li>{@link AuthBO#authenticate(String, AuthGrant, String, String)} - the
 *         {@code client_credentials} flow, which is served entirely from the in-memory
 *         {@link UserRoleCache} (no DB fallback), so the test pre-populates the cache itself.</li>
 * </ul>
 */
@ExtendWith({DBExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthBOTest {

    private static AuthBO authBO;

    private static User phoneUser;
    private static final String PHONE = "9812345101";

    @BeforeAll
    public static void setup() {
        authBO = new AuthBO();

        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("sub", UUID.randomUUID().toString());
        adminMap.put("name", "Admin User");
        adminMap.put("priv", "admin");
        adminMap.put("scope", "user:create|user:query");
        adminMap.put("jti", UUID.randomUUID().toString());
        AppUser adminUser = new AppUserImpl(new UserPrincipal(adminMap));

        phoneUser = new User();
        phoneUser.setFullName("Auth Test User");
        phoneUser.setPhone1(PHONE);
        phoneUser.setEmail("auth.test.user@folks.test");

        try {
            new UserBO().create(adminUser, phoneUser);
        }
        catch (IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    private static String basicHeader(String username, String password) {
        String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return "Basic " + encoded;
    }

    @Test
    @Order(1)
    public void testExtract() {
        String[] creds = authBO.extract(basicHeader("client123", "secret456"));

        assertEquals("client123", creds[0]);
        assertEquals("secret456", creds[1]);
    }

    @Test
    @Order(2)
    public void testAuthenticateByPhone() throws IllegalAccessException {
        Map<String, Object> claims = authBO.authenticate("n/a", "phone1", PHONE, "folks-issuer", "folks-app");

        assertEquals(phoneUser.getExternalId(), claims.get("sub"));
        assertEquals("folks-issuer", claims.get("iss"));
        assertEquals("folks-app", claims.get("aud"));
        assertNotNull(claims.get("jti"));
    }

    @Test
    @Order(3)
    public void testAuthenticateByPhoneNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> authBO.authenticate("n/a", "phone1", "0000000000", "folks-issuer", "folks-app"));
    }

    @Test
    @Order(4)
    public void testAuthenticateMissingInput() {
        assertThrows(IllegalAccessException.class,
                () -> authBO.authenticate("n/a", "phone1", null, "folks-issuer", "folks-app"));
    }

    @Test
    @Order(5)
    public void testAuthenticateClientCredentials() throws IllegalAccessException {
        String clientId = "test-client-" + UUID.randomUUID();
        String clientSecret = "s3cr3t";

        User clientUser = new User();
        clientUser.setExternalId(UUID.randomUUID().toString());
        clientUser.setRole(User.Role.ADMIN);
        clientUser.setPasswordHash(clientSecret);

        UserRoleCache.getCache().add(clientId, clientUser);

        AuthGrant grant = new AuthGrant();
        grant.setGrantType("client_credentials");
        grant.setScope("booking:read booking:write");

        Map<String, Object> claims = authBO.authenticate(
                basicHeader(clientId, clientSecret), grant, "folks-issuer", "folks-node");

        assertEquals(clientUser.getExternalId(), claims.get("sub"));
        assertEquals("folks-issuer", claims.get("iss"));
        assertEquals("folks-node", claims.get("aud"));
        assertEquals("admin", claims.get("priv"));
        assertEquals("booking:read booking:write", claims.get("scope"));
        assertNotNull(claims.get("jti"));
    }

    @Test
    @Order(6)
    public void testAuthenticateClientCredentialsMissingHeader() {
        AuthGrant grant = new AuthGrant();
        grant.setGrantType("client_credentials");

        assertThrows(IllegalAccessException.class,
                () -> authBO.authenticate(null, grant, "folks-issuer", "folks-node"));
    }

    @Test
    @Order(7)
    public void testAuthenticateClientCredentialsUnknownClient() {
        AuthGrant grant = new AuthGrant();
        grant.setGrantType("client_credentials");

        assertThrows(IllegalAccessException.class,
                () -> authBO.authenticate(basicHeader("no-such-client", "whatever"), grant, "folks-issuer", "folks-node"));
    }

    @Test
    @Order(8)
    public void testAuthenticateClientCredentialsWrongSecret() {
        String clientId = "test-client-" + UUID.randomUUID();

        User clientUser = new User();
        clientUser.setExternalId(UUID.randomUUID().toString());
        clientUser.setRole(User.Role.ADMIN);
        clientUser.setPasswordHash("correct-secret");
        UserRoleCache.getCache().add(clientId, clientUser);

        AuthGrant grant = new AuthGrant();
        grant.setGrantType("client_credentials");

        assertThrows(IllegalAccessException.class,
                () -> authBO.authenticate(basicHeader(clientId, "wrong-secret"), grant, "folks-issuer", "folks-node"));
    }

    @Test
    @Order(9)
    public void testAuthenticateUnsupportedGrantType() {
        String clientId = "test-client-" + UUID.randomUUID();
        String clientSecret = "s3cr3t";

        User clientUser = new User();
        clientUser.setExternalId(UUID.randomUUID().toString());
        clientUser.setRole(User.Role.ADMIN);
        clientUser.setPasswordHash(clientSecret);
        UserRoleCache.getCache().add(clientId, clientUser);

        AuthGrant grant = new AuthGrant();
        grant.setGrantType("authorization_code");

        assertThrows(IllegalAccessException.class,
                () -> authBO.authenticate(basicHeader(clientId, clientSecret), grant, "folks-issuer", "folks-node"));
    }
}
