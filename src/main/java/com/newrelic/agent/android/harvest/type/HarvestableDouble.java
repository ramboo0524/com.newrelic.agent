//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.google.gson.JsonPrimitive;

public class HarvestableDouble extends HarvestableValue {
    private double value;

    public HarvestableDouble() {
    }

    public HarvestableDouble(double value) {
        this();
        this.value = value;
    }

    public JsonPrimitive asJsonPrimitive() {
        return new JsonPrimitive(this.value);
    }
}
