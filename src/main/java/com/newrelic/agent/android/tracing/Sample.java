//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;

public class Sample extends HarvestableArray {
    private long timestamp;
    private SampleValue sampleValue;
    private SampleType type;

    public Sample(SampleType type) {
        this.setSampleType(type);
        this.setTimestamp(System.currentTimeMillis());
    }

    public Sample(long timestamp) {
        this.setTimestamp(timestamp);
    }

    public Sample(long timestamp, SampleValue sampleValue) {
        this.setTimestamp(timestamp);
        this.setSampleValue(sampleValue);
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public SampleValue getSampleValue() {
        return this.sampleValue;
    }

    public void setSampleValue(SampleValue sampleValue) {
        this.sampleValue = sampleValue;
    }

    public void setSampleValue(double value) {
        this.sampleValue = new SampleValue(value);
    }

    public void setSampleValue(long value) {
        this.sampleValue = new SampleValue(value);
    }

    public Number getValue() {
        return this.sampleValue.getValue();
    }

    public Sample.SampleType getSampleType() {
        return this.type;
    }

    public void setSampleType(Sample.SampleType type) {
        this.type = type;
    }

    public JsonArray asJsonArray() {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(SafeJsonPrimitive.factory(Long.valueOf(this.timestamp)));
        jsonArray.add(SafeJsonPrimitive.factory(this.sampleValue.getValue()));
        return jsonArray;
    }

    public enum SampleType {
        MEMORY,
        CPU
    }
}
