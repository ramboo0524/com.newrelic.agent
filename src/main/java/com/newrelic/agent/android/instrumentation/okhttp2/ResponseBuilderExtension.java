//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp2;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.squareup.okhttp.Handshake;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.Response.Builder;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;

public class ResponseBuilderExtension extends Builder {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private Builder impl;

    public ResponseBuilderExtension(Builder impl) {
        this.impl = impl;
    }

    public Builder request(Request request) {
        return this.impl.request(request);
    }

    public Builder protocol(Protocol protocol) {
        return this.impl.protocol(protocol);
    }

    public Builder code(int code) {
        return this.impl.code(code);
    }

    public Builder message(String message) {
        return this.impl.message(message);
    }

    public Builder handshake(Handshake handshake) {
        return this.impl.handshake(handshake);
    }

    public Builder header(String name, String value) {
        return this.impl.header(name, value);
    }

    public Builder addHeader(String name, String value) {
        return this.impl.addHeader(name, value);
    }

    public Builder removeHeader(String name) {
        return this.impl.removeHeader(name);
    }

    public Builder headers(Headers headers) {
        return this.impl.headers(headers);
    }

    public Builder body(ResponseBody body) {
        try {
            if(body != null) {
                BufferedSource source = body.source();
//                int length = false;
                if(source != null) {
                    Buffer buffer = new Buffer();
                    source.readAll(buffer);
                    return this.impl.body(new PrebufferedResponseBody(body, buffer));
                }
            }
        } catch (IOException var5) {
            log.error("IOException reading from source: ", var5);
        } catch (IllegalStateException var6) {
            ;
        }

        return this.impl.body(body);
    }

    public Builder networkResponse(Response networkResponse) {
        return this.impl.networkResponse(networkResponse);
    }

    public Builder cacheResponse(Response cacheResponse) {
        return this.impl.cacheResponse(cacheResponse);
    }

    public Builder priorResponse(Response priorResponse) {
        return this.impl.priorResponse(priorResponse);
    }

    public Response build() {
        return this.impl.build();
    }
}
