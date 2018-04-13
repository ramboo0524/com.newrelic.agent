//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.http;

import com.newrelic.agent.android.measurement.BaseMeasurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Map;

public class HttpErrorMeasurement extends BaseMeasurement {
    private String url;
    private int httpStatusCode;
    private int errorCode;
    private String responseBody;
    private String stackTrace;
    private Map<String, String> params;
    private String httpMethod;
    private String wanType;
    private double totalTime;
    private long bytesSent;
    private long bytesReceived;

    public HttpErrorMeasurement(String url, int httpStatusCode) {
        super(MeasurementType.HttpError);
        this.setUrl(url);
        this.setName(url);
        this.setHttpStatusCode(httpStatusCode);
        this.setStartTime(System.currentTimeMillis());
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return this.url;
    }

    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    public String getResponseBody() {
        return this.responseBody;
    }

    public String getStackTrace() {
        return this.stackTrace;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getWanType() {
        return this.wanType;
    }

    public void setWanType(String wanType) {
        this.wanType = wanType;
    }

    public double getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public long getBytesSent() {
        return this.bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
