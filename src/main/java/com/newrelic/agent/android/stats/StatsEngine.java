//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stats;

import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.harvest.HarvestAdapter;
import com.newrelic.agent.android.metric.Metric;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class StatsEngine extends HarvestAdapter {
    public static final StatsEngine INSTANCE = new StatsEngine();
    public boolean enabled = true;
    private ConcurrentHashMap<String, Metric> statsMap = new ConcurrentHashMap();

    private StatsEngine() {
    }

    public static StatsEngine get() {
        return INSTANCE;
    }

    public void inc(String name) {
        Metric m = this.lazyGet(name);
        synchronized(m) {
            m.increment();
        }
    }

    public void inc(String name, long count) {
        Metric m = this.lazyGet(name);
        synchronized(m) {
            m.increment(count);
        }
    }

    public void sample(String name, float value) {
        Metric m = this.lazyGet(name);
        synchronized(m) {
            m.sample((double)value);
        }
    }

    public void sampleTimeMs(String name, long time) {
        this.sample(name, (float)time / 1000.0F);
    }

    public static void populateMetrics() {
        Iterator var0 = INSTANCE.getStatsMap().entrySet().iterator();

        while(var0.hasNext()) {
            Entry<String, Metric> entry = (Entry)var0.next();
            Metric metric = (Metric)entry.getValue();
            TaskQueue.queue(metric);
        }

    }

    public void onHarvest() {
        populateMetrics();
        reset();
    }

    public static void reset() {
        INSTANCE.getStatsMap().clear();
    }

    public static synchronized void disable() {
        INSTANCE.enabled = false;
    }

    public static synchronized void enable() {
        INSTANCE.enabled = true;
    }

    public ConcurrentHashMap<String, Metric> getStatsMap() {
        return this.statsMap;
    }

    private Metric lazyGet(String name) {
        Metric m = this.statsMap.get(name);
        if(m == null) {
            m = new Metric(name);
            if(this.enabled) {
                this.statsMap.put(name, m);
            }
        }

        return m;
    }
}
