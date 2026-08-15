package com.folks.app.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.HttpException;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author schan280
 */
public class TestRoutingContext implements RoutingContext {
    
    private final HttpServerRequest request;
    private final HttpServerResponse response;
    private Throwable failure;
    private final Map<String, Object> data = new HashMap<>();
    
    private final Map<String, String> pathParams = new HashMap<>();
    private User user;
    
    public TestRoutingContext(HttpServerRequest request, HttpServerResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpServerRequest request() {
        return this.request;
    }

    @Override
    public HttpServerResponse response() {
        return this.response;
    }

    @Override
    public void next() {
        // Do Nothing.
    }

    @Override
    public void fail(int statusCode) {
        response.setStatusCode(statusCode);
        doFail();
    }

    @Override
    public void fail(Throwable t) {
        if (t instanceof HttpException) {
            this.fail(((HttpException) t).getStatusCode(), t);
        } else {
            this.fail(500, t);
        }
    }

    @Override
    public void fail(int statusCode, Throwable throwable) {
        response.setStatusCode(statusCode);
        response.setStatusMessage(throwable.getMessage());
        this.failure = throwable == null ? new NullPointerException() : throwable;
        
        doFail();
    }
    
    private void doFail() {
        // TODO (schan280)
    }

    @Override
    public RoutingContext put(String key, Object obj) {
        data.put(key, obj);
        return this;
    }

    @Override
    public <T> T get(String key) {
        return (T)data.get(key);
    }

    @Override
    public <T> T get(String key, T defaultValue) {
        return (T)data.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T remove(String key) {
        return (T)data.remove(key);
    }

    @Override
    public Map<String, Object> data() {
        return data;
    }

    @Override
    public Vertx vertx() {
        return null;
    }

    @Override
    public String mountPoint() {
        return "/";
    }

    @Override
    public Route currentRoute() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String normalizedPath() {
        return request.path();
    }

    @Override
    public RequestBody body() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FileUpload> fileUploads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cancelAndCleanupFileUploads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Session session() {
        return null;
    }

    @Override
    public boolean isSessionAccessed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public User user() {
        return this.user;
    }

    @Override
    public Throwable failure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int statusCode() {
        return response.getStatusCode();
    }

    @Override
    public String getAcceptableContentType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ParsedHeaderValues parsedHeaders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int addHeadersEndHandler(Handler<Void> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeHeadersEndHandler(int handlerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int addBodyEndHandler(Handler<Void> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeBodyEndHandler(int handlerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int addEndHandler(Handler<AsyncResult<Void>> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeEndHandler(int handlerID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean failed() {
        return failure != null;
    }

    @Override
    public void setAcceptableContentType(String contentType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reroute(HttpMethod method, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> pathParams() {
        return pathParams;
    }

    @Override
    public String pathParam(String name) {
        return pathParams.get(name);
    }

    @Override
    public MultiMap queryParams() {
        return request.params();
    }

    @Override
    public MultiMap queryParams(Charset encoding) {
        return request.params();
    }

    @Override
    public List<String> queryParam(String name) {
        return request.params().getAll(name);
    }

    @Override
    public UserContext userContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
