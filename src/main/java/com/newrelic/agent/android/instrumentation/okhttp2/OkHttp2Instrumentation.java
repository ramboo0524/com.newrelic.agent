//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp2;

import com.newrelic.agent.android.instrumentation.HttpURLConnectionExtension;
import com.newrelic.agent.android.instrumentation.HttpsURLConnectionExtension;
import com.newrelic.agent.android.instrumentation.ReplaceCallSite;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.Request.Builder;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class OkHttp2Instrumentation {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    private OkHttp2Instrumentation() {
    }

    @ReplaceCallSite
    public static Request build(Builder builder) {
        return (new RequestBuilderExtension(builder)).build();
    }

    @ReplaceCallSite
    public static Call newCall(OkHttpClient client, Request request) {
        return new CallExtension(client, request, client.newCall(request));
    }

    @ReplaceCallSite
    public static com.squareup.okhttp.Response.Builder body(com.squareup.okhttp.Response.Builder builder, ResponseBody body) {
        return (new ResponseBuilderExtension(builder)).body(body);
    }

    @ReplaceCallSite
    public static com.squareup.okhttp.Response.Builder newBuilder(com.squareup.okhttp.Response.Builder builder) {
        return new ResponseBuilderExtension(builder);
    }

    @ReplaceCallSite(
            isStatic = false,
            scope = "com.squareup.okhttp.OkUrlFactory"
    )
    public static HttpURLConnection open(OkUrlFactory factory, URL url) {
        HttpURLConnection conn = factory.open(url);
        String protocol = url.getProtocol();
        return (HttpURLConnection)(protocol.equals("http")?new HttpURLConnectionExtension(conn):(protocol.equals("https") && conn instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)conn):new HttpURLConnectionExtension(conn)));
    }
}
