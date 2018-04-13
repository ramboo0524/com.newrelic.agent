//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.Set;

public class BreadcrumbEvent extends AnalyticsEvent {

    public BreadcrumbEvent(String name) {
        super(name, AnalyticsEventCategory.Breadcrumb);
    }

    public BreadcrumbEvent(String name, Set<AnalyticAttribute> attributeSet) {
        super(name, AnalyticsEventCategory.Breadcrumb, "MobileBreadcrumb", attributeSet);
    }
}
