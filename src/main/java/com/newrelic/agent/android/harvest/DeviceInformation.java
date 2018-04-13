//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.ApplicationPlatform;
import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.Map;

public class DeviceInformation extends HarvestableArray {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private String osName;
    private String osVersion;
    private String osBuild;
    private String model;
    private String agentName;
    private String agentVersion;
    private String deviceId;
    private String countryCode;
    private String regionCode;
    private String manufacturer;
    private String architecture;
    private String runTime;
    private String size;
    private ApplicationPlatform applicationPlatform;
    private String applicationPlatformVersion;
    private Map<String, String> misc = new HashMap();

    public DeviceInformation() {
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        this.notEmpty(this.osName);
        array.add(new JsonPrimitive(this.osName));
        this.notEmpty(this.osVersion);
        array.add(new JsonPrimitive(this.osVersion));
        this.notEmpty(this.manufacturer);
        this.notEmpty(this.model);
        array.add(new JsonPrimitive(this.manufacturer + " " + this.model));
        this.notEmpty(this.agentName);
        array.add(new JsonPrimitive(this.agentName));
        this.notEmpty(this.agentVersion);
        array.add(new JsonPrimitive(this.agentVersion));
        this.notEmpty(this.deviceId);
        array.add(new JsonPrimitive(this.deviceId));
        array.add(new JsonPrimitive(this.optional(this.countryCode)));
        array.add(new JsonPrimitive(this.optional(this.regionCode)));
        array.add(new JsonPrimitive(this.manufacturer));
        Map<String, String> miscMap = new HashMap();
        if(this.misc != null && !this.misc.isEmpty()) {
            miscMap.putAll(this.misc);
        }

        if(this.applicationPlatform != null) {
            miscMap.put("platform", this.applicationPlatform.toString());
            if(this.applicationPlatformVersion != null) {
                miscMap.put("platformVersion", this.applicationPlatformVersion);
            }
        }

        JsonElement map = (new Gson()).toJsonTree(miscMap, GSON_STRING_MAP_TYPE);
        array.add(map);
        return array;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setOsBuild(String osBuild) {
        this.osBuild = osBuild;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public void setSize(String size) {
        this.size = size;
        this.addMisc("size", size);
    }

    public void setApplicationPlatform(ApplicationPlatform applicationPlatform) {
        this.applicationPlatform = applicationPlatform;
    }

    public void setApplicationPlatformVersion(String applicationPlatformVersion) {
        this.applicationPlatformVersion = applicationPlatformVersion;
    }

    public void setMisc(Map<String, String> misc) {
        this.misc = new HashMap(misc);
    }

    public void addMisc(String key, String value) {
        this.misc.put(key, value);
    }

    public String getOsName() {
        return this.osName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public String getOsBuild() {
        return this.osBuild;
    }

    public String getModel() {
        return this.model;
    }

    public String getAgentName() {
        return this.agentName;
    }

    public String getAgentVersion() {
        return this.agentVersion;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getRegionCode() {
        return this.regionCode;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public String getRunTime() {
        return this.runTime;
    }

    public String getSize() {
        return this.size;
    }

    public ApplicationPlatform getApplicationPlatform() {
        return this.applicationPlatform;
    }

    public String getApplicationPlatformVersion() {
        return this.applicationPlatformVersion;
    }

    public String toJsonString() {
        return "DeviceInformation{manufacturer='" + this.manufacturer + '\'' + ", osName='" + this.osName + '\'' + ", osVersion='" + this.osVersion + '\'' + ", model='" + this.model + '\'' + ", agentName='" + this.agentName + '\'' + ", agentVersion='" + this.agentVersion + '\'' + ", deviceId='" + this.deviceId + '\'' + ", countryCode='" + this.countryCode + '\'' + ", regionCode='" + this.regionCode + '\'' + '}';
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            DeviceInformation that;
            label141: {
                that = (DeviceInformation)o;
                if(this.agentName != null) {
                    if(this.agentName.equals(that.agentName)) {
                        break label141;
                    }
                } else if(that.agentName == null) {
                    break label141;
                }

                return false;
            }

            label134: {
                if(this.agentVersion != null) {
                    if(this.agentVersion.equals(that.agentVersion)) {
                        break label134;
                    }
                } else if(that.agentVersion == null) {
                    break label134;
                }

                return false;
            }

            if(this.architecture != null) {
                if(!this.architecture.equals(that.architecture)) {
                    return false;
                }
            } else if(that.architecture != null) {
                return false;
            }

            label120: {
                if(this.deviceId != null) {
                    if(this.deviceId.equals(that.deviceId)) {
                        break label120;
                    }
                } else if(that.deviceId == null) {
                    break label120;
                }

                return false;
            }

            label113: {
                if(this.manufacturer != null) {
                    if(this.manufacturer.equals(that.manufacturer)) {
                        break label113;
                    }
                } else if(that.manufacturer == null) {
                    break label113;
                }

                return false;
            }

            if(this.model != null) {
                if(!this.model.equals(that.model)) {
                    return false;
                }
            } else if(that.model != null) {
                return false;
            }

            if(this.osBuild != null) {
                if(!this.osBuild.equals(that.osBuild)) {
                    return false;
                }
            } else if(that.osBuild != null) {
                return false;
            }

            label92: {
                if(this.osName != null) {
                    if(this.osName.equals(that.osName)) {
                        break label92;
                    }
                } else if(that.osName == null) {
                    break label92;
                }

                return false;
            }

            if(this.osVersion != null) {
                if(!this.osVersion.equals(that.osVersion)) {
                    return false;
                }
            } else if(that.osVersion != null) {
                return false;
            }

            if(this.runTime != null) {
                if(!this.runTime.equals(that.runTime)) {
                    return false;
                }
            } else if(that.runTime != null) {
                return false;
            }

            if(this.size != null) {
                if(!this.size.equals(that.size)) {
                    return false;
                }
            } else if(that.size != null) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.osName != null?this.osName.hashCode():0;
        result = 31 * result + (this.osVersion != null?this.osVersion.hashCode():0);
        result = 31 * result + (this.osBuild != null?this.osBuild.hashCode():0);
        result = 31 * result + (this.model != null?this.model.hashCode():0);
        result = 31 * result + (this.agentName != null?this.agentName.hashCode():0);
        result = 31 * result + (this.agentVersion != null?this.agentVersion.hashCode():0);
        result = 31 * result + (this.deviceId != null?this.deviceId.hashCode():0);
        result = 31 * result + (this.manufacturer != null?this.manufacturer.hashCode():0);
        result = 31 * result + (this.architecture != null?this.architecture.hashCode():0);
        result = 31 * result + (this.runTime != null?this.runTime.hashCode():0);
        result = 31 * result + (this.size != null?this.size.hashCode():0);
        return result;
    }
}
