package com.folks.app.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * The standard auth response.
 * 
 * For the OAuth 2.0 Client Credentials flow, the standard response from the token endpoint is defined by RFC 6749 Section 5.1.
 * 
 *   {
 *     "access_token": "eyJhbGciOiJSUzI1NiIsInR...",
 *     "token_type": "Bearer",
 *     "expires_in": 3600,
 *     "scope": "customer:create"
 *   }
 * 
 * Standard attributes:
 * 
 *  ---------------- ------------------ -------------------------------------------------------------
 * |  Attribute	    |     Required     |	                  Description                        |
 *  ---------------- ------------------ -------------------------------------------------------------
 * | access_token   |       Yes        |       The OAuth access token (often a JWT)                  |
 *  ---------------- ------------------ -------------------------------------------------------------
 * |  token_type    |	    Yes	       |        Usually "Bearer"                                     |
 *  ---------------- ------------------ -------------------------------------------------------------
 * |  expires_in    |     Recommended  |        Lifetime in seconds                                  |
 *  ---------------- ------------------ -------------------------------------------------------------
 * |    scope	    |     Optional     |       Granted scope(s). May be omitted if same as requested.|
 *  ---------------- ------------------ -------------------------------------------------------------
 * | refresh_token  |     Usually No   |  Not normally returned for Client Credentials flow          |
 *  ----------------------------------- -------------------------------------------------------------
 *
 * @author schan280
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthToken {
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("scope")
    private String scope;
    
    @JsonProperty("expires_in")
    private Long expiresIn;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    public AuthToken() {}
    
    public static AuthToken from(Map<String, Object> claims, Integer expiryInMin) {
        AuthToken token = new AuthToken();
        token.setTokenType("Bearer");
        token.setExpiresIn(expiryInMin * 60L);
        
        if (claims.containsKey("scope")) {
            token.setScope((String)claims.get("scope"));
            // token.setExpiresIn(1800L);     // For scope token, the expiry os 30 minute.
        }
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
