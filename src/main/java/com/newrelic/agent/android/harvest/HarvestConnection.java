//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestErrorCodes;
import com.newrelic.agent.android.harvest.type.Harvestable;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.stats.TicToc;
import com.newrelic.agent.android.util.ExceptionHelper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class HarvestConnection implements HarvestErrorCodes {
    private final AgentLog log = AgentLogManager.getAgentLog();
    private static final String COLLECTOR_CONNECT_URI = "/mobile/v3/connect";
    private static final String COLLECTOR_DATA_URI = "/mobile/v3/data";
    private static final String APPLICATION_TOKEN_HEADER = "X-App-License-Key";
    private static final String CONNECT_TIME_HEADER = "X-NewRelic-Connect-Time";
    private static final Boolean DISABLE_COMPRESSION_FOR_DEBUGGING = Boolean.FALSE;
    private String collectorHost;
    private String applicationToken;
    private long serverTimestamp;
    private final HttpClient collectorClient;
    private ConnectInformation connectInformation;
    private boolean useSsl;

    public HarvestConnection() {
//        int TIMEOUT_IN_SECONDS = true;
        int CONNECTION_TIMEOUT = (int)TimeUnit.MILLISECONDS.convert(20L, TimeUnit.SECONDS);
//        int SOCKET_BUFFER_SIZE = true;
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        this.collectorClient = new DefaultHttpClient(params);
    }

    public HttpPost createPost(String uri, String message) {
        String contentEncoding = message.length() > 512 && !DISABLE_COMPRESSION_FOR_DEBUGGING ?"deflate":"identity";
        HttpPost post = new HttpPost(uri);
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Content-Encoding", contentEncoding);
        post.addHeader("User-Agent", System.getProperty("http.agent"));
        if(this.applicationToken == null) {
            this.log.error("Cannot create POST without an Application Token.");
            return null;
        } else {
            post.addHeader(APPLICATION_TOKEN_HEADER, this.applicationToken);
            if(this.serverTimestamp != 0L) {
                post.addHeader(CONNECT_TIME_HEADER, Long.valueOf(this.serverTimestamp).toString());
            }

            if("deflate".equals(contentEncoding)) {
                byte[] deflated = this.deflate(message);
                post.setEntity(new ByteArrayEntity(deflated));
            } else {
                post.setEntity(new StringEntity(message, "utf-8"));
            }

            return post;
        }
    }

    public HarvestResponse send(HttpPost post) {
        HarvestResponse harvestResponse = new HarvestResponse();

        HttpResponse response;
        try {
            TicToc timer = new TicToc();
            timer.tic();
            response = this.collectorClient.execute(post);
            harvestResponse.setResponseTime(timer.toc());
        } catch (Exception var6) {
            this.log.error("Failed to send POST to collector: " + var6.getMessage());
            this.recordCollectorError(var6);
            return null;
        }

        harvestResponse.setStatusCode(response.getStatusLine().getStatusCode());

        try {
            harvestResponse.setResponseBody(readResponse(response));
        } catch (IOException var5) {
            var5.printStackTrace();
            this.log.error("Failed to retrieve collector response: " + var5.getMessage());
        }

        return harvestResponse;
    }

    public HarvestResponse sendConnect() {
        if(this.connectInformation == null) {
            throw new IllegalArgumentException();
        } else {
            HttpPost connectPost = this.createConnectPost(this.connectInformation.toJsonString());
            if(connectPost == null) {
                this.log.error("Failed to create connect POST");
                return null;
            } else {
                TicToc timer = new TicToc();
                timer.tic();
                HarvestResponse response = this.send(connectPost);
                StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Connect", timer.toc());
                return response;
            }
        }
    }

    public HarvestResponse sendData(Harvestable harvestable) {
        if(harvestable == null) {
            throw new IllegalArgumentException();
        } else {
            HttpPost dataPost = this.createDataPost(harvestable.toJsonString());
            if(dataPost == null) {
                this.log.error("Failed to create data POST");
                return null;
            } else {
                return this.send(dataPost);
            }
        }
    }

    public HttpPost createConnectPost(String message) {
        return this.createPost(this.getCollectorConnectUri(), message);
    }

    public HttpPost createDataPost(String message) {
        return this.createPost(this.getCollectorDataUri(), message);
    }

    private byte[] deflate(String message) {
//        int DEFLATE_BUFFER_SIZE = true;
        Deflater deflater = new Deflater();
        deflater.setInput(message.getBytes());
        deflater.finish();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int byteCount;
        for(byte[] buf = new byte[8192]; !deflater.finished(); baos.write(buf, 0, byteCount)) {
            byteCount = deflater.deflate(buf);
            if(byteCount <= 0) {
                this.log.error("HTTP request contains an incomplete payload");
            }
        }

        deflater.end();
        return baos.toByteArray();
    }

    public static String readResponse(HttpResponse response) throws IOException {
//        int RESPONSE_BUFFER_SIZE = true;
        char[] buf = new char[8192];
        StringBuilder sb = new StringBuilder();
        InputStream in = response.getEntity().getContent();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while(true) {
                int n = reader.read(buf);
                if(n < 0) {
                    return sb.toString();
                }

                sb.append(buf, 0, n);
            }
        } finally {
            in.close();
        }
    }

    private void recordCollectorError(Exception e) {
        this.log.error("HarvestConnection: Attempting to convert network exception " + e.getClass().getName() + " to error code.");
        StatsEngine.get().inc("Supportability/AgentHealth/Collector/ResponseErrorCodes/" + ExceptionHelper.exceptionToErrorCode(e));
    }

    private String getCollectorUri(String resource) {
        String protocol = this.useSsl?"https://":"http://";
        return protocol + this.collectorHost + resource;
    }

    private String getCollectorConnectUri() {
        return this.getCollectorUri(COLLECTOR_CONNECT_URI);
    }

    private String getCollectorDataUri() {
        return this.getCollectorUri(COLLECTOR_DATA_URI);
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.log.debug("Setting server timestamp: " + serverTimestamp);
        this.serverTimestamp = serverTimestamp;
    }

    public void useSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public void setCollectorHost(String collectorHost) {
        this.collectorHost = collectorHost;
    }

    public void setConnectInformation(ConnectInformation connectInformation) {
        this.connectInformation = connectInformation;
    }

    public ConnectInformation getConnectInformation() {
        return this.connectInformation;
    }
}
