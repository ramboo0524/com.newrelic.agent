//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.http;

import com.newrelic.agent.android.measurement.BaseMeasurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.util.Util;

public class HttpTransactionMeasurement extends BaseMeasurement {
    private String url;
    private String httpMethod;
    private double totalTime;
    private int statusCode;
    private int errorCode;
    private long bytesSent;
    private long bytesReceived;
    private String appData;

    public HttpTransactionMeasurement(String url, String httpMethod, int statusCode, long startTime, double totalTime, long bytesSent, long bytesReceived, String appData) {
        super(MeasurementType.Network);
        url = Util.sanitizeUrl(url);
        this.setName(url);
        this.setScope(TraceMachine.getCurrentScope());
        this.setStartTime(startTime);
        this.setEndTime(startTime + (long)((int)totalTime));
        this.setExclusiveTime((long)((int)(totalTime * 1000.0D)));
        this.url = url;
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.totalTime = totalTime;
        this.appData = appData;
    }

    public HttpTransactionMeasurement(String url, String httpMethod, int statusCode, int errorCode, long startTime, double totalTime, long bytesSent, long bytesReceived, String appData) {
        this(url, httpMethod, statusCode, startTime, totalTime, bytesSent, bytesReceived, appData);
        this.errorCode = errorCode;
    }

    public double asDouble() {
        return this.totalTime;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public double getTotalTime() {
        return this.totalTime;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public long getBytesSent() {
        return this.bytesSent;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public String getAppData() {
        return this.appData;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return "HttpTransactionMeasurement{url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", totalTime=" + this.totalTime + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + '}';
    }
}
