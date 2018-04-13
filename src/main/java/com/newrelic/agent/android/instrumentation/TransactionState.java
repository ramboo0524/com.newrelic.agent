//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.util.Util;
import java.net.MalformedURLException;
import java.net.URL;

public final class TransactionState {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private String url;
    private String httpMethod;
    private int statusCode;
    private int errorCode;
    private long bytesSent;
    private long bytesReceived;
    private long startTime = System.currentTimeMillis();
    private long endTime;
    private String appData;
    private String carrier = "unknown";
    private String wanType = "unknown";
    private TransactionState.State state;
    private String contentType;
    private TransactionData transactionData;

    public TransactionState() {
        this.state = TransactionState.State.READY;
        TraceMachine.enterNetworkSegment("External/unknownhost");
    }

    public void setCarrier(String carrier) {
        if(!this.isSent()) {
            this.carrier = carrier;
            TraceMachine.setCurrentTraceParam("carrier", carrier);
        } else {
            log.warning("setCarrier(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public void setWanType(String wanType) {
        if(!this.isSent()) {
            this.wanType = wanType;
            TraceMachine.setCurrentTraceParam("wan_type", wanType);
        } else {
            log.warning("setWanType(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public void setAppData(String appData) {
        if(!this.isComplete()) {
            this.appData = appData;
            TraceMachine.setCurrentTraceParam("encoded_app_data", appData);
        } else {
            log.warning("setAppData(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public void setUrl(String urlString) {
        String url = Util.sanitizeUrl(urlString);
        if(url != null) {
            if(!this.isSent()) {
                this.url = url;

                try {
                    TraceMachine.setCurrentDisplayName("External/" + (new URL(url)).getHost());
                } catch (MalformedURLException var4) {
                    log.error("unable to parse host name from " + url);
                }

                TraceMachine.setCurrentTraceParam("uri", url);
            } else {
                log.warning("setUrl(...) called on TransactionState in " + this.state.toString() + " state");
            }

        }
    }

    public void setHttpMethod(String httpMethod) {
        if(!this.isSent()) {
            this.httpMethod = httpMethod;
            TraceMachine.setCurrentTraceParam("http_method", httpMethod);
        } else {
            log.warning("setHttpMethod(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public String getUrl() {
        return this.url;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public boolean isSent() {
        return this.state.ordinal() >= TransactionState.State.SENT.ordinal();
    }

    public boolean isComplete() {
        return this.state.ordinal() >= TransactionState.State.COMPLETE.ordinal();
    }

    public void setStatusCode(int statusCode) {
        if(!this.isComplete()) {
            this.statusCode = statusCode;
            TraceMachine.setCurrentTraceParam("status_code", statusCode);
        } else {
            log.warning("setStatusCode(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setErrorCode(int errorCode) {
        if(!this.isComplete()) {
            this.errorCode = errorCode;
            TraceMachine.setCurrentTraceParam("error_code", errorCode);
        } else {
            if(this.transactionData != null) {
                this.transactionData.setErrorCode(errorCode);
            }

            log.warning("setErrorCode(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setBytesSent(long bytesSent) {
        if(!this.isComplete()) {
            this.bytesSent = bytesSent;
            TraceMachine.setCurrentTraceParam("bytes_sent", bytesSent);
            this.state = TransactionState.State.SENT;
        } else {
            log.warning("setBytesSent(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public void setBytesReceived(long bytesReceived) {
        if(!this.isComplete()) {
            this.bytesReceived = bytesReceived;
            TraceMachine.setCurrentTraceParam("bytes_received", bytesReceived);
        } else {
            log.warning("setBytesReceived(...) called on TransactionState in " + this.state.toString() + " state");
        }

    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public TransactionData end() {
        if(!this.isComplete()) {
            this.state = TransactionState.State.COMPLETE;
            this.endTime = System.currentTimeMillis();
            TraceMachine.exitMethod();
        }

        return this.toTransactionData();
    }

    private TransactionData toTransactionData() {
        if(!this.isComplete()) {
            log.warning("toTransactionData() called on incomplete TransactionState");
        }

        if(this.url == null) {
            log.error("Attempted to convert a TransactionState instance with no URL into a TransactionData");
            return null;
        } else {
            if(this.transactionData == null) {
                this.transactionData = new TransactionData(this.url, this.httpMethod, this.carrier, (float)(this.endTime - this.startTime) / 1000.0F, this.statusCode, this.errorCode, this.bytesSent, this.bytesReceived, this.appData, this.wanType);
            }

            return this.transactionData;
        }
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String toString() {
        return "TransactionState{url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", appData='" + this.appData + '\'' + ", carrier='" + this.carrier + '\'' + ", wanType='" + this.wanType + '\'' + ", state=" + this.state + ", contentType='" + this.contentType + '\'' + ", transactionData=" + this.transactionData + '}';
    }

    private static enum State {
        READY,
        SENT,
        COMPLETE;

        private State() {
        }
    }
}
