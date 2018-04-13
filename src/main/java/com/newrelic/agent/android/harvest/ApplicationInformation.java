//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class ApplicationInformation extends HarvestableArray {
    private String appName;
    private String appVersion;
    private String appBuild;
    private String packageId;
    private int versionCode;

    public ApplicationInformation() {
        this.versionCode = -1;
    }

    public ApplicationInformation(String appName, String appVersion, String packageId, String appBuild) {
        this();
        this.appName = appName;
        this.appVersion = appVersion;
        this.packageId = packageId;
        this.appBuild = appBuild;
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        this.notEmpty(this.appName);
        array.add(new JsonPrimitive(this.appName));
        this.notEmpty(this.appVersion);
        array.add(new JsonPrimitive(this.appVersion));
        this.notEmpty(this.packageId);
        array.add(new JsonPrimitive(this.packageId));
        return array;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppBuild(String appBuild) {
        this.appBuild = appBuild;
    }

    public String getAppBuild() {
        return this.appBuild;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageId() {
        return this.packageId;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getVersionCode() {
        return this.versionCode;
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o != null && this.getClass() == o.getClass()) {
            ApplicationInformation that;
            label61: {
                that = (ApplicationInformation)o;
                if(this.appName != null) {
                    if(this.appName.equals(that.appName)) {
                        break label61;
                    }
                } else if(that.appName == null) {
                    break label61;
                }

                return false;
            }

            label54: {
                if(this.appVersion != null) {
                    if(this.appVersion.equals(that.appVersion)) {
                        break label54;
                    }
                } else if(that.appVersion == null) {
                    break label54;
                }

                return false;
            }

            if(this.appBuild != null) {
                if(!this.appBuild.equals(that.appBuild)) {
                    return false;
                }
            } else if(that.appBuild != null) {
                return false;
            }

            label40: {
                if(this.packageId != null) {
                    if(this.packageId.equals(that.packageId)) {
                        break label40;
                    }
                } else if(that.packageId == null) {
                    break label40;
                }

                return false;
            }

            if(this.versionCode != that.versionCode) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.appName != null?this.appName.hashCode():0;
        result = 31 * result + (this.appVersion != null?this.appVersion.hashCode():0);
        result = 31 * result + (this.appBuild != null?this.appBuild.hashCode():0);
        result = 31 * result + (this.packageId != null?this.packageId.hashCode():0);
        return result;
    }

    public boolean isAppUpgrade(ApplicationInformation that) {
        boolean brc = false;
        if(that.versionCode == -1) {
            brc = this.versionCode >= 0 && that.appVersion != null;
        } else {
            brc = this.versionCode > that.versionCode;
        }

        return brc;
    }
}
