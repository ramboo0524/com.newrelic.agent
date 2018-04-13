//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.newrelic.agent.android.harvest.type.Harvestable.Type;
import com.google.gson.JsonArray;

public abstract class HarvestableArray extends BaseHarvestable {
    public HarvestableArray() {
        super(Type.ARRAY);
    }

    public abstract JsonArray asJsonArray();
}
