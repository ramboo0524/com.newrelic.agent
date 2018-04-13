//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.api.common;

import java.util.ArrayList;
import java.util.List;

public class TransactionData {
    private final long timestamp;
    private final String url;
    private final String httpMethod;
    private final String carrier;
    private final float time;
    private final int statusCode;
    private int errorCode;
    private final Object errorCodeLock = new Object();
    private final long bytesSent;
    private final long bytesReceived;
    private final String appData;
    private final String wanType;

    public TransactionData(String url, String httpMethod, String carrier, float time, int statusCode, int errorCode, long bytesSent, long bytesReceived, String appData, String wanType) {
        int endPos = url.indexOf(63);
        if(endPos < 0) {
            endPos = url.indexOf(59);
            if(endPos < 0) {
                endPos = url.length();
            }
        }

        String trimmedUrl = url.substring(0, endPos);
        this.url = trimmedUrl;
        this.httpMethod = httpMethod;
        this.carrier = carrier;
        this.time = time;
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.appData = appData;
        this.wanType = wanType;
        this.timestamp = System.currentTimeMillis();
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

    public int getStatusCode() {
        return this.statusCode;
    }

    public int getErrorCode() {
        Object var1 = this.errorCodeLock;
        synchronized(this.errorCodeLock) {
            return this.errorCode;
        }
    }

    public void setErrorCode(int errorCode) {
        Object var2 = this.errorCodeLock;
        synchronized(this.errorCodeLock) {
            this.errorCode = errorCode;
        }
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

    public List<Object> asList() {
        ArrayList<Object> r = new ArrayList<>();
        r.add(this.url);
        r.add(this.carrier);
        r.add(this.time);
        r.add(this.statusCode);
        r.add(this.errorCode);
        r.add(this.bytesSent);
        r.add(this.bytesReceived);
        r.add(this.appData);
        return r;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public float getTime() {
        return this.time;
    }

    public TransactionData clone() {
        return new TransactionData(this.url, this.httpMethod, this.carrier, this.time, this.statusCode, this.errorCode, this.bytesSent, this.bytesReceived, this.appData, this.wanType);
    }

    public String toString() {
        return "TransactionData{timestamp=" + this.timestamp + ", url='" + this.url + '\'' + ", httpMethod='" + this.httpMethod + '\'' + ", carrier='" + this.carrier + '\'' + ", time=" + this.time + ", statusCode=" + this.statusCode + ", errorCode=" + this.errorCode + ", errorCodeLock=" + this.errorCodeLock + ", bytesSent=" + this.bytesSent + ", bytesReceived=" + this.bytesReceived + ", appData='" + this.appData + '\'' + ", wanType='" + this.wanType + '\'' + '}';
    }
}
