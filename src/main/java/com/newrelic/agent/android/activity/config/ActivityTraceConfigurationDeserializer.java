//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.activity.config;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

public class ActivityTraceConfigurationDeserializer implements JsonDeserializer<com.newrelic.agent.android.activity.config.ActivityTraceConfiguration> {
    private final AgentLog log = AgentLogManager.getAgentLog();

    public ActivityTraceConfigurationDeserializer() {
    }

    public com.newrelic.agent.android.activity.config.ActivityTraceConfiguration deserialize(JsonElement root, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        com.newrelic.agent.android.activity.config.ActivityTraceConfiguration configuration = new com.newrelic.agent.android.activity.config.ActivityTraceConfiguration();
        if(!root.isJsonArray()) {
            this.error("Expected root element to be an array.");
            return null;
        } else {
            JsonArray array = root.getAsJsonArray();
            if(array.size() != 2) {
                this.error("Root array must contain 2 elements.");
                return null;
            } else {
                Integer maxTotalTraceCount = this.getInteger(array.get(0));
                if(maxTotalTraceCount == null) {
                    return null;
                } else if(maxTotalTraceCount < 0) {
                    this.error("The first element of the root array must not be negative.");
                    return null;
                } else {
                    configuration.setMaxTotalTraceCount(maxTotalTraceCount);
                    return configuration;
                }
            }
        }
    }

    private Integer getInteger(JsonElement element) {
        if(!element.isJsonPrimitive()) {
            this.error("Expected an integer.");
            return null;
        } else {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if(!primitive.isNumber()) {
                this.error("Expected an integer.");
                return null;
            } else {
                int value = primitive.getAsInt();
                if(value < 0) {
                    this.error("Integer value must not be negative");
                    return null;
                } else {
                    return value;
                }
            }
        }
    }

    private void error(String message) {
        this.log.error("ActivityTraceConfigurationDeserializer: " + message);
    }
}
