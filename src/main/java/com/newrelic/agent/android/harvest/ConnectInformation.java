//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.google.gson.JsonArray;

public class ConnectInformation extends HarvestableArray {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private ApplicationInformation applicationInformation;
    private DeviceInformation deviceInformation;

    public ConnectInformation(ApplicationInformation applicationInformation, DeviceInformation deviceInformation) {
        if(null == applicationInformation) {
            log.error("null applicationInformation passed into ConnectInformation constructor");
        }

        if(null == deviceInformation) {
            log.error("null deviceInformation passed into ConnectInformation constructor");
        }

        this.applicationInformation = applicationInformation;
        this.deviceInformation = deviceInformation;
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        this.notNull(this.applicationInformation);
        array.add(this.applicationInformation.asJsonArray());
        this.notNull(this.deviceInformation);
        array.add(this.deviceInformation.asJsonArray());
        return array;
    }

    public ApplicationInformation getApplicationInformation() {
        return this.applicationInformation;
    }

    public DeviceInformation getDeviceInformation() {
        return this.deviceInformation;
    }

    public void setApplicationInformation(ApplicationInformation applicationInformation) {
        this.applicationInformation = applicationInformation;
    }

    public void setDeviceInformation(DeviceInformation deviceInformation) {
        this.deviceInformation = deviceInformation;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            ConnectInformation that = (ConnectInformation)o;
            if(this.applicationInformation != null) {
                if(!this.applicationInformation.equals(that.applicationInformation)) {
                    return false;
                }
            } else if(that.applicationInformation != null) {
                return false;
            }

            if(this.deviceInformation != null) {
                if(this.deviceInformation.equals(that.deviceInformation)) {
                    return true;
                }
            } else if(that.deviceInformation == null) {
                return true;
            }

            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.applicationInformation != null?this.applicationInformation.hashCode():0;
        result = 31 * result + (this.deviceInformation != null?this.deviceInformation.hashCode():0);
        return result;
    }
}
