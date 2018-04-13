//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.crash;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentImpl;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsEvent;
import com.newrelic.agent.android.harvest.ActivityHistory;
import com.newrelic.agent.android.harvest.DataToken;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.crash.ApplicationInfo;
import com.newrelic.agent.android.harvest.crash.DeviceInfo;
import com.newrelic.agent.android.harvest.crash.ExceptionInfo;
import com.newrelic.agent.android.harvest.crash.ThreadInfo;
import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Crash extends HarvestableObject {
    public static final int PROTOCOL_VERSION = 1;
    public static final int MAX_UPLOAD_COUNT = 3;
    private final UUID uuid;
    private final String buildId;
    private final long timestamp;
    private final String appToken;
    private boolean analyticsEnabled;
    private DeviceInfo deviceInfo;
    private ApplicationInfo applicationInfo;
    private ExceptionInfo exceptionInfo;
    private List<ThreadInfo> threads;
    private ActivityHistory activityHistory;
    private Set<AnalyticAttribute> sessionAttributes;
    private Collection<AnalyticsEvent> events;
    private int uploadCount;

    public Crash(UUID uuid, String buildId, long timestamp) {
        AgentImpl agentImpl = Agent.getImpl();
        this.uuid = uuid;
        this.buildId = buildId;
        this.timestamp = timestamp;
        this.appToken = CrashReporter.getInstance().getAgentConfiguration().getApplicationToken();
        this.deviceInfo = new DeviceInfo(agentImpl.getDeviceInformation(), agentImpl.getEnvironmentInformation());
        this.applicationInfo = new ApplicationInfo(agentImpl.getApplicationInformation());
        this.exceptionInfo = new ExceptionInfo();
        this.threads = new ArrayList<>();
        this.activityHistory = new ActivityHistory(new ArrayList());
        this.sessionAttributes = new HashSet<>();
        this.events = new HashSet<>();
        this.analyticsEnabled = true;
        this.uploadCount = 0;
    }

    public Crash(Throwable throwable) {
        this(throwable, new HashSet(), new HashSet(), false);
    }

    public Crash(Throwable throwable, Set<AnalyticAttribute> sessionAttributes, Collection<AnalyticsEvent> events, boolean analyticsEnabled) {
        AgentImpl agentImpl = Agent.getImpl();
        Throwable cause = getRootCause(throwable);
        this.uuid = UUID.randomUUID();
        this.buildId = getBuildId();
        this.timestamp = System.currentTimeMillis();
        this.appToken = CrashReporter.getInstance().getAgentConfiguration().getApplicationToken();
        this.deviceInfo = new DeviceInfo(agentImpl.getDeviceInformation(), agentImpl.getEnvironmentInformation());
        this.applicationInfo = new ApplicationInfo(agentImpl.getApplicationInformation());
        this.exceptionInfo = new ExceptionInfo(cause);
        this.threads = ThreadInfo.extractThreads(cause);
        this.activityHistory = TraceMachine.getActivityHistory();
        this.sessionAttributes = sessionAttributes;
        this.events = events;
        this.analyticsEnabled = analyticsEnabled;
        this.uploadCount = 0;
    }

    public static String getBuildId() {
        return Agent.getBuildId();
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public ExceptionInfo getExceptionInfo() {
        return this.exceptionInfo;
    }

    public void setSessionAttributes(Set<AnalyticAttribute> sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }

    public Set<AnalyticAttribute> getSessionAttributes() {
        return this.sessionAttributes;
    }

    public void setAnalyticsEvents(Collection<AnalyticsEvent> events) {
        this.events = events;
    }

    public JsonObject asJsonObject() {
        JsonObject data = new JsonObject();
        data.add("protocolVersion", new JsonPrimitive(PROTOCOL_VERSION));
        data.add("platform", new JsonPrimitive("Android"));
        data.add("uuid", SafeJsonPrimitive.factory(this.uuid.toString()));
        data.add("buildId", SafeJsonPrimitive.factory(this.buildId));
        data.add("timestamp", SafeJsonPrimitive.factory(this.timestamp));
        data.add("appToken", SafeJsonPrimitive.factory(this.appToken));
        data.add("deviceInfo", this.deviceInfo.asJsonObject());
        data.add("appInfo", this.applicationInfo.asJsonObject());
        data.add("exception", this.exceptionInfo.asJsonObject());
        data.add("threads", this.getThreadsAsJson());
        data.add("activityHistory", this.activityHistory.asJsonArrayWithoutDuration());
        JsonObject attributeObject = new JsonObject();
        if(this.sessionAttributes != null) {
            Iterator var3 = this.sessionAttributes.iterator();

            while(var3.hasNext()) {
                AnalyticAttribute attribute = (AnalyticAttribute)var3.next();
                attributeObject.add(attribute.getName(), attribute.asJsonElement());
            }
        }

        data.add("sessionAttributes", attributeObject);
        JsonArray eventArray = new JsonArray();
        if(this.events != null) {
            Iterator var7 = this.events.iterator();

            while(var7.hasNext()) {
                AnalyticsEvent event = (AnalyticsEvent)var7.next();
                eventArray.add(event.asJsonObject());
            }
        }

        data.add("analyticsEvents", eventArray);
        DataToken dataToken = Harvest.getHarvestConfiguration().getDataToken();
        if(dataToken != null) {
            data.add("dataToken", dataToken.asJsonArray());
        }

        return data;
    }

    public static Crash crashFromJsonString(String json) {
        JsonElement element = (new JsonParser()).parse(json);
        JsonObject crashObject = element.getAsJsonObject();
        String uuid = crashObject.get("uuid").getAsString();
        String buildIdentifier = crashObject.get("buildId").getAsString();
        long timestamp = crashObject.get("timestamp").getAsLong();
        Crash crash = new Crash(UUID.fromString(uuid), buildIdentifier, timestamp);
        crash.deviceInfo = DeviceInfo.newFromJson(crashObject.get("deviceInfo").getAsJsonObject());
        crash.applicationInfo = ApplicationInfo.newFromJson(crashObject.get("appInfo").getAsJsonObject());
        crash.exceptionInfo = ExceptionInfo.newFromJson(crashObject.get("exception").getAsJsonObject());
        crash.threads = ThreadInfo.newListFromJson(crashObject.get("threads").getAsJsonArray());
        crash.activityHistory = ActivityHistory.newFromJson(crashObject.get("activityHistory").getAsJsonArray());
        crash.analyticsEnabled = crashObject.has("sessionAttributes") || crashObject.has("analyticsEvents");
        if(crashObject.has("sessionAttributes")) {
            Set<AnalyticAttribute> sessionAttributes = AnalyticAttribute.newFromJson(crashObject.get("sessionAttributes").getAsJsonObject());
            crash.setSessionAttributes(sessionAttributes);
        }

        if(crashObject.has("analyticsEvents")) {
            Collection<AnalyticsEvent> events = AnalyticsEvent.newFromJson(crashObject.get("analyticsEvents").getAsJsonArray());
            crash.setAnalyticsEvents(events);
        }

        if(crashObject.has("uploadCount")) {
            crash.uploadCount = crashObject.get("uploadCount").getAsInt();
        }

        return crash;
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

    protected JsonArray getThreadsAsJson() {
        JsonArray data = new JsonArray();
        if(this.threads != null) {
            Iterator var2 = this.threads.iterator();

            while(var2.hasNext()) {
                ThreadInfo thread = (ThreadInfo)var2.next();
                data.add(thread.asJsonObject());
            }
        }

        return data;
    }

    public void incrementUploadCount() {
        ++this.uploadCount;
    }

    public int getUploadCount() {
        return this.uploadCount;
    }

    public boolean isStale() {
        return this.uploadCount >= 3;
    }
}
