package io.opns.app.auth;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class UserPrincipal implements Principal {
    
    private final String id;
    private final String name;
    private final String email;
    private final String priv;
    private final List<String> sub;

    public UserPrincipal(String id, String name, String email, String priv, List<String> sub) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.priv = priv;
        this.sub = Collections.unmodifiableList(sub);
    }
    
    public UserPrincipal(Map<String, Object> map) {
        this.id = (String) map.get("user");
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
        this.priv = (String) map.get("priv");
        this.sub = Collections.unmodifiableList((List) map.get("sub"));
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String priv() {
        return priv;
    }

    @Override
    public List<String> sub() {
        return sub;
    }
}
