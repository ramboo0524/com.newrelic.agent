//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.unity;

import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.util.NetworkFailure;
import java.lang.Thread.UncaughtExceptionHandler;

public class NewRelicUnity {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final String ROOT_TRACE_NAME = "Unity";


    static void handleUnityCrash(UnityException ex) {
        UncaughtExceptionHandler currentExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if(currentExceptionHandler != null && currentExceptionHandler instanceof com.newrelic.agent.android.crash.UncaughtExceptionHandler) {
            currentExceptionHandler.uncaughtException(Thread.currentThread(), ex);
        }

    }

    static boolean recordEvent(UnityEvent event) {
        return NewRelic.recordEvent(event.getName(), event.getAttributes());
    }

    static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, int failureCode, String message) {
        NetworkFailure networkFailure = NetworkFailure.fromErrorCode(failureCode);
        NewRelic.noticeNetworkFailure(url, httpMethod, startTime, endTime, networkFailure, message);
    }
}
