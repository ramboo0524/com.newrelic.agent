//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;

public class ActivitySighting extends HarvestableArray {
    private String name;
    private final long timestampMs;
    private long durationMs = 0L;

    public ActivitySighting(long timestampMs, String name) {
        this.timestampMs = timestampMs;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        synchronized(this) {
            this.name = name;
        }
    }

    public long getTimestampMs() {
        return this.timestampMs;
    }

    public long getDuration() {
        return this.durationMs;
    }

    public void end(long endTimestampMs) {
        synchronized(this) {
            this.durationMs = endTimestampMs - this.timestampMs;
        }
    }

    public JsonArray asJsonArray() {
        JsonArray data = new JsonArray();
        synchronized(this) {
            data.add(SafeJsonPrimitive.factory(this.name));
            data.add(SafeJsonPrimitive.factory(this.timestampMs));
            data.add(SafeJsonPrimitive.factory(this.durationMs));
            return data;
        }
    }

    public JsonArray asJsonArrayWithoutDuration() {
        JsonArray data = new JsonArray();
        synchronized(this) {
            data.add(SafeJsonPrimitive.factory(this.timestampMs));
            data.add(SafeJsonPrimitive.factory(this.name));
            return data;
        }
    }

    public static ActivitySighting newFromJson(JsonArray jsonArray) {
        return new ActivitySighting(jsonArray.get(0).getAsLong(), jsonArray.get(1).getAsString());
    }
}
