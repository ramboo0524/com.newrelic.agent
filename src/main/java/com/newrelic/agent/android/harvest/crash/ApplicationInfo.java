//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.crash;

import com.newrelic.agent.android.harvest.ApplicationInformation;
import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonObject;

public class ApplicationInfo extends HarvestableObject {
    private String applicationName = "";
    private String applicationVersion = "";
    private String applicationBuild = "";
    private String bundleId = "";
    private int processId = 0;

    public ApplicationInfo() {
    }

    public ApplicationInfo(ApplicationInformation applicationInformation) {
        this.applicationName = applicationInformation.getAppName();
        this.applicationVersion = applicationInformation.getAppVersion();
        this.applicationBuild = applicationInformation.getAppBuild();
        this.bundleId = applicationInformation.getPackageId();
    }

    public JsonObject asJsonObject() {
        JsonObject data = new JsonObject();
        data.add("appName", SafeJsonPrimitive.factory(this.applicationName));
        data.add("appVersion", SafeJsonPrimitive.factory(this.applicationVersion));
        data.add("appBuild", SafeJsonPrimitive.factory(this.applicationBuild));
        data.add("bundleId", SafeJsonPrimitive.factory(this.bundleId));
        data.add("processId", SafeJsonPrimitive.factory(this.processId));
        return data;
    }

    public static ApplicationInfo newFromJson(JsonObject jsonObject) {
        ApplicationInfo info = new ApplicationInfo();
        info.applicationName = jsonObject.get("appName").getAsString();
        info.applicationVersion = jsonObject.get("appVersion").getAsString();
        info.applicationBuild = jsonObject.get("appBuild").getAsString();
        info.bundleId = jsonObject.get("bundleId").getAsString();
        info.processId = jsonObject.get("processId").getAsInt();
        return info;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getApplicationVersion() {
        return this.applicationVersion;
    }

    public String getApplicationBuild() {
        return this.applicationBuild;
    }
}
