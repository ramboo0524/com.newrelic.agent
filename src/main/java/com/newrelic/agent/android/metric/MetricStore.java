//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class MetricStore {
    private final Map<String, Map<String, Metric>> metricStore = new ConcurrentHashMap<>();

    public MetricStore() {
    }

    public void add(Metric metric) {
        String scope = metric.getStringScope();
        String name = metric.getName();
        if(!this.metricStore.containsKey(scope)) {
            this.metricStore.put(scope, new HashMap());
        }

        if(this.metricStore.get(scope).containsKey(name)) {
            ((Metric)((Map)this.metricStore.get(scope)).get(name)).aggregate(metric);
        } else {
            this.metricStore.get(scope).put(name, metric);
        }

    }

    public Metric get(String name) {
        return this.get(name, "");
    }

    public Metric get(String name, String scope) {
        try {
            return (Metric)((Map)this.metricStore.get(scope == null?"":scope)).get(name);
        } catch (NullPointerException var4) {
            return null;
        }
    }

    public List<Metric> getAll() {
        List<Metric> metrics = new ArrayList<>();
        Iterator var2 = this.metricStore.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, Map<String, Metric>> entry = (Entry)var2.next();
            Iterator var4 = ((Map)entry.getValue()).entrySet().iterator();

            while(var4.hasNext()) {
                Entry<String, Metric> metricEntry = (Entry)var4.next();
                metrics.add(metricEntry.getValue());
            }
        }

        return metrics;
    }

    public List<Metric> getAllByScope(String scope) {
        ArrayList metrics = new ArrayList();

        try {
            Iterator var3 = ((Map)this.metricStore.get(scope)).entrySet().iterator();

            while(var3.hasNext()) {
                Entry<String, Metric> metricEntry = (Entry)var3.next();
                metrics.add(metricEntry.getValue());
            }
        } catch (NullPointerException var5) {
            ;
        }

        return metrics;
    }

    public List<Metric> getAllUnscoped() {
        return this.getAllByScope("");
    }

    public void remove(Metric metric) {
        String scope = metric.getStringScope();
        String name = metric.getName();
        if(this.metricStore.containsKey(scope)) {
            if(((Map)this.metricStore.get(scope)).containsKey(name)) {
                ((Map)this.metricStore.get(scope)).remove(name);
            }
        }
    }

    public void removeAll(List<Metric> metrics) {
//        Map var2 = this.metricStore;
        synchronized(this.metricStore) {
            Iterator var3 = metrics.iterator();

            while(var3.hasNext()) {
                Metric metric = (Metric)var3.next();
                this.remove(metric);
            }

        }
    }

    public List<Metric> removeAllWithScope(String scope) {
        List<Metric> metrics = this.getAllByScope(scope);
        if(!metrics.isEmpty()) {
            this.removeAll(metrics);
        }

        return metrics;
    }

    public void clear() {
        this.metricStore.clear();
    }

    public boolean isEmpty() {
        return this.metricStore.isEmpty();
    }
}
