//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.payload;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.TicToc;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;

public abstract class PayloadSender implements Callable<PayloadSender> {
    protected static final AgentLog log = AgentLogManager.getAgentLog();
    protected Payload payload;
    protected final AgentConfiguration agentConfiguration;
    protected final TicToc timer;
    protected int responseCode;

    public PayloadSender(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
        this.timer = new TicToc();
        this.responseCode = 0;
    }

    public PayloadSender(Payload payload, AgentConfiguration agentConfiguration) {
        this(agentConfiguration);
        this.payload = payload;
    }

    public PayloadSender(byte[] payloadBytes, AgentConfiguration agentConfiguration) {
        this(agentConfiguration);
        this.payload = new Payload(payloadBytes);
    }

    public Payload getPayload() {
        return this.payload;
    }

    public void setPayload(byte[] payloadBytes) {
        this.payload.putBytes(payloadBytes);
    }

    protected abstract HttpURLConnection getConnection() throws IOException;

    protected void onRequestResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        switch(responseCode) {
            case 200:
                InputStream responseInputStream = connection.getInputStream();
                if(responseInputStream != null) {
                    String responseString = this.readStream(responseInputStream, responseInputStream.available());
                    this.onRequestContent(responseString);
                }
                break;
            case 500:
                this.onFailedUpload("The data payload was rejected and will be deleted - Response code " + responseCode);
                break;
            default:
                this.onFailedUpload("Something went wrong while submitting the data payload (will try again later) - Response code " + responseCode);
        }

        log.debug("Payload delivery took " + this.timer.toc() + "ms");
    }

    protected void onRequestContent(String responseString) {
    }

    protected void onRequestException(Exception e) {
        this.onFailedUpload("Data upload failed: " + e);
    }

    protected void onFailedUpload(String errorMsg) {
        log.error(errorMsg);
    }

    public PayloadSender call() throws Exception {
        try {
            this.timer.tic();
            HttpURLConnection connection = this.getConnection();

            try {
                connection.connect();
                BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

                try {
                    out.write(this.payload.getBytes());
                } finally {
                    out.close();
                }

                this.responseCode = connection.getResponseCode();
                this.onRequestResponse(connection);
            } catch (Exception var14) {
                this.onRequestException(var14);
            } finally {
                connection.disconnect();
            }

            return this;
        } catch (Exception var16) {
            this.onFailedUpload("Unable to upload data to New Relic, will try again later. " + var16);
            return this;
        }
    }

    protected String getProtocol() {
        return this.agentConfiguration.useSsl()?"https://":"http://";
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    protected String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[maxLength];
        int numChars = 0;

        for(int readSize = 0; numChars < maxLength && readSize != -1; readSize = reader.read(buffer, numChars, buffer.length - numChars)) {
            numChars += readSize;
        }

        if(numChars != -1) {
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }

        return result;
    }

    public boolean isSuccessfulResponse() {
        switch(this.responseCode) {
            case 200:
            case 500:
                return true;
            default:
                return false;
        }
    }

    public boolean equals(Object object) {
        return object != null && object instanceof PayloadSender?this.getPayload() == ((PayloadSender)object).getPayload():false;
    }

    protected boolean shouldUploadOpportunistically() {
        return Agent.hasReachableNetworkConnection((String)null);
    }

    public boolean shouldRetry() {
        return false;
    }

    public interface CompletionHandler {
        void onResponse(PayloadSender var1);

        void onException(PayloadSender var1, Exception var2);
    }
}
