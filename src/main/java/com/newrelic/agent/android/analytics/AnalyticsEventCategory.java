//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

public enum AnalyticsEventCategory {
    Session,
    Interaction,
    Crash,
    Custom,
    NetworkRequest,
    RequestError,
    Breadcrumb;


    public static AnalyticsEventCategory fromString(String categoryString) {
        AnalyticsEventCategory category = Custom;
        if(categoryString != null) {
            if(categoryString.equalsIgnoreCase("session")) {
                category = Session;
            } else if(categoryString.equalsIgnoreCase("interaction")) {
                category = Interaction;
            } else if(categoryString.equalsIgnoreCase("crash")) {
                category = Crash;
            } else if(categoryString.equalsIgnoreCase("requesterror")) {
                category = RequestError;
            } else if(categoryString.equalsIgnoreCase("breadcrumb")) {
                category = Breadcrumb;
            } else if(categoryString.equalsIgnoreCase("networkrequest")) {
                category = NetworkRequest;
            }
        }

        return category;
    }
}
