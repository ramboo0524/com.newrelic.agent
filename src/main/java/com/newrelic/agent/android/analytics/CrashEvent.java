//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.Set;

public class CrashEvent extends AnalyticsEvent {
    public CrashEvent(String name) {
        super(name, AnalyticsEventCategory.Crash);
    }

    public CrashEvent(String name, Set<AnalyticAttribute> attributeSet) {
        super(name, AnalyticsEventCategory.Crash, "Mobile", attributeSet);
    }
}
