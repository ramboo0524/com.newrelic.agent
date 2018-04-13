//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class AnalyticsEvent extends HarvestableObject {
    private final AgentLog log;
    private String name;
    private long timestamp;
    private AnalyticsEventCategory category;
    private String eventType;
    private Set<AnalyticAttribute> attributeSet;

    protected AnalyticsEvent(String name) {
        this(name, AnalyticsEventCategory.Custom, null, null);
    }

    protected AnalyticsEvent(String name, AnalyticsEventCategory category) {
        this(name, category, null, null);
    }

    protected AnalyticsEvent(String name, AnalyticsEventCategory category, String eventType, Set<com.newrelic.agent.android.analytics.AnalyticAttribute> initialAttributeSet) {
        this(name, category, eventType, System.currentTimeMillis(), initialAttributeSet);
    }

    private AnalyticsEvent(String name, AnalyticsEventCategory category, String eventType, long timeStamp, Set<com.newrelic.agent.android.analytics.AnalyticAttribute> initialAttributeSet) {
        this.log = AgentLogManager.getAgentLog();
        this.attributeSet = new HashSet<AnalyticAttribute>();
        this.name = name;
        if(category == null) {
            this.category = AnalyticsEventCategory.Custom;
        } else {
            this.category = category;
        }

        if(eventType == null) {
            this.eventType = "Mobile";
        } else {
            this.eventType = eventType;
        }

        this.timestamp = timeStamp;
        if(initialAttributeSet != null) {
            Iterator var7 = initialAttributeSet.iterator();

            while(var7.hasNext()) {
                com.newrelic.agent.android.analytics.AnalyticAttribute attribute = (com.newrelic.agent.android.analytics.AnalyticAttribute)var7.next();
                this.attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute(attribute));
            }
        }

        if(name != null) {
            this.attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute("name", this.name));
        }

        this.attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute("timestamp", String.valueOf(this.timestamp)));
        this.attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute("category", this.category.name()));
        this.attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute("eventType", this.eventType));
    }

    public void addAttributes(Set<com.newrelic.agent.android.analytics.AnalyticAttribute> attributeSet) {
        if(attributeSet != null) {
            Iterator var2 = attributeSet.iterator();

            while(var2.hasNext()) {
                com.newrelic.agent.android.analytics.AnalyticAttribute attribute = (com.newrelic.agent.android.analytics.AnalyticAttribute)var2.next();
                if(!this.attributeSet.add(attribute)) {
                    this.log.error("Failed to add attribute " + attribute.getName() + " to event " + this.getName() + ": the event already contains that attribute.");
                }
            }
        }

    }

    public String getName() {
        return this.name;
    }

    public AnalyticsEventCategory getCategory() {
        return this.category;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getEventType() {
        return this.eventType;
    }

    public JsonObject asJsonObject() {
        JsonObject data = new JsonObject();
        synchronized(this) {
            Iterator var3 = this.attributeSet.iterator();

            while(var3.hasNext()) {
                com.newrelic.agent.android.analytics.AnalyticAttribute attribute = (com.newrelic.agent.android.analytics.AnalyticAttribute)var3.next();
                data.add(attribute.getName(), attribute.asJsonElement());
            }

            return data;
        }
    }

    public Collection<com.newrelic.agent.android.analytics.AnalyticAttribute> getAttributeSet() {
        return Collections.unmodifiableCollection(this.attributeSet);
    }

    public static AnalyticsEvent newFromJson(JsonObject analyticsEventJson) {
        Iterator<Entry<String, JsonElement>> entry = analyticsEventJson.entrySet().iterator();
        String name = null;
        String eventType = null;
        AnalyticsEventCategory category = null;
        long timestamp = 0L;
        HashSet attributeSet = new HashSet();

        while(entry.hasNext()) {
            Entry<String, JsonElement> elem = entry.next();
            String key = elem.getKey();
            if(key.equalsIgnoreCase("name")) {
                name = elem.getValue().getAsString();
            } else if(key.equalsIgnoreCase("category")) {
                category = AnalyticsEventCategory.fromString(elem.getValue().getAsString());
            } else if(key.equalsIgnoreCase("eventType")) {
                eventType = elem.getValue().getAsString();
            } else if(key.equalsIgnoreCase("timestamp")) {
                timestamp = elem.getValue().getAsLong();
            } else {
                JsonPrimitive value = ((JsonElement)elem.getValue()).getAsJsonPrimitive();
                if(value.isString()) {
                    attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute(key, value.getAsString(), false));
                } else if(value.isBoolean()) {
                    attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute(key, value.getAsBoolean(), false));
                } else if(value.isNumber()) {
                    attributeSet.add(new com.newrelic.agent.android.analytics.AnalyticAttribute(key, value.getAsFloat(), false));
                }
            }
        }

        return new AnalyticsEvent(name, category, eventType, timestamp, attributeSet);
    }

    public static Collection<AnalyticsEvent> newFromJson(JsonArray analyticsEventsJson) {
        ArrayList<AnalyticsEvent> events = new ArrayList();
        Iterator entry = analyticsEventsJson.iterator();

        while(entry.hasNext()) {
            JsonElement e = (JsonElement)entry.next();
            events.add(newFromJson(e.getAsJsonObject()));
        }

        return events;
    }
}
