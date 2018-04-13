//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

import com.newrelic.agent.android.metric.Metric;

public class CustomMetricMeasurement extends CategorizedMeasurement {
    private Metric customMetric;

    public CustomMetricMeasurement() {
        super(MeasurementType.Custom);
    }

    public CustomMetricMeasurement(String name, int count, double totalValue, double exclusiveValue) {
        this();
        this.setName(name);
        this.customMetric = new Metric(name);
        this.customMetric.sample(totalValue);
        this.customMetric.setCount((long)count);
        this.customMetric.setExclusive(Double.valueOf(exclusiveValue));
    }

    public Metric getCustomMetric() {
        return this.customMetric;
    }
}
