//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.Map;
import java.util.Set;

public interface AnalyticsController {
    com.newrelic.agent.android.analytics.AnalyticAttribute getAttribute(String var1);

    Set<com.newrelic.agent.android.analytics.AnalyticAttribute> getSystemAttributes();

    Set<com.newrelic.agent.android.analytics.AnalyticAttribute> getUserAttributes();

    Set<com.newrelic.agent.android.analytics.AnalyticAttribute> getSessionAttributes();

    int getSystemAttributeCount();

    int getUserAttributeCount();

    int getSessionAttributeCount();

    boolean setAttribute(String var1, String var2);

    boolean setAttribute(String var1, String var2, boolean var3);

    boolean setAttribute(String var1, float var2);

    boolean setAttribute(String var1, float var2, boolean var3);

    boolean setAttribute(String var1, boolean var2);

    boolean setAttribute(String var1, boolean var2, boolean var3);

    boolean incrementAttribute(String var1, float var2);

    boolean incrementAttribute(String var1, float var2, boolean var3);

    boolean removeAttribute(String var1);

    boolean removeAllAttributes();

    boolean addEvent(AnalyticsEvent var1);

    boolean addEvent(String var1, Set<com.newrelic.agent.android.analytics.AnalyticAttribute> var2);

    boolean addEvent(String var1, AnalyticsEventCategory var2, String var3, Set<com.newrelic.agent.android.analytics.AnalyticAttribute> var4);

    int getMaxEventPoolSize();

    void setMaxEventPoolSize(int var1);

    int getMaxEventBufferTime();

    void setMaxEventBufferTime(int var1);

    EventManager getEventManager();

    boolean recordEvent(String var1, Map<String, Object> var2);
}
