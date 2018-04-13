//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import android.content.Context;
import android.text.TextUtils;
import com.newrelic.agent.android.agentdata.AgentDataController;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.logging.AndroidAgentLog;
import com.newrelic.agent.android.logging.NullAgentLog;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.newrelic.agent.android.metric.MetricUnit;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.tracing.TracingInactiveException;
import com.newrelic.agent.android.util.NetworkFailure;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

public final class NewRelic {
    private static final String UNKNOWN_HTTP_REQUEST_TYPE = "unknown";
    protected static final AgentLog log = AgentLogManager.getAgentLog();
    protected static final AgentConfiguration agentConfiguration = new AgentConfiguration();
    protected static boolean started = false;
    protected boolean loggingEnabled = true;
    protected int logLevel = 3;

    protected NewRelic(String token) {
        agentConfiguration.setApplicationToken(token);
    }

    public static NewRelic withApplicationToken(String token) {
        return new NewRelic(token);
    }

    public NewRelic usingSsl(boolean useSsl) {
        agentConfiguration.setUseSsl(useSsl);
        return this;
    }

    public NewRelic usingCollectorAddress(String address) {
        agentConfiguration.setCollectorHost(address);
        return this;
    }

    public NewRelic usingCrashCollectorAddress(String address) {
        agentConfiguration.setCrashCollectorHost(address);
        return this;
    }

    public NewRelic withLocationServiceEnabled(boolean enabled) {
        agentConfiguration.setUseLocationService(enabled);
        return this;
    }

    public NewRelic withLoggingEnabled(boolean enabled) {
        this.loggingEnabled = enabled;
        return this;
    }

    public NewRelic withLogLevel(int level) {
        this.logLevel = level;
        return this;
    }

    public NewRelic withCrashReportingEnabled(boolean enabled) {
        agentConfiguration.setReportCrashes(enabled);
        if(enabled) {
            enableFeature(FeatureFlag.CrashReporting);
        } else {
            disableFeature(FeatureFlag.CrashReporting);
        }

        return this;
    }

    public NewRelic withHttpResponseBodyCaptureEnabled(boolean enabled) {
        if(enabled) {
            enableFeature(FeatureFlag.HttpResponseBodyCapture);
        } else {
            disableFeature(FeatureFlag.HttpResponseBodyCapture);
        }

        return this;
    }

    public NewRelic withApplicationVersion(String appVersion) {
        if(appVersion != null) {
            agentConfiguration.setCustomApplicationVersion(appVersion);
        }

        return this;
    }

    public NewRelic withApplicationFramework(ApplicationPlatform applicationPlatform) {
        if(applicationPlatform != null) {
            agentConfiguration.setApplicationPlatform(applicationPlatform);
        }

        return this;
    }

    /** @deprecated */
    @Deprecated
    public NewRelic withAnalyticsEvents(boolean enabled) {
        enableFeature(FeatureFlag.AnalyticsEvents);
        return this;
    }

    public NewRelic withInteractionTracing(boolean enabled) {
        if(enabled) {
            enableFeature(FeatureFlag.InteractionTracing);
        } else {
            disableFeature(FeatureFlag.InteractionTracing);
        }

        return this;
    }

    public NewRelic withDefaultInteractions(boolean enabled) {
        if(enabled) {
            enableFeature(FeatureFlag.DefaultInteractions);
        } else {
            disableFeature(FeatureFlag.DefaultInteractions);
        }

        return this;
    }

    public static void enableFeature(FeatureFlag featureFlag) {
        FeatureFlag.enableFeature(featureFlag);
    }

    public static void disableFeature(FeatureFlag featureFlag) {
        log.debug("Disable feature: " + featureFlag.name());
        FeatureFlag.disableFeature(featureFlag);
    }

    /** @deprecated */
    @Deprecated
    public NewRelic withBuildIdentifier(String buildId) {
        StatsEngine.get().inc("Supportability/AgentHealth/Deprecated/WithBuildIdentifier");
        return this.withApplicationBuild(buildId);
    }

    public NewRelic withApplicationBuild(String buildId) {
        if(!TextUtils.isEmpty(buildId)) {
            agentConfiguration.setCustomBuildIdentifier(buildId);
        }

        return this;
    }

    public void start(Context context) {
        if(started) {
            log.debug("NewRelic is already running.");
        } else {
            try {
                AgentLogManager.setAgentLog((this.loggingEnabled?new AndroidAgentLog():new NullAgentLog()));
                log.setLevel(this.logLevel);
                if(!this.isInstrumented()) {
                    log.error("Failed to detect New Relic instrumentation.  Something likely went wrong during your build process and you should visit http://support.newrelic.com.");
                    return;
                }

                AndroidAgentImpl.init(context, agentConfiguration);
                started = true;
            } catch (Throwable var3) {
                log.error("Error occurred while starting the New Relic agent!", var3);
            }

        }
    }

    public static boolean isStarted() {
        return started;
    }

    /** @deprecated */
    @Deprecated
    public static void shutdown() {
        StatsEngine.get().inc("Supportability/AgentHealth/Deprecated/Shutdown");
        if(started) {
            try {
                Agent.getImpl().stop();
            } finally {
                Agent.setImpl(NullAgentImpl.instance);
                started = false;
            }
        }

    }

    private boolean isInstrumented() {
        log.info("isInstrumented: checking for Mono instrumentation flag - " + Agent.getMonoInstrumentationFlag());
        return Agent.getMonoInstrumentationFlag().equals("YES");
    }

    public static String startInteraction(String actionName) {
        checkNull(actionName, "startInteraction: actionName must be an action/method name.");
        log.debug("NewRelic.startInteraction invoked. actionName: " + actionName);
        TraceMachine.startTracing(actionName.replace("/", "."), true, FeatureFlag.featureEnabled(FeatureFlag.InteractionTracing));

        try {
            return TraceMachine.getActivityTrace().getId();
        } catch (TracingInactiveException var2) {
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public static String startInteraction(Context activityContext, String actionName) {
        checkNull(activityContext, "startInteraction: context must be an Activity instance.");
        checkNull(actionName, "startInteraction: actionName must be an action/method name.");
        TraceMachine.startTracing(activityContext.getClass().getSimpleName() + "#" + actionName.replace("/", "."), false, FeatureFlag.featureEnabled(FeatureFlag.InteractionTracing));

        try {
            return TraceMachine.getActivityTrace().getId();
        } catch (TracingInactiveException var3) {
            return null;
        }
    }

    /** @deprecated */
    @Deprecated
    public static String startInteraction(Context context, String actionName, boolean cancelRunningTrace) {
        if(TraceMachine.isTracingActive() && !cancelRunningTrace) {
            log.warning("startInteraction: An interaction is already being traced, and invalidateActiveTrace is false. This interaction will not be traced.");
            return null;
        } else {
            return startInteraction(context, actionName);
        }
    }

    public static void endInteraction(String id) {
        log.debug("NewRelic.endInteraction invoked. id: " + id);
        TraceMachine.endTrace(id);
    }

    public static void setInteractionName(String name) {
        TraceMachine.setRootDisplayName(name);
    }

    public static void startMethodTrace(String actionName) {
        checkNull(actionName, "startMethodTrace: actionName must be an action/method name.");
        TraceMachine.enterMethod(actionName);
    }

    public static void endMethodTrace() {
        log.debug("NewRelic.endMethodTrace invoked.");
        TraceMachine.exitMethod();
    }

    public static void recordMetric(String name, String category, int count, double totalValue, double exclusiveValue) {
        recordMetric(name, category, count, totalValue, exclusiveValue, (MetricUnit)null, (MetricUnit)null);
    }

    public static void recordMetric(String name, String category, int count, double totalValue, double exclusiveValue, MetricUnit countUnit, MetricUnit valueUnit) {
        log.debug("NewRelic.recordMeric invoked for name " + name + ", category: " + category + ", count: " + count + ", totalValue " + totalValue + ", exclusiveValue: " + exclusiveValue + ", countUnit: " + countUnit + ", valueUnit: " + valueUnit);
        checkNull(category, "recordMetric: category must not be null. If no MetricCategory is applicable, use MetricCategory.NONE.");
        checkEmpty(name, "recordMetric: name must not be empty.");
        if(!checkNegative(count, "recordMetric: count must not be negative.")) {
            Measurements.addCustomMetric(name, category, count, totalValue, exclusiveValue, countUnit, valueUnit);
        }

    }

    public static void recordMetric(String name, String category, double value) {
        recordMetric(name, category, 1, value, value, (MetricUnit)null, (MetricUnit)null);
    }

    public static void recordMetric(String name, String category) {
        recordMetric(name, category, 1.0D);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived) {
        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, (String)null, (Map)null, (String)null);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody) {
        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, (Map)null, (String)null);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params) {
        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, (String)null);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, appData);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, HttpResponse httpResponse) {
        if(httpResponse != null) {
            Header header = httpResponse.getFirstHeader("X-NewRelic-ID");
            if(header != null && header.getValue() != null && header.getValue().length() > 0) {
                _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, header.getValue());
                return;
            }
        }

        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, (String)null);
    }

    public static void noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, URLConnection urlConnection) {
        if(urlConnection != null) {
            String header = urlConnection.getHeaderField("X-NewRelic-ID");
            if(header != null && header.length() > 0) {
                _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, header);
                return;
            }
        }

        _noticeHttpTransaction(url, httpMethod, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, HttpResponse httpResponse) {
        noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, httpResponse);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, URLConnection urlConnection) {
        noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, urlConnection);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived) {
        _noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, (String)null, (Map)null, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody) {
        _noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, (Map)null, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params) {
        _noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, (String)null);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeHttpTransaction(String url, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
        _noticeHttpTransaction(url, UNKNOWN_HTTP_REQUEST_TYPE, statusCode, startTimeMs, endTimeMs, bytesSent, bytesReceived, responseBody, params, appData);
    }

    protected static void _noticeHttpTransaction(String url, String httpMethod, int statusCode, long startTimeMs, long endTimeMs, long bytesSent, long bytesReceived, String responseBody, Map<String, String> params, String appData) {
        checkEmpty(url, "noticeHttpTransaction: url must not be empty.");
        checkEmpty(httpMethod, "noticeHttpTransaction: httpMethod must not be empty.");

        try {
            new URL(url);
        } catch (MalformedURLException var17) {
            throw new IllegalArgumentException("noticeHttpTransaction: URL is malformed: " + url);
        }

        double totalTime = (double)(endTimeMs - startTimeMs);
        if(!checkNegative((int)totalTime, "noticeHttpTransaction: the startTimeMs is later than the endTimeMs, resulting in a negative total time.")) {
            totalTime /= 1000.0D;
            TaskQueue.queue(new HttpTransactionMeasurement(url, httpMethod, statusCode, 0, startTimeMs, totalTime, bytesSent, bytesReceived, appData));
            if((long)statusCode >= 400L) {
                Measurements.addHttpError(url, httpMethod, statusCode, responseBody, params);
            }
        }

    }

    private static void noticeNetworkFailureDelegate(String url, String httpMethod, long startTime, long endTime, NetworkFailure failure, String message) {
        float durationInSeconds = (float)(endTime - startTime) / 1000.0F;
        TransactionState ts = new TransactionState();
        TransactionStateUtil.inspectAndInstrument(ts, url, httpMethod);
        ts.setErrorCode(failure.getErrorCode());
        TransactionData transactionData = ts.end();
        Map<String, String> params = new TreeMap();
        params.put("content_length", "0");
        params.put("content_type", "text/html");
        TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), startTime, (double)durationInSeconds, transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
        if(ts.getErrorCode() != 0) {
            Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), message, params);
        } else {
            Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), message, params);
        }

    }

    public static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, NetworkFailure failure, String message) {
        noticeNetworkFailureDelegate(url, httpMethod, startTime, endTime, failure, message);
    }

    public static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, NetworkFailure failure) {
        noticeNetworkFailure(url, httpMethod, startTime, endTime, failure, "");
    }

    public static void noticeNetworkFailure(String url, String httpMethod, long startTime, long endTime, Exception e) {
        checkEmpty(url, "noticeHttpException: url must not be empty.");
        NetworkFailure failure = NetworkFailure.exceptionToNetworkFailure(e);
        noticeNetworkFailure(url, httpMethod, startTime, endTime, failure, e.getMessage());
    }

    /** @deprecated */
    @Deprecated
    public static void noticeNetworkFailure(String url, long startTime, long endTime, NetworkFailure failure) {
        noticeNetworkFailure(url, UNKNOWN_HTTP_REQUEST_TYPE, startTime, endTime, failure);
    }

    /** @deprecated */
    @Deprecated
    public static void noticeNetworkFailure(String url, long startTime, long endTime, Exception e) {
        noticeNetworkFailure(url, UNKNOWN_HTTP_REQUEST_TYPE, startTime, endTime, e);
    }

    private static void checkNull(Object object, String message) {
        if(object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void checkEmpty(String string, String message) {
        checkNull(string, message);
        if(string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean checkNegative(int number, String message) {
        if(number < 0) {
            log.error(message);
            return true;
        } else {
            return false;
        }
    }

    public static void crashNow() {
        crashNow("This is a demonstration crash courtesy of New Relic");
    }

    public static void crashNow(String message) {
        throw new RuntimeException(message);
    }

    public static boolean setAttribute(String name, String value) {
        return AnalyticsControllerImpl.getInstance().setAttribute(name, value);
    }

    public static boolean setAttribute(String name, float value) {
        return AnalyticsControllerImpl.getInstance().setAttribute(name, value);
    }

    public static boolean setAttribute(String name, boolean value) {
        return AnalyticsControllerImpl.getInstance().setAttribute(name, value);
    }

    public static boolean incrementAttribute(String name) {
        return AnalyticsControllerImpl.getInstance().incrementAttribute(name, 1.0F);
    }

    public static boolean incrementAttribute(String name, float value) {
        return AnalyticsControllerImpl.getInstance().incrementAttribute(name, value);
    }

    public static boolean removeAttribute(String name) {
        return AnalyticsControllerImpl.getInstance().removeAttribute(name);
    }

    public static boolean removeAllAttributes() {
        return AnalyticsControllerImpl.getInstance().removeAllAttributes();
    }

    public static boolean setUserId(String userId) {
        return AnalyticsControllerImpl.getInstance().setAttribute(AnalyticAttribute.USER_ID_ATTRIBUTE, userId);
    }

    /** @deprecated */
    @Deprecated
    public static boolean recordEvent(String name, Map<String, Object> eventAttributes) {
        if(null == eventAttributes) {
            eventAttributes = new HashMap();
        }

        return AnalyticsControllerImpl.getInstance().recordEvent(name, (Map)eventAttributes);
    }

    public static boolean recordCustomEvent(String eventType, Map<String, Object> eventAttributes) {
        if(null == eventAttributes) {
            eventAttributes = new HashMap();
        }

        return AnalyticsControllerImpl.getInstance().recordCustomEvent(eventType, (Map)eventAttributes);
    }

    public static boolean recordCustomEvent(String eventType, String eventName, Map<String, Object> eventAttributes) {
        if(null == eventAttributes) {
            eventAttributes = new HashMap();
        }

        if(eventName != null && !eventName.isEmpty()) {
            ((Map)eventAttributes).put("name", eventName);
        }

        return recordCustomEvent(eventType, (Map)eventAttributes);
    }

    public static boolean recordBreadcrumb(String breadcrumbName) {
        return recordBreadcrumb(breadcrumbName, (Map)null);
    }

    public static boolean recordBreadcrumb(String breadcrumbName, Map<String, Object> attributes) {
        if(null == attributes) {
            attributes = new HashMap();
        }

        if(breadcrumbName != null && !breadcrumbName.isEmpty()) {
            ((Map)attributes).put("name", breadcrumbName);
        }

        return AnalyticsControllerImpl.getInstance().recordBreadcrumb(breadcrumbName, (Map)attributes);
    }

    public static boolean recordHandledException(Exception exceptionToHandle) {
        HashMap<String, Object> exceptionAttributes = new HashMap();
        return recordHandledException(exceptionToHandle, exceptionAttributes);
    }

    public static boolean recordHandledException(Exception exceptionToHandle, Map<String, Object> exceptionAttributes) {
        if(exceptionAttributes == null) {
            exceptionAttributes = new HashMap();
        }

        return AgentDataController.sendAgentData(exceptionToHandle, (Map)exceptionAttributes);
    }

    public static void setMaxEventPoolSize(int maxSize) {
        AnalyticsControllerImpl.getInstance().setMaxEventPoolSize(maxSize);
    }

    public static void setMaxEventBufferTime(int maxBufferTimeInSec) {
        AnalyticsControllerImpl.getInstance().setMaxEventBufferTime(maxBufferTimeInSec);
    }

    public static String currentSessionId() {
        return agentConfiguration.getSessionID();
    }
}
