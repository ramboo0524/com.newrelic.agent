//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp3;

import com.newrelic.agent.android.instrumentation.HttpURLConnectionExtension;
import com.newrelic.agent.android.instrumentation.HttpsURLConnectionExtension;
import com.newrelic.agent.android.instrumentation.ReplaceCallSite;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Request.Builder;
import okhttp3.internal.Internal;
import okhttp3.internal.http.StreamAllocation;

public class OkHttp3Instrumentation {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    private OkHttp3Instrumentation() {
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
    public static okhttp3.Response.Builder body(okhttp3.Response.Builder builder, ResponseBody body) {
        return (new ResponseBuilderExtension(builder)).body(body);
    }

    @ReplaceCallSite
    public static okhttp3.Response.Builder newBuilder(okhttp3.Response.Builder builder) {
        return new ResponseBuilderExtension(builder);
    }

    @ReplaceCallSite(
            isStatic = false,
            scope = "okhttp3.OkUrlFactory"
    )
    public static HttpURLConnection open(OkUrlFactory factory, URL url) {
        HttpURLConnection conn = factory.open(url);
        String protocol = url.getProtocol();
        return (HttpURLConnection)(protocol.equals("http")?new HttpURLConnectionExtension(conn):(protocol.equals("https") && conn instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)conn):new HttpURLConnectionExtension(conn)));
    }

    private static void logReflectionError(String signature) {
        String crlf = System.getProperty("line.separator");
        log.error("Unable to resolve method \"" + signature + "\"." + crlf + "This is usually due to building the app with unsupported OkHttp versions." + crlf + "Check your build configuration for compatibility.");
    }

    public static class OkHttp32 {
        public OkHttp32() {
        }

        @ReplaceCallSite
        public static void callEnqueue(Internal internal, Call call, Callback responseCallback, boolean forWebSocket) {
            try {
                if(call instanceof CallExtension) {
                    call = ((CallExtension)call).getImpl();
                }

                Method callEnqueue = Internal.class.getMethod("callEnqueue", Call.class, Callback.class, Boolean.TYPE);
                if(callEnqueue != null) {
                    callEnqueue.invoke(internal, call, responseCallback, forWebSocket);
                } else {
                    OkHttp3Instrumentation.logReflectionError("callEnqueue(Lokhttp3/Call;Lokhttp3/Callback;Z)V");
                }
            } catch (Exception var5) {
                OkHttp3Instrumentation.log.error(var5.getMessage());
            }

        }

        @ReplaceCallSite
        public static StreamAllocation callEngineGetStreamAllocation(Internal internal, Call call) {
            StreamAllocation streamAllocation = null;

            try {
                if(call instanceof CallExtension) {
                    call = ((CallExtension)call).getImpl();
                }

                Method callEngineGetStreamAllocation = Internal.class.getMethod("callEngineGetStreamAllocation", Call.class);
                if(callEngineGetStreamAllocation != null) {
                    streamAllocation = (StreamAllocation)callEngineGetStreamAllocation.invoke(internal, call);
                } else {
                    OkHttp3Instrumentation.logReflectionError("callEngineGetStreamAllocation(Lokhttp3/Call;)Lokhttp3/internal/http/StreamAllocation;");
                }
            } catch (Exception var4) {
                OkHttp3Instrumentation.log.error(var4.getMessage());
            }

            return streamAllocation;
        }
    }

    public static class OkHttp35 {
        public OkHttp35() {
        }

        @ReplaceCallSite
        public static Call newWebSocketCall(Internal internal, OkHttpClient client, Request request) {
            CallExtension call = null;

            try {
                Method newWebSocketCall = Internal.class.getMethod("newWebSocketCall", OkHttpClient.class, Request.class);
                if(newWebSocketCall != null) {
                    Call impl = (Call)newWebSocketCall.invoke(internal, client, request);
                    call = new CallExtension(client, request, impl);
                } else {
                    OkHttp3Instrumentation.logReflectionError("newWebSocketCall(Lokhttp3/OkHttpClient;Lokhttp3/Request;)Lokhttp3/Call;");
                }
            } catch (Exception var6) {
                OkHttp3Instrumentation.log.error(var6.getMessage());
            }

            return call;
        }
    }

    public static class OkHttp34 {
        public OkHttp34() {
        }

        @ReplaceCallSite
        public static void setCallWebSocket(Internal internal, Call call) {
            try {
                if(call instanceof CallExtension) {
                    call = ((CallExtension)call).getImpl();
                }

                Method setCallWebSocket = Internal.class.getMethod("setCallWebSocket", Call.class);
                if(setCallWebSocket != null) {
                    setCallWebSocket.invoke(internal, call);
                } else {
                    OkHttp3Instrumentation.logReflectionError("setCallWebSocket(Lokhttp3/Call;)V");
                }
            } catch (Exception var3) {
                OkHttp3Instrumentation.log.error(var3.getMessage());
            }

        }

        @ReplaceCallSite
        public static StreamAllocation callEngineGetStreamAllocation(Internal internal, Call call) {
            StreamAllocation streamAllocation = null;

            try {
                if(call instanceof CallExtension) {
                    call = ((CallExtension)call).getImpl();
                }

                Method callEngineGetStreamAllocation = Internal.class.getMethod("callEngineGetStreamAllocation", Call.class);
                if(callEngineGetStreamAllocation != null) {
                    streamAllocation = (StreamAllocation)callEngineGetStreamAllocation.invoke(internal, call);
                } else {
                    OkHttp3Instrumentation.logReflectionError("callEngineGetStreamAllocation(Lokhttp3/Call;)Lokhttp3/internal/connection/StreamAllocation;");
                }
            } catch (Exception var4) {
                OkHttp3Instrumentation.log.error(var4.getMessage());
            }

            return streamAllocation;
        }
    }
}
