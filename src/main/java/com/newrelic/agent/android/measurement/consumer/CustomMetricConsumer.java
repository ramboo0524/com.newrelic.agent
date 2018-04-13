//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.measurement.CustomMetricMeasurement;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import com.newrelic.agent.android.metric.Metric;

public class CustomMetricConsumer extends MetricMeasurementConsumer {
    private static final String METRIC_PREFIX = "Custom/";

    public CustomMetricConsumer() {
        super(MeasurementType.Custom);
    }

    protected String formatMetricName(String name) {
        return METRIC_PREFIX + name;
    }

    public void consumeMeasurement(Measurement measurement) {
        CustomMetricMeasurement custom = (CustomMetricMeasurement)measurement;
        Metric metric = custom.getCustomMetric();
        metric.setName(this.formatMetricName(metric.getName()));
        this.addMetric(metric);
    }
}
