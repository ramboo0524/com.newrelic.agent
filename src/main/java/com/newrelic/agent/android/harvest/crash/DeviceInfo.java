//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.crash;

import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.EnvironmentInformation;
import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;

public class DeviceInfo extends HarvestableObject {
    private long memoryUsage;
    private int orientation;
    private String networkStatus;
    private long[] diskAvailable;
    private String OSVersion;
    private String deviceName;
    private String OSBuild;
    private String architecture;
    private String modelNumber;
    private String screenResolution;
    private String deviceUuid;
    private String runTime;

    public DeviceInfo() {
    }

    public DeviceInfo(DeviceInformation devInfo, EnvironmentInformation envInfo) {
        this.memoryUsage = envInfo.getMemoryUsage();
        this.orientation = envInfo.getOrientation();
        this.networkStatus = envInfo.getNetworkStatus();
        this.diskAvailable = envInfo.getDiskAvailable();
        this.OSVersion = devInfo.getOsVersion();
        this.deviceName = devInfo.getManufacturer();
        this.OSBuild = devInfo.getOsBuild();
        this.architecture = devInfo.getArchitecture();
        this.modelNumber = devInfo.getModel();
        this.screenResolution = devInfo.getSize();
        this.deviceUuid = devInfo.getDeviceId();
        this.runTime = devInfo.getRunTime();
    }

    public JsonObject asJsonObject() {
        JsonObject data = new JsonObject();
        data.add("memoryUsage", SafeJsonPrimitive.factory(this.memoryUsage));
        data.add("orientation", SafeJsonPrimitive.factory(this.orientation));
        data.add("networkStatus", SafeJsonPrimitive.factory(this.networkStatus));
        data.add("diskAvailable", this.getDiskAvailableAsJson());
        data.add("osVersion", SafeJsonPrimitive.factory(this.OSVersion));
        data.add("deviceName", SafeJsonPrimitive.factory(this.deviceName));
        data.add("osBuild", SafeJsonPrimitive.factory(this.OSBuild));
        data.add("architecture", SafeJsonPrimitive.factory(this.architecture));
        data.add("runTime", SafeJsonPrimitive.factory(this.runTime));
        data.add("modelNumber", SafeJsonPrimitive.factory(this.modelNumber));
        data.add("screenResolution", SafeJsonPrimitive.factory(this.screenResolution));
        data.add("deviceUuid", SafeJsonPrimitive.factory(this.deviceUuid));
        return data;
    }

    public static DeviceInfo newFromJson(JsonObject jsonObject) {
        DeviceInfo info = new DeviceInfo();
        info.memoryUsage = jsonObject.get("memoryUsage").getAsLong();
        info.orientation = jsonObject.get("orientation").getAsInt();
        info.networkStatus = jsonObject.get("networkStatus").getAsString();
        info.diskAvailable = longArrayFromJsonArray(jsonObject.get("diskAvailable").getAsJsonArray());
        info.OSVersion = jsonObject.get("osVersion").getAsString();
        info.deviceName = jsonObject.get("deviceName").getAsString();
        info.OSBuild = jsonObject.get("osBuild").getAsString();
        info.architecture = jsonObject.get("architecture").getAsString();
        info.runTime = jsonObject.get("runTime").getAsString();
        info.modelNumber = jsonObject.get("modelNumber").getAsString();
        info.screenResolution = jsonObject.get("screenResolution").getAsString();
        info.deviceUuid = jsonObject.get("deviceUuid").getAsString();
        return info;
    }

    private static long[] longArrayFromJsonArray(JsonArray jsonArray) {
        long[] array = new long[jsonArray.size()];
        int i = 0;

        JsonElement jsonElement;
        for(Iterator var3 = jsonArray.iterator(); var3.hasNext(); array[i++] = jsonElement.getAsLong()) {
            jsonElement = (JsonElement)var3.next();
        }

        return array;
    }

    private JsonArray getDiskAvailableAsJson() {
        JsonArray data = new JsonArray();
        long[] var2 = this.diskAvailable;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            long value = var2[var4];
            data.add(SafeJsonPrimitive.factory(value));
        }

        return data;
    }
}
