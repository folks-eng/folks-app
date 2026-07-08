package com.folks.app.handler;

import com.folks.app.bo.RegistrationBO;
import com.folks.app.model.RegistrationInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import org.javalabs.decl.util.MapperUtil;
import org.javalabs.decl.vertx.config.model.ServerMessage;

import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

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

    private final RegistrationBO regBO;

    public RegistrationHandler(Vertx vertx) {
        super(vertx);
        this.regBO = new RegistrationBO();
    }

    public void registerUser(RoutingContext ctx) {

        /* Lambda that takes a future and contains code that will be executed on a worker thread in the background.
        You have to resolve the Future to trigger the handler. Any exceptions that happen in this first block, will
        automatically trigger future.fail()
         */
        System.out.println("Start of register user");
        vertx().executeBlocking(new Callable<RegistrationInfo>(){
            @Override
            public RegistrationInfo call() throws Exception {
                RegistrationInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), RegistrationInfo.class);
                regInfo = regBO.registerUser(user(ctx), regInfo);
                System.out.println("regInfo " +regInfo.toString());
                return regInfo;
            }
        }).onComplete(new Handler<AsyncResult<RegistrationInfo>>() {
            @Override
            public void handle(AsyncResult<RegistrationInfo> result) {
                if (result.succeeded()) {
                    sendResponse(ctx, HttpURLConnection.HTTP_CREATED, result.result());
                } else {
                    ctx.fail(result.cause());
                }
            }
        });
    }

    public void verifyUser(RoutingContext ctx) {
        System.out.println("Start of verify user");
        vertx().executeBlocking(() -> {
            RegistrationInfo regInfo = MapperUtil.decode(ctx.body().buffer().getBytes(), RegistrationInfo.class);

            // First fetch the entry, to see if this already exists.
            boolean matched = regBO.verifyUser(user(ctx), regInfo);
            System.out.println("Verified " + matched);
            ServerMessage msg = new ServerMessage();
            if(matched) {
                msg.setCode(HttpURLConnection.HTTP_OK);
                msg.setMessage("User verified successfully");
            }
            else {
                msg.setCode(HttpURLConnection.HTTP_OK);
                msg.setMessage("User verification failed");
            }
            return msg;

        }).onComplete(result -> {
            if (result.succeeded()) {
                sendResponse(ctx, HttpURLConnection.HTTP_OK, result.result());
            }
            else {
                ctx.fail(result.cause());
            }
        });
    }

}
