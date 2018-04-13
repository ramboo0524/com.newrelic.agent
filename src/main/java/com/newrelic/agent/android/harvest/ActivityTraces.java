//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ActivityTraces extends HarvestableArray {
    private final Collection<ActivityTrace> activityTraces = new ArrayList<>();

    public ActivityTraces() {
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        Iterator var2 = this.activityTraces.iterator();

        while(var2.hasNext()) {
            ActivityTrace activityTrace = (ActivityTrace)var2.next();
            array.add(activityTrace.asJson());
        }

        return array;
    }

    public synchronized void add(ActivityTrace activityTrace) {
        this.activityTraces.add(activityTrace);
    }

    public synchronized void remove(ActivityTrace activityTrace) {
        this.activityTraces.remove(activityTrace);
    }

    public void clear() {
        this.activityTraces.clear();
    }

    public int count() {
        return this.activityTraces.size();
    }

    public Collection<ActivityTrace> getActivityTraces() {
        return this.activityTraces;
    }
}
