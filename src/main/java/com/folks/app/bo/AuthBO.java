package com.folks.app.bo;

import com.folks.app.cache.impl.UserRoleCache;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.model.AuthGrant;
import com.folks.app.model.User;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AuthBO extends AbstractBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthBO.class);
    
    private static final ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();
    
    private final UserRoleCache cache = UserRoleCache.getCache();
    
    // Standard OAuth 2.0 Grant Types
    
    // 1. authorization_code: Used for server-side applications (e.g., Node.js, Java) where the client secret can be 
    //                        securely stored on a backend server.
    private final String GRANT_AUTHORIZATION_CODE = "authorization_code";
    
    // 2. client_credentials: Used for machine-to-machine (M2M) communication where an application needs to authenticate
    //                        itself rather than a specific user.
    private final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
    
    // 3. refresh_token: Used to obtain a new access token when the current one expires, without forcing the user to log in again.
    private final String GRANT_REFRESH_TOKEN = "refresh_token";
    
    // 4. password (Resource Owner Password Credentials): Deprecated. Used for legacy apps where the user inputs their
    //              username and password directly into the client app.
    private final String GRANT_PASSWORD = "password";
    
    /**
     * This method will be invoked when node.js server requests for a short-lived token to perform certain admin tasks.
     * 
     * @param credential    The client_id and client_secret.
     * @param grant         The grant client has requested for.
     * @param issuer        The issuer that should issue this token
     * @param audience      The audience this token is meant for, e.g., node.js server (which is acting as a client here)
     * 
     * @return
     * @throws IllegalAccessException 
     */
    public Map<String, Object> authenticate(String credential, AuthGrant grant, String issuer, String audience) throws IllegalAccessException {
        if (credential == null) {
            throw new IllegalAccessException("No Basic Authorization header is present."
                    + " Authentication cannot be performed");
        }
        String[] creds = extract(credential);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting the authentication for {}", Arrays.toString(creds));
        }
        
        User user = cache.get(creds[0]);
        if (user == null) {
            throw new IllegalAccessException("Client_id " + creds[0] + " does not exist");
        }
        if (! user.getPasswordHash().equals(creds[1])) {
            throw new IllegalAccessException("Invalid secret provided for client_id " + creds[0]);
        }
        if (! GRANT_CLIENT_CREDENTIALS.equals(grant.getGrantType())) {
            throw new IllegalAccessException("Unsupported grant_type " + grant.getGrantType());
        }
        
        // Credentials matches. Procedd with token generation ...
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getExternalId());
        claims.put("iss", issuer);
        claims.put("aud", audience);
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("priv", user.getRole().name().toLowerCase());
        
        if (grant.getScope() != null && grant.getScope().trim().length() > 0) {
            claims.put("scope", grant.getScope());
        }
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Authentication successful for client_id {}. Result: {}", creds[0], claims);
        }
        return claims;
        
        // Test code.
        // if (((String)appConfig.get("testing", "user")).equals(creds[0])
        //         && ((String)appConfig.get("testing", "password")).equals(creds[1])) {
        // 
        //     // Add the claims.
        //     Map<String, Object> testClaims = new HashMap<>();
        //     testClaims.put("user", creds[0]);
        //     testClaims.put("name", creds[0]);
        //     testClaims.put("email", creds[0]);
        //     testClaims.put("sub", creds[0]);
        //     
        //     if (LOGGER.isInfoEnabled()) {
        //         LOGGER.info("Authentication successfull for user {}. Result: {}", creds[0], claims);
        //     }
        //     return testClaims;
        // }
        // else {
        //     throw new IllegalAccessException("Invalid login credentials");
        // }
    }
    
    public String[] extract(String credential) {
        String encoded = credential.substring(6);
        String decoded = new String(Base64.getDecoder().decode(encoded));

        String username = decoded.substring(0, decoded.indexOf(":"));
        String password = decoded.substring(decoded.indexOf(":") + 1);

        // Prepare for the call.
        return new String[] {username, password};
    }
}
