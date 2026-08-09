package com.folks.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author schan280
 */
public class AuthGrant {

    @JsonProperty("grant_type")
    private String grantType;
    
    private String scope;

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
