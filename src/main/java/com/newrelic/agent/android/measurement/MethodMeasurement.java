//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

import com.newrelic.agent.android.instrumentation.MetricCategory;

public class MethodMeasurement extends CategorizedMeasurement {
    public MethodMeasurement(String name, String scope, long startTime, long endTime, long exclusiveTime, MetricCategory category) {
        super(MeasurementType.Method);
        this.setName(name);
        this.setScope(scope);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setExclusiveTime(exclusiveTime);
        this.setCategory(category);
    }
}
