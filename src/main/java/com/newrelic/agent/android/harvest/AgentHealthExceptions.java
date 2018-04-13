//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentHealthExceptions extends HarvestableObject {
    private static final JsonArray keyArray = new JsonArray();
    private final Map<String, AgentHealthException> agentHealthExceptions = new ConcurrentHashMap<>();

    public AgentHealthExceptions() {
        keyArray.add(new JsonPrimitive("ExceptionClass"));
        keyArray.add(new JsonPrimitive("Message"));
        keyArray.add(new JsonPrimitive("ThreadName"));
        keyArray.add(new JsonPrimitive("CallStack"));
        keyArray.add(new JsonPrimitive("Count"));
        keyArray.add(new JsonPrimitive("Extras"));
    }

    public void add(AgentHealthException exception) {
        String aggregationKey = this.getKey(exception);
        Map var3 = this.agentHealthExceptions;
        synchronized(this.agentHealthExceptions) {
            AgentHealthException healthException = this.agentHealthExceptions.get(aggregationKey);
            if(healthException == null) {
                this.agentHealthExceptions.put(aggregationKey, exception);
            } else {
                healthException.increment();
            }

        }
    }

    public void clear() {
        Map var1 = this.agentHealthExceptions;
        synchronized(this.agentHealthExceptions) {
            this.agentHealthExceptions.clear();
        }
    }

    public boolean isEmpty() {
        return this.agentHealthExceptions.isEmpty();
    }

    public Map<String, AgentHealthException> getAgentHealthExceptions() {
        return this.agentHealthExceptions;
    }

    public final String getKey(AgentHealthException exception) {
        String key = this.getClass().getName();
        if(exception != null) {
            key = exception.getExceptionClass() + exception.getStackTrace()[0].toString();
        }

        return key;
    }

    public JsonObject asJsonObject() {
        JsonObject exceptions = new JsonObject();
        JsonArray data = new JsonArray();
        Iterator var3 = this.agentHealthExceptions.values().iterator();

        while(var3.hasNext()) {
            AgentHealthException exception = (AgentHealthException)var3.next();
            data.add(exception.asJsonArray());
        }

        exceptions.add("Type", new JsonPrimitive("AgentErrors"));
        exceptions.add("Keys", keyArray);
        exceptions.add("Data", data);
        return exceptions;
    }
}
