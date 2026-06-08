package io.opns.app.handler;

import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.javalabs.decl.vertx.container.util.CookieUtil;
import io.opns.app.auth.AuthToken;
import io.opns.app.bo.AuthBO;
import io.vertx.core.Vertx;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.RoutingContext;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class AuthenticationHandler extends AbstractHandler {
    
    private final JWTAuth authZ;
    private final AuthBO authBO;
    
    private final ConfigStorage cs = ConfigStorage.get();
    
    public AuthenticationHandler(Vertx vertx) {
        super(vertx);
        JWTOptions jwtOpts = new JWTOptions()
                .setAlgorithm(cs.jwtAlgo())
                .setIssuer(cs.jwtIssuer())
                .setAudience(Arrays.asList(cs.jwtAudience()))
                .setExpiresInMinutes(cs.jwtExpiry());
        
        KeyStoreOptions keyOpts = new KeyStoreOptions()
                .setPath(cs.keystoreFile())
                .setPassword(cs.keystorePassword());
        
        this.authZ = JWTAuth.create(vertx
                , new JWTAuthOptions()
                        .setJWTOptions(jwtOpts)
                        .setKeyStore(keyOpts));
        
        this.authBO = new AuthBO();
    }
    
    /**
     * API to authenticate a user.
     * @param ctx 
     */
    public void authenticate(RoutingContext ctx) {
        final String audience = ctx.request().headers().get("user-agent");
        final String credential = ctx.request().getHeader("Authorization");
        
        vertx().executeBlocking(() -> {
            // Add the claims.
            Map<String, Object> claims = authBO.authenticate(credential, cs.jwtIssuer(), cs.jwtAudience());
            String jwt = authZ.generateToken(JsonObject.mapFrom(claims));

            AuthToken token = AuthToken.from(claims);
            token.setJwt(jwt);
                
            return token;
            
        }).onComplete(result -> {
            if (result.succeeded()) {
                Cookie cookie = CookieUtil.create((String) ((AuthToken)result.result()).getJwt());
                ctx.response().addCookie(cookie);

                sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }
}