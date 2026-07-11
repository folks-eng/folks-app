package com.folks.app.handler;

import com.folks.app.bo.RegistrationBO;
import com.folks.app.config.ApplicationConfiguration;
import com.folks.app.model.RegistrationInfo;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import org.javalabs.decl.util.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>
 * This handler class is designed to handle asynchronous events, such as incoming network requests,
 *
 * accordingly without blocking the main event loop, making your application highly scalable and reactive.
 * 
 * <p>
 * Refer to the <code>routing-config.xml</code> to understand the url mapping.
 */
public class RegistrationHandler extends AbstractHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationHandler.class);

    private final RegistrationBO regBO;

    public RegistrationHandler(Vertx vertx) {
        super(vertx);

        Map<String, Object> props = ApplicationConfiguration.getInstance().get("redis.config");
        //Object propVal = props.get("url");
        //System.out.println("URL : " + propVal.getClass().getName() + ":" +propVal.toString());
        Redis redis = Redis.createClient(vertx, props.get("url").toString()); //"redis://localhost:6379"
        RedisAPI redisAPI = RedisAPI.api(redis);
        this.regBO = new RegistrationBO(redisAPI);
    }

    public void registerUser(RoutingContext ctx) {

        /* Lambda that takes a future and contains code that will be executed on a worker thread in the background.
        You have to resolve the Future to trigger the handler. Any exceptions that happen in this first block, will
        automatically trigger future.fail()
         */
        System.out.println("Start of register user");
        RegistrationInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), RegistrationInfo.class);
        regBO.registerUser(user(ctx), regInfo)
            .onSuccess(newRegInfo -> {
                System.out.println(" User registered and OTP sent: " + newRegInfo.getOtp());
                sendResponse(ctx, HttpURLConnection.HTTP_OK, regInfo);
            })
            .onFailure(err -> {
                System.err.println("Registration failed: " + err.getMessage());
                ctx.fail(err);
            });
        //System.out.println("regInfo " +regInfo.toString());
    }

    public void verifyUser(RoutingContext ctx) {
        System.out.println("Start of verify user");
        RegistrationInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), RegistrationInfo.class);
            // First fetch the entry, to see if this already exists.
        regBO.verifyUser(user(ctx), regInfo)
                .onSuccess(isValid -> {
                if(isValid) {
                    System.out.println("OTP verified successfully!");
                    sendResponse(ctx, HttpURLConnection.HTTP_OK, "User Verified");
                    // Proceed to register/activate user
                } else {
                    sendResponse(ctx, HttpURLConnection.HTTP_UNAUTHORIZED, "User verification failed : Invalid or expired OTP");
                }
        }).onFailure(err -> {
            System.err.println("Verification error: " + err.getMessage());
            ctx.fail(err);
        });
    }
}
