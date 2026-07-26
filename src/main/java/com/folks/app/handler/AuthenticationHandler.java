package com.folks.app.handler;

import com.folks.app.bo.SignupBO;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.model.SignupInfo;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.internal.ConfigStorage;
import org.javalabs.decl.vertx.container.util.CookieUtil;
import com.folks.app.auth.AuthToken;
import com.folks.app.bo.AuthBO;
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
    private final SignupBO signupBO;
    
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

        Map<String, Object> props = ApplicationConfiguration.getInstance().get("redis.config");
        //System.out.println("URL : " + propVal.getClass().getName() + ":" +propVal.toString());
        Redis redis = Redis.createClient(vertx, props.get("url").toString());  //"redis://localhost:6379"
        RedisAPI redisAPI = RedisAPI.api(redis);
        
        this.authBO = new AuthBO();
        this.signupBO = new SignupBO(redisAPI);
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

    public void generateSignupInfo(RoutingContext ctx) {

        /* Lambda that takes a future and contains code that will be executed on a worker thread in the background.
        You have to resolve the Future to trigger the handler. Any exceptions that happen in this first block, will
        automatically trigger future.fail()
         */
        System.out.println("Start of Generate OTP");
        //deserialize
        SignupInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), SignupInfo.class);

        if(!regInfo.getMobileNum().trim().isEmpty()) {
            signupBO.genOtp(user(ctx), regInfo)
                    .onSuccess(smsResponse -> {
                        //System.out.println(" OTP generated and sent");
                        sendResponse(ctx, HttpURLConnection.HTTP_OK, smsResponse);
                        //System.out.println(smsResponse.getHttpStatusCode() + " smsResponse " + smsResponse.getErrorCode());
                    })
                    .onFailure(err -> {
                        System.err.println("Registration failed: " + err.getMessage());
                        ctx.fail(err);
                    });
        }
    }

    public void verifySignupInfo(RoutingContext ctx) {
        System.out.println("Start of verify OTP");
        SignupInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), SignupInfo.class);
        // First fetch the entry, to see if this already exists.
        signupBO.verifyUser(user(ctx), regInfo)
                .onSuccess(msg -> {
                    sendResponse(ctx, msg.getCode(), msg.getMessage());
                    // Proceed to register/activate user
                }).onFailure(msg -> {
                    System.err.println("Verification error: " + msg.getMessage());
                    ctx.fail(msg.getCause());
                });
    }
}