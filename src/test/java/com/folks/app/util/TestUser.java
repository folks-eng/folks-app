package com.folks.app.util;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

/**
 *
 * @author schan280
 */
public class TestUser implements User {

    @Override
    public JsonObject attributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JsonObject principal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User merge(User other) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
