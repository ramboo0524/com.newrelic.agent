//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.Permission;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpURLConnectionExtension extends HttpURLConnection {
    private HttpURLConnection impl;
    private TransactionState transactionState;
    private static final AgentLog log = AgentLogManager.getAgentLog();

    public HttpURLConnectionExtension(HttpURLConnection impl) {
        super(impl.getURL());
        this.impl = impl;
        TransactionStateUtil.setCrossProcessHeader(impl);
    }

    public void addRequestProperty(String field, String newValue) {
        this.impl.addRequestProperty(field, newValue);
    }

    public void disconnect() {
        if(this.transactionState != null && !this.transactionState.isComplete()) {
            this.addTransactionAndErrorData(this.transactionState);
        }

        this.impl.disconnect();
    }

    public boolean usingProxy() {
        return this.impl.usingProxy();
    }

    public void connect() throws IOException {
        this.getTransactionState();

        try {
            this.impl.connect();
        } catch (IOException var2) {
            this.error(var2);
            throw var2;
        }
    }

    public boolean getAllowUserInteraction() {
        return this.impl.getAllowUserInteraction();
    }

    public int getConnectTimeout() {
        return this.impl.getConnectTimeout();
    }

    public Object getContent() throws IOException {
        this.getTransactionState();

        Object object;
        try {
            object = this.impl.getContent();
        } catch (IOException var4) {
            this.error(var4);
            throw var4;
        }

        int contentLength = this.impl.getContentLength();
        if(contentLength >= 0) {
            TransactionState transactionState = this.getTransactionState();
            if(!transactionState.isComplete()) {
                transactionState.setBytesReceived((long)contentLength);
                this.addTransactionAndErrorData(transactionState);
            }
        }

        return object;
    }

    public Object getContent(Class[] types) throws IOException {
        this.getTransactionState();

        Object object;
        try {
            object = this.impl.getContent(types);
        } catch (IOException var4) {
            this.error(var4);
            throw var4;
        }

        this.checkResponse();
        return object;
    }

    public String getContentEncoding() {
        this.getTransactionState();
        String contentEncoding = this.impl.getContentEncoding();
        this.checkResponse();
        return contentEncoding;
    }

    public int getContentLength() {
        this.getTransactionState();
        int contentLength = this.impl.getContentLength();
        this.checkResponse();
        return contentLength;
    }

    public String getContentType() {
        this.getTransactionState();
        String contentType = this.impl.getContentType();
        this.checkResponse();
        return contentType;
    }

    public long getDate() {
        this.getTransactionState();
        long date = this.impl.getDate();
        this.checkResponse();
        return date;
    }

    public InputStream getErrorStream() {
        this.getTransactionState();

        try {
            CountingInputStream in = new CountingInputStream(this.impl.getErrorStream(), true);
            return in;
        } catch (Exception var3) {
            log.error("HttpURLConnectionExtension: " + var3.toString());
            return this.impl.getErrorStream();
        }
    }

    public long getHeaderFieldDate(String field, long defaultValue) {
        this.getTransactionState();
        long date = this.impl.getHeaderFieldDate(field, defaultValue);
        this.checkResponse();
        return date;
    }

    public boolean getInstanceFollowRedirects() {
        return this.impl.getInstanceFollowRedirects();
    }

    public Permission getPermission() throws IOException {
        return this.impl.getPermission();
    }

    public String getRequestMethod() {
        return this.impl.getRequestMethod();
    }

    public int getResponseCode() throws IOException {
        this.getTransactionState();

        int responseCode;
        try {
            responseCode = this.impl.getResponseCode();
        } catch (IOException var3) {
            this.error(var3);
            throw var3;
        }

        this.checkResponse();
        return responseCode;
    }

    public String getResponseMessage() throws IOException {
        this.getTransactionState();

        String message;
        try {
            message = this.impl.getResponseMessage();
        } catch (IOException var3) {
            this.error(var3);
            throw var3;
        }

        this.checkResponse();
        return message;
    }

    public void setChunkedStreamingMode(int chunkLength) {
        this.impl.setChunkedStreamingMode(chunkLength);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        this.impl.setFixedLengthStreamingMode(contentLength);
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        this.impl.setInstanceFollowRedirects(followRedirects);
    }

    public void setRequestMethod(String method) throws ProtocolException {
        this.getTransactionState();

        try {
            this.impl.setRequestMethod(method);
        } catch (ProtocolException var3) {
            this.error(var3);
            throw var3;
        }
    }

    public boolean getDefaultUseCaches() {
        return this.impl.getDefaultUseCaches();
    }

    public boolean getDoInput() {
        return this.impl.getDoInput();
    }

    public boolean getDoOutput() {
        return this.impl.getDoOutput();
    }

    public long getExpiration() {
        this.getTransactionState();
        long expiration = this.impl.getExpiration();
        this.checkResponse();
        return expiration;
    }

    public String getHeaderField(int pos) {
        this.getTransactionState();
        String header = this.impl.getHeaderField(pos);
        this.checkResponse();
        return header;
    }

    public String getHeaderField(String key) {
        this.getTransactionState();
        String header = this.impl.getHeaderField(key);
        this.checkResponse();
        return header;
    }

    public int getHeaderFieldInt(String field, int defaultValue) {
        this.getTransactionState();
        int header = this.impl.getHeaderFieldInt(field, defaultValue);
        this.checkResponse();
        return header;
    }

    public String getHeaderFieldKey(int posn) {
        this.getTransactionState();
        String key = this.impl.getHeaderFieldKey(posn);
        this.checkResponse();
        return key;
    }

    public Map<String, List<String>> getHeaderFields() {
        this.getTransactionState();
        Map<String, List<String>> fields = this.impl.getHeaderFields();
        this.checkResponse();
        return fields;
    }

    public long getIfModifiedSince() {
        this.getTransactionState();
        long ifModifiedSince = this.impl.getIfModifiedSince();
        this.checkResponse();
        return ifModifiedSince;
    }

    public InputStream getInputStream() throws IOException {
        final TransactionState transactionState = this.getTransactionState();

        CountingInputStream in;
        try {
            in = new CountingInputStream(this.impl.getInputStream());
            TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
        } catch (IOException var4) {
            this.error(var4);
            throw var4;
        }

        in.addStreamCompleteListener(new StreamCompleteListener() {
            public void streamError(StreamCompleteEvent e) {
                if(!transactionState.isComplete()) {
                    transactionState.setBytesReceived(e.getBytes());
                }

                HttpURLConnectionExtension.this.error(e.getException());
            }

            public void streamComplete(StreamCompleteEvent e) {
                if(!transactionState.isComplete()) {
                    try {
                        int statusCode = HttpURLConnectionExtension.this.impl.getResponseCode();
                        transactionState.setStatusCode(statusCode);
                    } catch (IOException var7) {
                        ;
                    }

                    long contentLength = (long)HttpURLConnectionExtension.this.impl.getContentLength();
                    long numBytes = e.getBytes();
                    if(contentLength >= 0L) {
                        numBytes = contentLength;
                    }

                    transactionState.setBytesReceived(numBytes);
                    HttpURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
                }

            }
        });
        return in;
    }

    public long getLastModified() {
        this.getTransactionState();
        long lastModified = this.impl.getLastModified();
        this.checkResponse();
        return lastModified;
    }

    public OutputStream getOutputStream() throws IOException {
        final TransactionState transactionState = this.getTransactionState();

        CountingOutputStream out;
        try {
            out = new CountingOutputStream(this.impl.getOutputStream());
        } catch (IOException var4) {
            this.error(var4);
            throw var4;
        }

        out.addStreamCompleteListener(new StreamCompleteListener() {
            public void streamError(StreamCompleteEvent e) {
                if(!transactionState.isComplete()) {
                    transactionState.setBytesSent(e.getBytes());
                }

                HttpURLConnectionExtension.this.error(e.getException());
            }

            public void streamComplete(StreamCompleteEvent e) {
                if(!transactionState.isComplete()) {
                    try {
                        int statusCode = HttpURLConnectionExtension.this.impl.getResponseCode();
                        transactionState.setStatusCode(statusCode);
                    } catch (IOException var7) {
                        ;
                    }

                    String header = HttpURLConnectionExtension.this.impl.getRequestProperty("content-length");
                    long numBytes = e.getBytes();
                    if(header != null) {
                        try {
                            numBytes = Long.parseLong(header);
                        } catch (NumberFormatException var6) {
                            ;
                        }
                    }

                    transactionState.setBytesSent(numBytes);
                    HttpURLConnectionExtension.this.addTransactionAndErrorData(transactionState);
                }

            }
        });
        return out;
    }

    public int getReadTimeout() {
        return this.impl.getReadTimeout();
    }

    public Map<String, List<String>> getRequestProperties() {
        return this.impl.getRequestProperties();
    }

    public String getRequestProperty(String field) {
        return this.impl.getRequestProperty(field);
    }

    public URL getURL() {
        return this.impl.getURL();
    }

    public boolean getUseCaches() {
        return this.impl.getUseCaches();
    }

    public void setAllowUserInteraction(boolean newValue) {
        this.impl.setAllowUserInteraction(newValue);
    }

    public void setConnectTimeout(int timeoutMillis) {
        this.impl.setConnectTimeout(timeoutMillis);
    }

    public void setDefaultUseCaches(boolean newValue) {
        this.impl.setDefaultUseCaches(newValue);
    }

    public void setDoInput(boolean newValue) {
        this.impl.setDoInput(newValue);
    }

    public void setDoOutput(boolean newValue) {
        this.impl.setDoOutput(newValue);
    }

    public void setIfModifiedSince(long newValue) {
        this.impl.setIfModifiedSince(newValue);
    }

    public void setReadTimeout(int timeoutMillis) {
        this.impl.setReadTimeout(timeoutMillis);
    }

    public void setRequestProperty(String field, String newValue) {
        this.impl.setRequestProperty(field, newValue);
    }

    public void setUseCaches(boolean newValue) {
        this.impl.setUseCaches(newValue);
    }

    public String toString() {
        return this.impl.toString();
    }

    private void checkResponse() {
        if(!this.getTransactionState().isComplete()) {
            TransactionStateUtil.inspectAndInstrumentResponse(this.getTransactionState(), this.impl);
        }

    }

    private TransactionState getTransactionState() {
        if(this.transactionState == null) {
            this.transactionState = new TransactionState();
            TransactionStateUtil.inspectAndInstrument(this.transactionState, this.impl);
        }

        return this.transactionState;
    }

    private void error(Exception e) {
        TransactionState transactionState = this.getTransactionState();
        TransactionStateUtil.setErrorCodeFromException(transactionState, e);
        if(!transactionState.isComplete()) {
            TransactionStateUtil.inspectAndInstrumentResponse(transactionState, this.impl);
            TransactionData transactionData = transactionState.end();
            if(transactionData != null) {
                TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            }
        }

    }

    private void addTransactionAndErrorData(TransactionState transactionState) {
        TransactionData transactionData = transactionState.end();
        if(transactionData != null) {
            TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            if((long)transactionState.getStatusCode() >= 400L) {
                StringBuilder responseBody = new StringBuilder();

                try {
                    InputStream errorStream = this.getErrorStream();
                    if(errorStream instanceof CountingInputStream) {
                        responseBody.append(((CountingInputStream)errorStream).getBufferAsString());
                    }
                } catch (Exception var6) {
                    log.error(var6.toString());
                }

                Map<String, String> params = new TreeMap();
                String contentType = this.impl.getContentType();
                if(contentType != null && !"".equals(contentType)) {
                    params.put("content_type", contentType);
                }

                params.put("content_length", transactionState.getBytesReceived() + "");
                Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), responseBody.toString(), params);
            }

        }
    }
}
