//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class DataToken extends HarvestableArray {
    private int accountId;
    private int agentId;

    public DataToken() {
    }

    public DataToken(int accountId, int agentId) {
        this.accountId = accountId;
        this.agentId = agentId;
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        array.add(new JsonPrimitive(this.accountId));
        array.add(new JsonPrimitive(this.agentId));
        return array;
    }

    public void clear() {
        this.accountId = 0;
        this.agentId = 0;
    }

    public int getAccountId() {
        return this.accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getAgentId() {
        return this.agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public boolean isValid() {
        return this.accountId > 0 && this.agentId > 0;
    }

    public String toString() {
        return "DataToken{accountId=" + this.accountId + ", agentId=" + this.agentId + '}';
    }
}
