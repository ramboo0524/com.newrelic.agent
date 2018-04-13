//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Events extends HarvestableArray {
    private final Collection<Event> events = new ArrayList<>();


    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        Iterator var2 = this.events.iterator();

        while(var2.hasNext()) {
            Event event = (Event)var2.next();
            array.add(event.asJson());
        }

        return array;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }
}
