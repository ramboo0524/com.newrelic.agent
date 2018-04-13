//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.measurement.MeasurementType;

public class ActivityMeasurementConsumer extends MetricMeasurementConsumer {
    public ActivityMeasurementConsumer() {
        super(MeasurementType.Activity);
    }

    protected String formatMetricName(String name) {
        return name;
    }
}
