//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.crash;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.payload.PayloadSender;
import com.newrelic.agent.android.stats.StatsEngine;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class CrashSender extends PayloadSender {
    public static final int CRASH_COLLECTOR_TIMEOUT = 5000;
    private static final String CRASH_COLLECTOR_PATH = "/mobile_crash";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE = "application/json";
    private final Crash crash;

    public CrashSender(Crash crash, AgentConfiguration agentConfiguration) {
        super(crash.toJsonString().getBytes(), agentConfiguration);
        this.crash = crash;
    }

    protected HttpURLConnection getConnection() throws IOException {
        String urlString = this.getProtocol() + this.agentConfiguration.getCrashCollectorHost() + CRASH_COLLECTOR_PATH;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        connection.setRequestProperty(this.agentConfiguration.getAppTokenHeader(), this.agentConfiguration.getApplicationToken());
        connection.setRequestProperty(this.agentConfiguration.getDeviceOsNameHeader(), Agent.getDeviceInformation().getOsName());
        connection.setRequestProperty(this.agentConfiguration.getAppVersionHeader(), Agent.getApplicationInformation().getAppVersion());
        connection.setConnectTimeout(CRASH_COLLECTOR_TIMEOUT);
        connection.setReadTimeout(CRASH_COLLECTOR_TIMEOUT);
        return connection;
    }

    public PayloadSender call() throws Exception {
        this.setPayload(this.crash.toJsonString().getBytes());
        this.crash.incrementUploadCount();
        this.agentConfiguration.getCrashStore().store(this.crash);

        try {
            return super.call();
        } catch (Exception var2) {
            this.onFailedUpload("Unable to report crash to New Relic, will try again later. " + var2);
            return this;
        }
    }

    protected void onRequestResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        switch(responseCode) {
            case 200:
                StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Crash/UploadTime", this.timer.peek());
                log.info("Crash " + this.crash.getUuid().toString() + " successfully submitted.");
                break;
            case 500:
                StatsEngine.get().inc("Supportability/AgentHealth/Crash/Removed/Rejected");
                this.onFailedUpload("The crash was rejected and will be deleted - Response code " + connection.getResponseCode());
                break;
            default:
                this.onFailedUpload("Something went wrong while submitting a crash (will try again later) - Response code " + connection.getResponseCode());
        }

        log.debug("Crash collection took " + this.timer.toc() + "ms");
    }

    protected void onFailedUpload(String errorMsg) {
        log.error(errorMsg);
        StatsEngine.get().inc("Supportability/AgentHealth/Crash/FailedUpload");
    }

    protected void onRequestException(Exception e) {
        log.error("Crash upload failed: " + e);
    }

    protected boolean shouldUploadOpportunistically() {
        return Agent.hasReachableNetworkConnection((String)null);
    }
}
