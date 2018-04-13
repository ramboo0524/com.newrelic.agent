//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.newrelic.agent.android.harvest.type.Harvestable.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

public class BaseHarvestable implements Harvestable {
    private final Type type;
    protected static final java.lang.reflect.Type GSON_STRING_MAP_TYPE = (new TypeToken<Map>() {
    }).getType();

    public BaseHarvestable(Type type) {
        this.type = type;
    }

    public JsonElement asJson() {
        switch(this.type) {
            case OBJECT:
                return this.asJsonObject();
            case ARRAY:
                return this.asJsonArray();
            case VALUE:
                return this.asJsonPrimitive();
            default:
                return null;
        }
    }

    public Type getType() {
        return this.type;
    }

    public String toJsonString() {
        return this.asJson().toString();
    }

    public JsonArray asJsonArray() {
        return null;
    }

    public JsonObject asJsonObject() {
        return null;
    }

    public JsonPrimitive asJsonPrimitive() {
        return null;
    }

    protected void notEmpty(String argument) {
        if(argument == null || argument.length() == 0) {
            throw new IllegalArgumentException("Missing Harvestable field.");
        }
    }

    protected void notNull(Object argument) {
        if(argument == null) {
            throw new IllegalArgumentException("Null field in Harvestable object");
        }
    }

    protected String optional(String argument) {
        return argument == null?"":argument;
    }
}
