//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.type;

import com.newrelic.agent.android.harvest.type.Harvestable.Type;
import com.google.gson.JsonPrimitive;

public abstract class HarvestableValue extends BaseHarvestable {

    public HarvestableValue() {
        super(Type.VALUE);
    }

    public abstract JsonPrimitive asJsonPrimitive();
}
