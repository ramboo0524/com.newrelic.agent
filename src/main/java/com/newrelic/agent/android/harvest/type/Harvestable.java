//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public interface Harvestable {
    Harvestable.Type getType();

    JsonElement asJson();

    JsonObject asJsonObject();

    JsonArray asJsonArray();

    JsonPrimitive asJsonPrimitive();

    String toJsonString();

    enum Type {
        OBJECT,
        ARRAY,
        VALUE
    }
}
