//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.activity.NamedActivity;
import com.newrelic.agent.android.harvest.ActivitySighting;
import com.newrelic.agent.android.harvest.ConnectInformation;
import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.tracing.Sample.SampleType;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ActivityTrace extends HarvestableArray {
    public static final String TRACE_VERSION = "1.0";
    public static final int MAX_TRACES = 2000;
    public Trace rootTrace;
    private final ConcurrentHashMap<UUID, Trace> traces = new ConcurrentHashMap<>();
    private int traceCount = 0;
    private final Set missingChildren = new HashSet();
    private NamedActivity measuredActivity;
    private long reportAttemptCount = 0L;
    public long lastUpdatedAt;
    public long startedAt;
    public ActivitySighting previousActivity;
    private boolean complete = false;
    private final HashMap<String, String> params = new HashMap<>();
    private Map<SampleType, Collection<Sample>> vitals;
    private final AgentLog log = AgentLogManager.getAgentLog();
    public final Metric networkCountMetric = new Metric("Mobile/Activity/Network/<activity>/Count");
    public final Metric networkTimeMetric = new Metric("Mobile/Activity/Network/<activity>/Time");
    private static final String SIZE_NORMAL = "NORMAL";
    private static final HashMap<String, String> ENVIRONMENT_TYPE = new HashMap<String, String>() {
        {
            this.put("type", "ENVIRONMENT");
        }
    };
    private static final HashMap<String, String> VITALS_TYPE = new HashMap<String, String>() {
        {
            this.put("type", "VITALS");
        }
    };
    private static final HashMap<String, String> ACTIVITY_HISTORY_TYPE = new HashMap<String, String>() {
        {
            this.put("type", "ACTIVITY_HISTORY");
        }
    };

    public ActivityTrace() {
    }

    public ActivityTrace(Trace rootTrace) {
        this.rootTrace = rootTrace;
        this.lastUpdatedAt = rootTrace.entryTimestamp;
        this.startedAt = this.lastUpdatedAt;
        this.params.put("traceVersion", TRACE_VERSION);
        this.params.put("type", "ACTIVITY");
        this.measuredActivity = (NamedActivity)Measurements.startActivity(rootTrace.displayName);
        this.measuredActivity.setStartTime(rootTrace.entryTimestamp);
    }

    public String getId() {
        return this.rootTrace == null?null:this.rootTrace.myUUID.toString();
    }

    public void addTrace(Trace trace) {
        this.missingChildren.add(trace.myUUID);
        this.lastUpdatedAt = System.currentTimeMillis();
    }

    public void addCompletedTrace(Trace trace) {
        if(trace.getType() == TraceType.NETWORK) {
            this.networkCountMetric.sample(1.0D);
            this.networkTimeMetric.sample((double)trace.getDurationAsSeconds());
            if(this.rootTrace != null) {
                this.rootTrace.childExclusiveTime += trace.getDurationAsMilliseconds();
            }
        }

        trace.traceMachine = null;
        this.missingChildren.remove(trace.myUUID);
        if(this.traceCount > MAX_TRACES) {
            this.log.verbose("Maximum trace limit reached, discarding trace " + trace.myUUID);
        } else {
            this.traces.put(trace.myUUID, trace);
            ++this.traceCount;
            if(trace.exitTimestamp > this.rootTrace.exitTimestamp) {
                this.rootTrace.exitTimestamp = trace.exitTimestamp;
            }

            this.log.verbose("Added trace " + trace.myUUID.toString() + " missing children: " + this.missingChildren.size());
            this.lastUpdatedAt = System.currentTimeMillis();
        }
    }

    public boolean hasMissingChildren() {
        return !this.missingChildren.isEmpty();
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void discard() {
        this.log.debug("Discarding trace of " + this.rootTrace.displayName + ":" + this.rootTrace.myUUID.toString() + "(" + this.traces.size() + " traces)");
        this.rootTrace.traceMachine = null;
        this.complete = true;
        Measurements.endActivityWithoutMeasurement(this.measuredActivity);
    }

    public void complete() {
        this.log.debug("Completing trace of " + this.rootTrace.displayName + ":" + this.rootTrace.myUUID.toString() + "(" + this.traces.size() + " traces)");
        if(this.rootTrace.exitTimestamp == 0L) {
            this.rootTrace.exitTimestamp = System.currentTimeMillis();
        }

        if(this.traces.isEmpty()) {
            this.rootTrace.traceMachine = null;
            this.complete = true;
            Measurements.endActivityWithoutMeasurement(this.measuredActivity);
        } else {
            this.measuredActivity.setEndTime(this.rootTrace.exitTimestamp);
            Measurements.endActivity(this.measuredActivity);
            this.rootTrace.traceMachine = null;
            this.complete = true;
            TaskQueue.queue(this);
        }
    }

    public Map<UUID, Trace> getTraces() {
        return this.traces;
    }

    public JsonArray asJsonArray() {
        JsonArray tree = new JsonArray();
        if(!this.complete) {
            this.log.verbose("Attempted to serialize trace " + this.rootTrace.myUUID.toString() + " but it has yet to be finalized");
            return null;
        } else {
            tree.add((new Gson()).toJsonTree(this.params, GSON_STRING_MAP_TYPE));
            tree.add(SafeJsonPrimitive.factory(Long.valueOf(this.rootTrace.entryTimestamp)));
            tree.add(SafeJsonPrimitive.factory(Long.valueOf(this.rootTrace.exitTimestamp)));
            tree.add(SafeJsonPrimitive.factory(this.rootTrace.displayName));
            JsonArray segments = new JsonArray();
            segments.add(this.getEnvironment());
            segments.add(this.traceToTree(this.rootTrace));
            segments.add(this.getVitalsAsJson());
            if(this.previousActivity != null) {
                segments.add(this.getPreviousActivityAsJson());
            }

            tree.add(segments);
            return tree;
        }
    }

    private JsonArray traceToTree(Trace trace) {
        JsonArray segment = new JsonArray();
        trace.prepareForSerialization();
        segment.add((new Gson()).toJsonTree(trace.getParams(), GSON_STRING_MAP_TYPE));
        segment.add(SafeJsonPrimitive.factory(Long.valueOf(trace.entryTimestamp)));
        segment.add(SafeJsonPrimitive.factory(Long.valueOf(trace.exitTimestamp)));
        segment.add(SafeJsonPrimitive.factory(trace.displayName));
        JsonArray threadData = new JsonArray();
        threadData.add(SafeJsonPrimitive.factory(Long.valueOf(trace.threadId)));
        threadData.add(SafeJsonPrimitive.factory(trace.threadName));
        segment.add(threadData);
        if(trace.getChildren().isEmpty()) {
            segment.add(new JsonArray());
        } else {
            JsonArray children = new JsonArray();
            Iterator var5 = trace.getChildren().iterator();

            while(var5.hasNext()) {
                UUID traceUUID = (UUID)var5.next();
                Trace childTrace = (Trace)this.traces.get(traceUUID);
                if(childTrace != null) {
                    children.add(this.traceToTree(childTrace));
                }
            }

            segment.add(children);
        }

        return segment;
    }

    private JsonArray getEnvironment() {
        JsonArray environment = new JsonArray();
        environment.add((new Gson()).toJsonTree(ENVIRONMENT_TYPE, GSON_STRING_MAP_TYPE));
        ConnectInformation connectInformation = new ConnectInformation(Agent.getApplicationInformation(), Agent.getDeviceInformation());
        environment.addAll(connectInformation.asJsonArray());
        HashMap<String, String> environmentParams = new HashMap<>();
        environmentParams.put("size", SIZE_NORMAL);
        environment.add((new Gson()).toJsonTree(environmentParams, GSON_STRING_MAP_TYPE));
        return environment;
    }

    public void setVitals(Map<SampleType, Collection<Sample>> vitals) {
        this.vitals = vitals;
    }

    private JsonArray getVitalsAsJson() {
        JsonArray vitalsJson = new JsonArray();
        vitalsJson.add((new Gson()).toJsonTree(VITALS_TYPE, GSON_STRING_MAP_TYPE));
        JsonObject vitalsMap = new JsonObject();
        if(this.vitals != null) {
            Iterator var3 = this.vitals.entrySet().iterator();

            while(var3.hasNext()) {
                Entry<SampleType, Collection<Sample>> entry = (Entry)var3.next();
                JsonArray samplesJsonArray = new JsonArray();
                Iterator var6 = ((Collection)entry.getValue()).iterator();

                while(var6.hasNext()) {
                    Sample sample = (Sample)var6.next();
                    if(sample.getTimestamp() <= this.lastUpdatedAt) {
                        samplesJsonArray.add(sample.asJsonArray());
                    }
                }

                vitalsMap.add(((SampleType)entry.getKey()).toString(), samplesJsonArray);
            }
        }

        vitalsJson.add(vitalsMap);
        return vitalsJson;
    }

    private JsonArray getPreviousActivityAsJson() {
        JsonArray historyJson = new JsonArray();
        historyJson.add((new Gson()).toJsonTree(ACTIVITY_HISTORY_TYPE, GSON_STRING_MAP_TYPE));
        historyJson.addAll(this.previousActivity.asJsonArray());
        return historyJson;
    }

    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public long getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    public long getReportAttemptCount() {
        return this.reportAttemptCount;
    }

    public void incrementReportAttemptCount() {
        ++this.reportAttemptCount;
    }

    public String getActivityName() {
        String activityName = "<activity>";
        if(this.rootTrace != null) {
            activityName = this.rootTrace.displayName;
            if(activityName != null) {
                int hashIndex = activityName.indexOf("#");
                if(hashIndex > 0) {
                    activityName = activityName.substring(0, hashIndex);
                }
            }
        }

        return activityName;
    }
}
