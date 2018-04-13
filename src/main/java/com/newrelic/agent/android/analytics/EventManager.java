//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface EventManager {
    List<String> RESERVED_EVENT_TYPES = new ArrayList<>();

    void initialize();

    void shutdown();

    int size();

    void empty();

    boolean isTransmitRequired();

    boolean addEvent(AnalyticsEvent var1);

    int getEventsRecorded();

    int getEventsEjected();

    boolean isMaxEventBufferTimeExceeded();

    boolean isMaxEventPoolSizeExceeded();

    int getMaxEventPoolSize();

    void setMaxEventPoolSize(int var1);

    int getMaxEventBufferTime();

    void setMaxEventBufferTime(int var1);

    Collection<AnalyticsEvent> getQueuedEvents();
}
