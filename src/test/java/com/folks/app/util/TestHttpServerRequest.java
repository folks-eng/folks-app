package com.folks.app.util;

import io.netty.handler.codec.DecoderResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.net.HostAndPort;
import io.vertx.core.net.NetSocket;

import java.util.Set;

/**
 *
 * @author schan280
 */
public class TestHttpServerRequest implements HttpServerRequest {
    
    private final String host;
    private final String path;
    private final HttpMethod method;
    private final MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    private final MultiMap params = MultiMap.caseInsensitiveMultiMap();
    
    private Boolean ended = Boolean.FALSE;
    private Boolean paused = Boolean.FALSE;
    
    private Handler<Throwable> exHandler;
    private Handler<Buffer> handler;
    
    public TestHttpServerRequest(String path, HttpMethod method) {
        this(path, method, "localhost");
    }

    public TestHttpServerRequest(String path, HttpMethod method, String host) {
        this.host = host;
        this.path = path;
        this.method = method;
    }

    @Override
    public HttpServerRequest exceptionHandler(Handler<Throwable> handler) {
        this.exHandler = handler;
        return this;
    }

    @Override
    public HttpServerRequest handler(Handler<Buffer> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public HttpServerRequest pause() {
        paused = Boolean.TRUE;
        return this;
    }

    @Override
    public HttpServerRequest resume() {
        paused = Boolean.FALSE;
        return this;
    }

    @Override
    public HttpServerRequest fetch(long amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpServerRequest endHandler(Handler<Void> endHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpVersion version() {
        return HttpVersion.HTTP_2;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public String scheme() {
        return "http";
    }

    @Override
    public String uri() {
        return path;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String query() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HostAndPort authority() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long bytesRead() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpServerResponse response() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MultiMap headers() {
        return headers;
    }

    @Override
    public HttpServerRequest setParamsCharset(String charset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getParamsCharset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MultiMap params() {
        return params;
    }

    @Override
    public String absoluteURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Future<Buffer> body() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Future<Void> end() {
        ended = Boolean.TRUE;
        return null;
    }

    @Override
    public Future<NetSocket> toNetSocket() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpServerRequest setExpectMultipart(boolean expect) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isExpectMultipart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpServerRequest uploadHandler(Handler<HttpServerFileUpload> uploadHandler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MultiMap formAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getFormAttribute(String attributeName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Future<ServerWebSocket> toWebSocket() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEnded() {
        return ended;
    }

    @Override
    public HttpServerRequest customFrameHandler(Handler<HttpFrame> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpConnection connection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HttpServerRequest streamPriorityHandler(Handler<StreamPriority> handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DecoderResult decoderResult() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Cookie getCookie(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Cookie getCookie(String name, String domain, String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Cookie> cookies(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Cookie> cookies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MultiMap params(boolean semicolonIsNormalChar) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public HostAndPort authority(boolean real) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
