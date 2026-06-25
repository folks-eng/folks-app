package com.folks.app.auth;

import java.util.Map;

/**
 *
 * @author schan280
 */
public class AuthToken {
    
    private String user;
    private String email;
    private String jwt;
    private Long validityHr;
    
    public AuthToken() {}
    
    public static AuthToken from(Map<String, Object> claims) {
        AuthToken token = new AuthToken();
        token.setUser((String)claims.get("user"));
        token.setEmail((String)claims.get("email"));
        token.setValidityHr(1L);
        
        return token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getValidityHr() {
        return validityHr;
    }

    public void setValidityHr(Long validityHr) {
        this.validityHr = validityHr;
    }
}
