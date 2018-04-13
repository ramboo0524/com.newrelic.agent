//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.HarvestLifecycleAware;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.metric.MetricStore;
import java.util.Iterator;

public abstract class MetricMeasurementConsumer extends BaseMeasurementConsumer implements HarvestLifecycleAware {
    protected MetricStore metrics = new MetricStore();
    protected boolean recordUnscopedMetrics = true;

    public MetricMeasurementConsumer(MeasurementType measurementType) {
        super(measurementType);
        Harvest.addHarvestListener(this);
    }

    protected abstract String formatMetricName(String var1);

    public void consumeMeasurement(Measurement measurement) {
        String name = this.formatMetricName(measurement.getName());
        String scope = measurement.getScope();
        double delta = measurement.getEndTimeInSeconds() - measurement.getStartTimeInSeconds();
        Metric unscopedMetric;
        if(scope != null) {
            unscopedMetric = this.metrics.get(name, scope);
            if(unscopedMetric == null) {
                unscopedMetric = new Metric(name, scope);
                this.metrics.add(unscopedMetric);
            }

            unscopedMetric.sample(delta);
            unscopedMetric.addExclusive(measurement.getExclusiveTimeInSeconds());
        }

        if(this.recordUnscopedMetrics) {
            unscopedMetric = this.metrics.get(name);
            if(unscopedMetric == null) {
                unscopedMetric = new Metric(name);
                this.metrics.add(unscopedMetric);
            }

            unscopedMetric.sample(delta);
            unscopedMetric.addExclusive(measurement.getExclusiveTimeInSeconds());
        }
    }

    protected void addMetric(Metric newMetric) {
        Metric metric;
        if(newMetric.getScope() != null) {
            metric = this.metrics.get(newMetric.getName(), newMetric.getScope());
        } else {
            metric = this.metrics.get(newMetric.getName());
        }

        if(metric != null) {
            metric.aggregate(newMetric);
        } else {
            this.metrics.add(newMetric);
        }

    }

    public void onHarvest() {
        Iterator var1 = this.metrics.getAll().iterator();

        while(var1.hasNext()) {
            Metric metric = (Metric)var1.next();
            Harvest.addMetric(metric);
        }

    }

    public void onHarvestComplete() {
        this.metrics.clear();
    }

    public void onHarvestError() {
        this.metrics.clear();
    }

    public void onHarvestSendFailed() {
        this.metrics.clear();
    }
}
