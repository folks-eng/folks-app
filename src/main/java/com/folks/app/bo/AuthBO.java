package com.folks.app.bo;

import com.folks.app.config.ApplicationConfiguration;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author schan280
 */
public class AuthBO {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthBO.class);
    
    private static final ApplicationConfiguration appConfig = ApplicationConfiguration.getInstance();
    
    public Map<String, Object> authenticate(String credential, String issuer, String audience) throws IllegalAccessException {
        if (credential == null) {
            throw new RuntimeException("No Basic Authorization header is present."
                    + " Authentication cannot be performed");
        }
        String[] creds = extract(credential);
        if (((String)appConfig.get("testing", "user")).equals(creds[0])
                && ((String)appConfig.get("testing", "password")).equals(creds[1])) {

            // Add the claims.
            Map<String, Object> claims = new HashMap<>();
            claims.put("user", creds[0]);
            claims.put("name", creds[0]);
            claims.put("email", creds[0]);
            claims.put("sub", Arrays.asList(creds[0]));
            
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Authentication successfull for user {}. Result: {}", creds[0], claims);
            }
            return claims;
        }
        else {
            throw new IllegalAccessException("Invalid login credentials");
        }
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
