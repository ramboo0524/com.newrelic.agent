//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;

public class HttpTransaction extends HarvestableArray {
    private String url;
    private String httpMethod;
    private String carrier;
    private String wanType;
    private double totalTime;
    private int statusCode;
    private int errorCode;
    private long bytesSent;
    private long bytesReceived;
    private String appData;
    private Long timestamp;

    public HttpTransaction() {
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        array.add(SafeJsonPrimitive.factory(this.url));
        array.add(SafeJsonPrimitive.factory(this.carrier));
        array.add(SafeJsonPrimitive.factory(this.totalTime));
        array.add(SafeJsonPrimitive.factory(this.statusCode));
        array.add(SafeJsonPrimitive.factory(this.errorCode));
        array.add(SafeJsonPrimitive.factory(this.bytesSent));
        array.add(SafeJsonPrimitive.factory(this.bytesReceived));
        array.add(this.appData == null?null:SafeJsonPrimitive.factory(this.appData));
        array.add(SafeJsonPrimitive.factory(this.wanType));
        array.add(SafeJsonPrimitive.factory(this.httpMethod));
        return array;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public void setWanType(String wanType) {
        this.wanType = wanType;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public void setAppData(String appData) {
        this.appData = appData;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHttpMethod() {
        return this.httpMethod;
    }

    public String getCarrier() {
        return this.carrier;
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

    public String getWanType() {
        return this.wanType;
    }

    public String toString() {
        return "HttpTransaction{url='" + this.url + '\'' + ", carrier='" + this.carrier + '\'' + ", wanType='" + this.wanType + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", totalTime=" + this.totalTime + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + ", timestamp=" + this.timestamp + '}';
    }
}
