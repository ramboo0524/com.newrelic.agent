//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ConnectTimeoutException;

public enum NetworkFailure {
    Unknown(-1),
    BadURL(-1000),
    TimedOut(-1001),
    CannotConnectToHost(-1004),
    DNSLookupFailed(-1006),
    BadServerResponse(-1011),
    SecureConnectionFailed(-1200);

    private int errorCode;
    private static final AgentLog log = AgentLogManager.getAgentLog();

    private NetworkFailure(int errorCode) {
        this.errorCode = errorCode;
    }

    public static NetworkFailure exceptionToNetworkFailure(Exception e) {
        log.error("NetworkFailure.exceptionToNetworkFailure: Attempting to convert network exception " + e.getClass().getName() + " to error code.");
        NetworkFailure error = Unknown;
        if(e instanceof UnknownHostException) {
            error = DNSLookupFailed;
        } else if(!(e instanceof SocketTimeoutException) && !(e instanceof ConnectTimeoutException)) {
            if(e instanceof ConnectException) {
                error = CannotConnectToHost;
            } else if(e instanceof MalformedURLException) {
                error = BadURL;
            } else if(e instanceof SSLException) {
                error = SecureConnectionFailed;
            } else if(e instanceof HttpResponseException || e instanceof ClientProtocolException) {
                error = BadServerResponse;
            }
        } else {
            error = TimedOut;
        }

        return error;
    }

    public static int exceptionToErrorCode(Exception e) {
        return exceptionToNetworkFailure(e).getErrorCode();
    }

    public static NetworkFailure fromErrorCode(int errorCode) {
        log.debug("fromErrorCode invoked with errorCode: " + errorCode);
        NetworkFailure[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            NetworkFailure failure = var1[var3];
            if(failure.getErrorCode() == errorCode) {
                log.debug("fromErrorCode found matching failure: " + failure);
                return failure;
            }
        }

        return Unknown;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
