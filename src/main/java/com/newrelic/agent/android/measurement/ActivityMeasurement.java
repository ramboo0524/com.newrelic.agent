//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

public class ActivityMeasurement extends BaseMeasurement {
    public ActivityMeasurement(String name, long startTime, long endTime) {
        super(MeasurementType.Activity);
        this.setName(name);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
    }
}
