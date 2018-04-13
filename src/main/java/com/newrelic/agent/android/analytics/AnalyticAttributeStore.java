//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import com.newrelic.agent.android.payload.PayloadStore;
import java.util.List;

public interface AnalyticAttributeStore extends PayloadStore<com.newrelic.agent.android.analytics.AnalyticAttribute> {
    boolean store(com.newrelic.agent.android.analytics.AnalyticAttribute var1);

    List<com.newrelic.agent.android.analytics.AnalyticAttribute> fetchAll();

    int count();

    void clear();

    void delete(com.newrelic.agent.android.analytics.AnalyticAttribute var1);
}
