//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.instrumentation.MetricCategory;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.BaseMeasurement;
import com.newrelic.agent.android.measurement.CustomMetricMeasurement;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import com.newrelic.agent.android.measurement.MethodMeasurement;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.newrelic.agent.android.tracing.Trace;
import com.newrelic.agent.android.tracing.TraceLifecycleAware;
import com.newrelic.agent.android.tracing.TraceMachine;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class SummaryMetricMeasurementConsumer extends MetricMeasurementConsumer implements TraceLifecycleAware {
    private static final String METRIC_PREFIX = "Mobile/Summary/";
    private static final String ACTIVITY_METRIC_PREFIX = "Mobile/Activity/Summary/Name/";
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private final List<ActivityTrace> completedTraces = new CopyOnWriteArrayList();

    public SummaryMetricMeasurementConsumer() {
        super(MeasurementType.Any);
        this.recordUnscopedMetrics = false;
        TraceMachine.addTraceListener(this);
    }

    public void consumeMeasurement(Measurement measurement) {
        if(measurement != null) {
            switch(measurement.getType().ordinal()) {
                case 1:
                    this.consumeMethodMeasurement((MethodMeasurement)measurement);
                    break;
                case 2:
                    this.consumeNetworkMeasurement((HttpTransactionMeasurement)measurement);
                    break;
                case 3:
                    this.consumeCustomMeasurement((CustomMetricMeasurement)measurement);
            }

        }
    }

    private void consumeMethodMeasurement(MethodMeasurement methodMeasurement) {
        if(methodMeasurement.getCategory() == null || methodMeasurement.getCategory() == MetricCategory.NONE) {
            methodMeasurement.setCategory(MetricCategory.categoryForMethod(methodMeasurement.getName()));
            if(methodMeasurement.getCategory() == MetricCategory.NONE) {
                return;
            }
        }

        BaseMeasurement summary = new BaseMeasurement(methodMeasurement);
        summary.setName(methodMeasurement.getCategory().getCategoryName());
        super.consumeMeasurement(summary);
    }

    private void consumeCustomMeasurement(CustomMetricMeasurement customMetricMeasurement) {
        if(customMetricMeasurement.getCategory() != null && customMetricMeasurement.getCategory() != MetricCategory.NONE) {
            BaseMeasurement summary = new BaseMeasurement(customMetricMeasurement);
            summary.setName(customMetricMeasurement.getCategory().getCategoryName());
            super.consumeMeasurement(summary);
        }
    }

    private void consumeNetworkMeasurement(HttpTransactionMeasurement networkMeasurement) {
        BaseMeasurement summary = new BaseMeasurement(networkMeasurement);
        summary.setName(MetricCategory.NETWORK.getCategoryName());
        super.consumeMeasurement(summary);
    }

    protected String formatMetricName(String name) {
        return METRIC_PREFIX + name.replace("#", "/");
    }

    public void onHarvest() {
        if(this.metrics.getAll().size() != 0) {
            if(this.completedTraces.size() != 0) {
                Iterator var1 = this.completedTraces.iterator();

                while(var1.hasNext()) {
                    ActivityTrace trace = (ActivityTrace)var1.next();
                    this.summarizeActivityMetrics(trace);
                }

                if(this.metrics.getAll().size() != 0) {
                    log.debug("Not all metrics were summarized!");
                }

                this.completedTraces.clear();
            }
        }
    }

    private void summarizeActivityNetworkMetrics(ActivityTrace activityTrace) {
        String activityName = activityTrace.getActivityName();
        String name;
        if(activityTrace.networkCountMetric.getCount() > 0L) {
            name = activityTrace.networkCountMetric.getName();
            activityTrace.networkCountMetric.setName(name.replace("<activity>", activityName));
            activityTrace.networkCountMetric.setCount(1L);
            activityTrace.networkCountMetric.setMinFieldValue(activityTrace.networkCountMetric.getTotal());
            activityTrace.networkCountMetric.setMaxFieldValue(activityTrace.networkCountMetric.getTotal());
            Harvest.addMetric(activityTrace.networkCountMetric);
        }

        if(activityTrace.networkTimeMetric.getCount() > 0L) {
            name = activityTrace.networkTimeMetric.getName();
            activityTrace.networkTimeMetric.setName(name.replace("<activity>", activityName));
            activityTrace.networkTimeMetric.setCount(1L);
            activityTrace.networkTimeMetric.setMinFieldValue(activityTrace.networkTimeMetric.getTotal());
            activityTrace.networkTimeMetric.setMaxFieldValue(activityTrace.networkTimeMetric.getTotal());
            Harvest.addMetric(activityTrace.networkTimeMetric);
        }

    }

    private void summarizeActivityMetrics(ActivityTrace activityTrace) {
        Trace trace = activityTrace.rootTrace;
        List<Metric> activityMetrics = this.metrics.removeAllWithScope(trace.metricName);
        List<Metric> backgroundMetrics = this.metrics.removeAllWithScope(trace.metricBackgroundName);
        Map<String, Metric> summaryMetrics = new HashMap<>();
        Iterator var6 = activityMetrics.iterator();

        Metric backgroundMetric;
        while(var6.hasNext()) {
            backgroundMetric = (Metric)var6.next();
            summaryMetrics.put(backgroundMetric.getName(), backgroundMetric);
        }

        var6 = backgroundMetrics.iterator();

        while(var6.hasNext()) {
            backgroundMetric = (Metric)var6.next();
            if(summaryMetrics.containsKey(backgroundMetric.getName())) {
                ((Metric)summaryMetrics.get(backgroundMetric.getName())).aggregate(backgroundMetric);
            } else {
                summaryMetrics.put(backgroundMetric.getName(), backgroundMetric);
            }
        }

        double totalExclusiveTime = 0.0D;

        Metric metric;
        for(Iterator var10 = summaryMetrics.values().iterator(); var10.hasNext(); totalExclusiveTime += metric.getExclusive()) {
            metric = (Metric)var10.next();
        }

        double traceTime = (double)(trace.exitTimestamp - trace.entryTimestamp) / 1000.0D;
        Iterator var14 = summaryMetrics.values().iterator();

        while(var14.hasNext()) {
            Metric next = (Metric)var14.next();
            double normalizedTime = 0.0D;
            if(next.getExclusive() != 0.0D && totalExclusiveTime != 0.0D) {
                normalizedTime = next.getExclusive() / totalExclusiveTime;
            }

            double scaledTime = normalizedTime * traceTime;
            next.setTotal(scaledTime);
            next.setExclusive(scaledTime);
            next.setMinFieldValue(0.0D);
            next.setMaxFieldValue(0.0D);
            next.setSumOfSquares(0.0D);
            next.setScope(ACTIVITY_METRIC_PREFIX + trace.displayName);
            Harvest.addMetric(next);
            Metric unScoped = new Metric(next);
            unScoped.setScope(null);
            Harvest.addMetric(unScoped);
        }

        this.summarizeActivityNetworkMetrics(activityTrace);
    }

    public void onHarvestError() {
    }

    public void onHarvestComplete() {
    }

    public void onTraceStart(ActivityTrace activityTrace) {
    }

    public void onTraceComplete(ActivityTrace activityTrace) {
        if(!this.completedTraces.contains(activityTrace)) {
            this.completedTraces.add(activityTrace);
        }

    }

    public void onEnterMethod() {
    }

    public void onExitMethod() {
    }

    public void onTraceRename(ActivityTrace activityTrace) {
    }
}
