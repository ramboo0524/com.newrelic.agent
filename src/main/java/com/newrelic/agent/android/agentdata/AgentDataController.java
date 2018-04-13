//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.agentdata;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.agentdata.builder.AgentDataBuilder;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.crash.Crash;
import com.newrelic.agent.android.harvest.crash.ApplicationInfo;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.util.ExceptionHelper;
import com.google.flatbuffers.FlatBufferBuilder;
import com.newrelic.mobile.fbs.AgentDataBundle;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AgentDataController {
    protected static final AgentConfiguration agentConfiguration = new AgentConfiguration();
    private static final AgentLog log = AgentLogManager.getAgentLog();

    public AgentDataController() {
    }

    static FlatBufferBuilder buildAgentDataFromHandledException(Exception e, Map<String, Object> exceptionAttributes) {
        Map<String, Object> handledException = new HashMap<>();
        Map<String, Object> sessionAttributes = new HashMap<>();
        ApplicationInfo applicationInfo = new ApplicationInfo(Agent.getApplicationInformation());

        UUID buildUuid;
        try {
            buildUuid = UUID.fromString(Crash.getBuildId());
        } catch (IllegalArgumentException var12) {
            buildUuid = UUID.randomUUID();
            ExceptionHelper.recordSupportabilityMetric(var12, "RandomUUID");
        }

        handledException.put("appUuidHigh", buildUuid.getLeastSignificantBits());
        handledException.put("appUuidLow", buildUuid.getMostSignificantBits());
        handledException.put("appVersion", applicationInfo.getApplicationVersion());
        handledException.put("appBuild", applicationInfo.getApplicationBuild());
        handledException.put("sessionId", agentConfiguration.getSessionID());
        handledException.put("timestampMs", System.currentTimeMillis());
        handledException.put("message", e.getMessage());
        handledException.put("cause", getRootCause(e).toString());
        handledException.put("name", e.getClass().toString());
        handledException.put("thread", threadSetFromStackElements(e.getStackTrace()));
        handledException.putAll(exceptionAttributes);
        Iterator var6 = AnalyticsControllerImpl.getInstance().getSessionAttributes().iterator();

        while(var6.hasNext()) {
            AnalyticAttribute attribute = (AnalyticAttribute)var6.next();
            switch(attribute.getAttributeDataType()) {
                case STRING:
                    sessionAttributes.put(attribute.getName(), attribute.getStringValue());
                    break;
                case FLOAT:
                    sessionAttributes.put(attribute.getName(), attribute.getFloatValue());
                    break;
                case BOOLEAN:
                    sessionAttributes.put(attribute.getName(), attribute.getBooleanValue());
            }
        }

        long sessionDuration = Agent.getImpl().getSessionDurationMillis();
        if(0L == sessionDuration) {
            log.error("Harvest instance is not running! Session duration will be invalid");
        } else {
            sessionAttributes.put("timeSinceLoad", (float) sessionDuration / 1000.0F);
        }

        sessionAttributes.putAll(exceptionAttributes);
        Set<Map<String, Object>> agentData = new HashSet<>();
        agentData.add(handledException);
        FlatBufferBuilder flat = AgentDataBuilder.startAndFinishAgentData(sessionAttributes, agentData);
        return flat;
    }

    static FlatBufferBuilder buildAgentDataFromHandledException(Exception e) {
        return buildAgentDataFromHandledException(e, new HashMap());
    }

    protected static Throwable getRootCause(Throwable throwable) {
        try {
            if(throwable != null) {
                Throwable cause = throwable.getCause();
                if(cause == null) {
                    return throwable;
                }

                return getRootCause(cause);
            }
        } catch (Exception var2) {
            if(throwable != null) {
                return throwable;
            }
        }

        return new Throwable("Unknown cause");
    }

    protected static List<Map<String, Object>> threadSetFromStackElements(StackTraceElement[] ste) {
        List<Map<String, Object>> thread = new ArrayList<>();
        StackTraceElement[] var2 = ste;
        int var3 = ste.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement ele = var2[var4];
            Map<String, Object> frame = new LinkedHashMap<>();
            frame.put("className", ele.getClassName());
            frame.put("methodName", ele.getMethodName());
            frame.put("lineNumber", ele.getLineNumber());
            frame.put("fileName", ele.getFileName());
            thread.add(frame);
        }

        return thread;
    }

    public static boolean sendAgentData(Exception e, Map<String, Object> attributes) {
        if(FeatureFlag.featureEnabled(FeatureFlag.HandledExceptions)) {
            try {
                FlatBufferBuilder flat = buildAgentDataFromHandledException(e, attributes);
                ByteBuffer byteBuffer = flat.dataBuffer().slice();
                byte[] modifiedBytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(modifiedBytes);
                log.audit(AgentDataBuilder.toJsonString(AgentDataBundle.getRootAsAgentDataBundle(ByteBuffer.wrap(modifiedBytes)), 0));
                boolean reported = AgentDataReporter.reportAgentData(modifiedBytes);
                if(!reported) {
                    log.error("HandledException: exception " + e.getClass().getName() + " failed to record data.");
                }

                return reported;
            } catch (Exception var6) {
                log.error("HandledException: exception " + e.getClass().getName() + " failed to record data.");
            }
        }

        return false;
    }
}
