//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

public interface Measurement {
    MeasurementType getType();

    String getName();

    String getScope();

    long getStartTime();

    double getStartTimeInSeconds();

    long getEndTime();

    double getEndTimeInSeconds();

    long getExclusiveTime();

    double getExclusiveTimeInSeconds();

    ThreadInfo getThreadInfo();

    boolean isInstantaneous();

    void finish();

    boolean isFinished();

    double asDouble();
}
