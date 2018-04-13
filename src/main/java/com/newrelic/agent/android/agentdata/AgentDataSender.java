//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.agentdata;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.payload.Payload;
import com.newrelic.agent.android.payload.PayloadController;
import com.newrelic.agent.android.payload.PayloadSender;
import com.newrelic.agent.android.stats.StatsEngine;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class AgentDataSender extends PayloadSender {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE = "application/octet-stream";

    public AgentDataSender(byte[] bytes, AgentConfiguration agentConfiguration) {
        super(bytes, agentConfiguration);
    }

    public AgentDataSender(Payload payload, AgentConfiguration agentConfiguration) {
        super(payload, agentConfiguration);
    }

    protected HttpURLConnection getConnection() throws IOException {
        String urlString = this.getProtocol() + this.agentConfiguration.getHexCollectorHost() + this.agentConfiguration.getHexCollectorPath();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        connection.setRequestMethod("POST");
        connection.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE);
        connection.setRequestProperty(this.agentConfiguration.getAppTokenHeader(), this.agentConfiguration.getApplicationToken());
        connection.setRequestProperty(this.agentConfiguration.getDeviceOsNameHeader(), Agent.getDeviceInformation().getOsName());
        connection.setRequestProperty(this.agentConfiguration.getAppVersionHeader(), Agent.getApplicationInformation().getAppVersion());
        connection.setConnectTimeout(this.agentConfiguration.getHexCollectorTimeout());
        connection.setReadTimeout(this.agentConfiguration.getHexCollectorTimeout());
        return connection;
    }

    protected void onRequestResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        switch(responseCode) {
            case 200:
                StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Hex/UploadTime", this.timer.peek());
                break;
            case 500:
                this.onFailedUpload("The data payload was rejected and will be deleted - Response code " + responseCode);
                StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Hex/FailedUpload", this.timer.peek());
                break;
            default:
                this.onFailedUpload("Something went wrong while submitting the data payload (will try again later) - Response code " + responseCode);
        }

        log.debug("Handled Exception collection took " + this.timer.toc() + "ms");
    }

    protected void onFailedUpload(String errorMsg) {
        log.error(errorMsg);
        StatsEngine.get().inc("Supportability/AgentHealth/Hex/FailedUpload");
    }

    protected boolean shouldUploadOpportunistically() {
        return PayloadController.shouldUploadOpportunistically();
    }
}
