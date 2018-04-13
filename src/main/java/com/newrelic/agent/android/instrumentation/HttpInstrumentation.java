//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.httpclient.ResponseHandlerImpl;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public final class HttpInstrumentation {

    @WrapReturn(
            className = "java/net/URL",
            methodName = "openConnection",
            methodDesc = "()Ljava/net/URLConnection;"
    )
    public static URLConnection openConnection(URLConnection connection) {
        return (URLConnection)(connection instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)connection):(connection instanceof HttpURLConnection?new HttpURLConnectionExtension((HttpURLConnection)connection):connection));
    }

    @WrapReturn(
            className = "java.net.URL",
            methodName = "openConnection",
            methodDesc = "(Ljava/net/Proxy;)Ljava/net/URLConnection;"
    )
    public static URLConnection openConnectionWithProxy(URLConnection connection) {
        return (URLConnection)(connection instanceof HttpsURLConnection?new HttpsURLConnectionExtension((HttpsURLConnection)connection):(connection instanceof HttpURLConnection?new HttpURLConnectionExtension((HttpURLConnection)connection):connection));
    }

    @ReplaceCallSite
    public static HttpResponse execute(HttpClient httpClient, HttpHost target, HttpRequest request, HttpContext context) throws IOException {
        TransactionState transactionState = new TransactionState();

        try {
            return delegate(httpClient.execute(target, delegate(target, request, transactionState), context), transactionState);
        } catch (IOException var6) {
            httpClientError(transactionState, var6);
            throw var6;
        }
    }

    @ReplaceCallSite
    public static <T> T execute(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        TransactionState transactionState = new TransactionState();

        try {
            return httpClient.execute(target, delegate(target, request, transactionState), delegate(responseHandler, transactionState), context);
        } catch (ClientProtocolException var7) {
            httpClientError(transactionState, var7);
            throw var7;
        } catch (IOException var8) {
            httpClientError(transactionState, var8);
            throw var8;
        }
    }

    @ReplaceCallSite
    public static <T> T execute(HttpClient httpClient, HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        TransactionState transactionState = new TransactionState();

        try {
            return httpClient.execute(target, delegate(target, request, transactionState), delegate(responseHandler, transactionState));
        } catch (ClientProtocolException var6) {
            httpClientError(transactionState, var6);
            throw var6;
        } catch (IOException var7) {
            httpClientError(transactionState, var7);
            throw var7;
        }
    }

    @ReplaceCallSite
    public static HttpResponse execute(HttpClient httpClient, HttpHost target, HttpRequest request) throws IOException {
        TransactionState transactionState = new TransactionState();

        try {
            return delegate(httpClient.execute(target, delegate(target, request, transactionState)), transactionState);
        } catch (IOException var5) {
            httpClientError(transactionState, var5);
            throw var5;
        }
    }

    @ReplaceCallSite
    public static HttpResponse execute(HttpClient httpClient, HttpUriRequest request, HttpContext context) throws IOException {
        TransactionState transactionState = new TransactionState();

        try {
            return delegate(httpClient.execute(delegate(request, transactionState), context), transactionState);
        } catch (IOException var5) {
            httpClientError(transactionState, var5);
            throw var5;
        }
    }

    @ReplaceCallSite
    public static <T> T execute(HttpClient httpClient, HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
        TransactionState transactionState = new TransactionState();

        try {
            return httpClient.execute(delegate(request, transactionState), delegate(responseHandler, transactionState), context);
        } catch (ClientProtocolException var6) {
            httpClientError(transactionState, var6);
            throw var6;
        } catch (IOException var7) {
            httpClientError(transactionState, var7);
            throw var7;
        }
    }

    @ReplaceCallSite
    public static <T> T execute(HttpClient httpClient, HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        TransactionState transactionState = new TransactionState();

        try {
            return httpClient.execute(delegate(request, transactionState), delegate(responseHandler, transactionState));
        } catch (ClientProtocolException var5) {
            httpClientError(transactionState, var5);
            throw var5;
        } catch (IOException var6) {
            httpClientError(transactionState, var6);
            throw var6;
        }
    }

    @ReplaceCallSite
    public static HttpResponse execute(HttpClient httpClient, HttpUriRequest request) throws IOException {
        TransactionState transactionState = new TransactionState();

        try {
            return delegate(httpClient.execute(delegate(request, transactionState)), transactionState);
        } catch (IOException var4) {
            httpClientError(transactionState, var4);
            throw var4;
        }
    }

    private static void httpClientError(TransactionState transactionState, Exception e) {
        if(!transactionState.isComplete()) {
            TransactionStateUtil.setErrorCodeFromException(transactionState, e);
            TransactionData transactionData = transactionState.end();
            if(transactionData != null) {
                TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            }
        }

    }

    private static HttpUriRequest delegate(HttpUriRequest request, TransactionState transactionState) {
        return TransactionStateUtil.inspectAndInstrument(transactionState, request);
    }

    private static HttpRequest delegate(HttpHost host, HttpRequest request, TransactionState transactionState) {
        return TransactionStateUtil.inspectAndInstrument(transactionState, host, request);
    }

    private static HttpResponse delegate(HttpResponse response, TransactionState transactionState) {
        return TransactionStateUtil.inspectAndInstrument(transactionState, response);
    }

    private static <T> ResponseHandler<? extends T> delegate(ResponseHandler<? extends T> handler, TransactionState transactionState) {
        return ResponseHandlerImpl.wrap(handler, transactionState);
    }
}
