//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.metric.MetricStore;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.HashMap;
import java.util.Iterator;

public class MachineMeasurements extends HarvestableArray {
    private final MetricStore metrics = new MetricStore();

    public MachineMeasurements() {
    }

    public void addMetric(String name, double value) {
        Metric metric = new Metric(name);
        metric.sample(value);
        this.addMetric(metric);
    }

    public void addMetric(Metric metric) {
        this.metrics.add(metric);
    }

    public void clear() {
        this.metrics.clear();
    }

    public boolean isEmpty() {
        return this.metrics.isEmpty();
    }

    public MetricStore getMetrics() {
        return this.metrics;
    }

    public JsonArray asJsonArray() {
        JsonArray metricArray = new JsonArray();
        Iterator var2 = this.metrics.getAll().iterator();

        while(var2.hasNext()) {
            Metric metric = (Metric)var2.next();
            JsonArray metricJson = new JsonArray();
            HashMap<String, String> header = new HashMap<>();
            header.put("name", metric.getName());
            header.put("scope", metric.getStringScope());
            metricJson.add((new Gson()).toJsonTree(header, GSON_STRING_MAP_TYPE));
            metricJson.add(metric.asJsonObject());
            metricArray.add(metricJson);
        }

        return metricArray;
    }
}
