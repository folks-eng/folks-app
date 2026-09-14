package com.folks.app.core;

import com.folks.app.cache.impl.UserRoleCache;
import com.folks.app.model.User;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sudip
 */
public class AdminFilter implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminFilter.class);
    
    private final Vertx vertx;
    
    public AdminFilter(Vertx vertx) {
        this.vertx = vertx;
    }
    
    @Override
    public void handle(RoutingContext ctx) {
        // Access to /admin resource is restricted. Therefore check the presence
        // of any token in the cookie/header. For any kind of
        // request, the admin Bearer token must be present in the http header.
        
        // The jwt validation is already done.
        // Now check if the user has the admin privilege.
        io.vertx.ext.auth.User usr = ctx.user();
        if (usr == null) {
            ctx.fail(new IllegalAccessException("Access to this resource is restricted"));
            return;
        }
        String email = usr.principal().getString("email");
        String privilege = usr.principal().getString("priv");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Received admin request from user: {}, privilege: {}", email, privilege);
        }

        // User has admin privilege. Now check the user email.
        try {
            ensureAdmin(usr);
        }
        catch (IllegalAccessException | RuntimeException e) {
            LOGGER.warn("Error verifying admin user", e);
            ctx.fail(e);
            return;
        }
        // All good ! Invoke the next handler/middleware
        ctx.next();
    }
 
    protected void ensureAdmin(io.vertx.ext.auth.User usr) throws IllegalAccessException {
        if (! User.isAdmin(usr.principal().getString("priv"))) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
        if (! UserRoleCache.getCache().contains(usr.principal().getString("sub"))) {
            throw new IllegalAccessException("Access to this resource is restricted");
        }
    }
}
