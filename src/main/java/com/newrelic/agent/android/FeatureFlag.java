//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import java.util.HashSet;
import java.util.Set;

public enum FeatureFlag {
    HttpResponseBodyCapture,
    CrashReporting,
    AnalyticsEvents,
    InteractionTracing,
    DefaultInteractions,
    NetworkRequests,
    NetworkErrorRequests,
    HandledExceptions;

    protected static final Set<FeatureFlag> enabledFeatures = new HashSet<>();

    public static void enableFeature(FeatureFlag featureFlag) {
        enabledFeatures.add(featureFlag);
    }

    public static void disableFeature(FeatureFlag featureFlag) {
        enabledFeatures.remove(featureFlag);
    }

    public static boolean featureEnabled(FeatureFlag featureFlag) {
        return enabledFeatures.contains(featureFlag);
    }

    static void resetFeatures() {
        enabledFeatures.clear();
        enableFeature(HttpResponseBodyCapture);
        enableFeature(CrashReporting);
        enableFeature(AnalyticsEvents);
        enableFeature(InteractionTracing);
        enableFeature(DefaultInteractions);
        enableFeature(NetworkRequests);
        enableFeature(NetworkErrorRequests);
        enableFeature(HandledExceptions);
    }

    static {
        resetFeatures();
    }
}
