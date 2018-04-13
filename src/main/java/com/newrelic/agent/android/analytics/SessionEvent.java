//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.Set;

public class SessionEvent extends AnalyticsEvent {
    public SessionEvent() {
        super(null, AnalyticsEventCategory.Session);
    }

    public SessionEvent(Set<AnalyticAttribute> attributeSet) {
        super(null, AnalyticsEventCategory.Session, "Mobile", attributeSet);
    }
}
