//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import com.newrelic.agent.android.harvest.AgentHealth;
import com.newrelic.agent.android.harvest.AgentHealthException;
import com.newrelic.agent.android.harvest.type.HarvestErrorCodes;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.PortUnreachableException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;

public class ExceptionHelper implements HarvestErrorCodes {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    public ExceptionHelper() {
    }

    public static int exceptionToErrorCode(Exception e) {
        int errorCode = NSURLErrorUnknown;
        log.debug("ExceptionHelper: exception " + e.getClass().getName() + " to error code.");
        if(e instanceof ClientProtocolException) {
            errorCode = NSURLErrorBadServerResponse;
        } else if(e instanceof UnknownHostException) {
            errorCode = NSURLErrorDNSLookupFailed;
        } else if(e instanceof NoRouteToHostException) {
            errorCode = NSURLErrorCannotFindHost;
        } else if(e instanceof PortUnreachableException) {
            errorCode = NSURLErrorCannotFindHost;
        } else if(e instanceof SocketTimeoutException) {
            errorCode = NSURLErrorTimedOut;
        } else if(e instanceof ConnectTimeoutException) {
            errorCode = NSURLErrorTimedOut;
        } else if(e instanceof ConnectException) {
            errorCode = NSURLErrorCannotConnectToHost;
        } else if(e instanceof MalformedURLException) {
            errorCode = NSURLErrorBadURL;
        } else if(e instanceof SSLException) {
            errorCode = NSURLErrorSecureConnectionFailed;
        } else if(e instanceof FileNotFoundException) {
            errorCode = NRURLErrorFileDoesNotExist;
        } else if(e instanceof EOFException) {
            errorCode = NSURLErrorRequestBodyStreamExhausted;
        } else if(e instanceof IOException) {
            recordSupportabilityMetric(e, "IOException");
        } else if(e instanceof RuntimeException) {
            recordSupportabilityMetric(e, "RuntimeException");
        }

        return errorCode;
    }

    public static void recordSupportabilityMetric(Exception e, String baseExceptionKey) {
        AgentHealthException agentHealthException = new AgentHealthException(e);
        StackTraceElement topTraceElement = agentHealthException.getStackTrace()[0];
        log.error(String.format("ExceptionHelper: %s:%s(%s:%s) %s[%s] %s", new Object[]{agentHealthException.getSourceClass(), agentHealthException.getSourceMethod(), topTraceElement.getFileName(), Integer.valueOf(topTraceElement.getLineNumber()), baseExceptionKey, agentHealthException.getExceptionClass(), agentHealthException.getMessage()}));
        AgentHealth.noticeException(agentHealthException, baseExceptionKey);
    }
}
