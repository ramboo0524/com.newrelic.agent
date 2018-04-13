//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

public class OkHttpInstrumentation {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    @WrapReturn(
            className = "com/squareup/okhttp/OkHttpClient",
            methodName = "open",
            methodDesc = "(Ljava/net/URL;)Ljava/net/HttpURLConnection;"
    )
    public static HttpURLConnection open(HttpURLConnection connection) {
        return (HttpURLConnection)(connection instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)connection):(connection != null?new HttpURLConnectionExtension(connection):null));
    }

    @WrapReturn(
            className = "com/squareup/okhttp/OkHttpClient",
            methodName = "open",
            methodDesc = "(Ljava/net/URL;Ljava/net/Proxy)Ljava/net/HttpURLConnection;"
    )
    public static HttpURLConnection openWithProxy(HttpURLConnection connection) {
        return (HttpURLConnection)(connection instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)connection):(connection != null?new HttpURLConnectionExtension(connection):null));
    }

    @WrapReturn(
            className = "com/squareup/okhttp/OkUrlFactory",
            methodName = "open",
            methodDesc = "(Ljava/net/URL;)Ljava/net/HttpURLConnection;"
    )
    public static HttpURLConnection urlFactoryOpen(HttpURLConnection connection) {
        log.debug("OkHttpInstrumentation - wrapping return of call to OkUrlFactory.open...");
        return (HttpURLConnection)(connection instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)connection):(connection != null?new HttpURLConnectionExtension(connection):null));
    }
}
