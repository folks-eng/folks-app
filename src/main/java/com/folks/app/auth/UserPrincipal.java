package com.folks.app.auth;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class UserPrincipal implements Principal {
    
    private final String sub;
    private final String jti;
    private final String name;
    private final String priv;
    private final List<String> scopes;
    
    public UserPrincipal(String sub, String jti, String name, String phone, String email, String priv, List<String> scopes) {
        this.sub = sub;
        this.jti = jti;
        this.name = name;
        this.priv = priv;
        this.scopes = scopes;
    }
    
    public UserPrincipal(Map<String, Object> map) {
        this.sub = (String) map.get("sub");
        this.jti = (String) map.get("jti");
        this.name = (String) map.get("name");
        this.priv = (String) map.get("priv");
        
        String scope = (String) map.get("scope");
        if (scope != null) {
            String[] tmp = scope.split("\\|");
            if (tmp.length > 0) {
                scopes = Arrays.asList(tmp);
            }
            else {
                scopes = Collections.EMPTY_LIST;
            }
        }
        else {
            scopes = Collections.EMPTY_LIST;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String jti() {
        return jti;
    }

    @Override
    public String priv() {
        return priv;
    }

    @Override
    public String sub() {
        return sub;
    }

    @Override
    public List<String> scopes() {
        return scopes;
    }
}
