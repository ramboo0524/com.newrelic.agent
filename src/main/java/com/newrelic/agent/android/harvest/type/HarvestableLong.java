//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.google.gson.JsonPrimitive;

public class HarvestableLong extends HarvestableValue {
    private long value;

    public HarvestableLong() {
    }

    public HarvestableLong(long value) {
        this();
        this.value = value;
    }

    public JsonPrimitive asJsonPrimitive() {
        return new JsonPrimitive(this.value);
    }
}
