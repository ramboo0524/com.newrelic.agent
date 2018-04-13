//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivityHistory extends HarvestableArray {
    private final List<ActivitySighting> activityHistory;

    public ActivityHistory(List<ActivitySighting> activityHistory) {
        this.activityHistory = activityHistory;
    }

    public int size() {
        return this.activityHistory.size();
    }

    public JsonArray asJsonArray() {
        JsonArray data = new JsonArray();
        Iterator var2 = this.activityHistory.iterator();

        while(var2.hasNext()) {
            ActivitySighting sighting = (ActivitySighting)var2.next();
            data.add(sighting.asJsonArray());
        }

        return data;
    }

    public JsonArray asJsonArrayWithoutDuration() {
        JsonArray data = new JsonArray();
        Iterator var2 = this.activityHistory.iterator();

        while(var2.hasNext()) {
            ActivitySighting sighting = (ActivitySighting)var2.next();
            data.add(sighting.asJsonArrayWithoutDuration());
        }

        return data;
    }

    public static ActivityHistory newFromJson(JsonArray jsonArray) {
        List<ActivitySighting> sightings = new ArrayList<>();
        Iterator var2 = jsonArray.iterator();

        while(var2.hasNext()) {
            JsonElement element = (JsonElement)var2.next();
            sightings.add(ActivitySighting.newFromJson(element.getAsJsonArray()));
        }

        return new ActivityHistory(sightings);
    }
}
