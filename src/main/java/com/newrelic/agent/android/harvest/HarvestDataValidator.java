//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.tracing.ActivityTrace;
import java.util.Iterator;
import java.util.List;

public class HarvestDataValidator extends HarvestAdapter {
    public HarvestDataValidator() {
    }

    public void onHarvestFinalize() {
        if(Harvest.isInitialized()) {
            this.ensureActivityNameMetricsExist();
        }
    }

    public void ensureActivityNameMetricsExist() {
        HarvestData harvestData = Harvest.getInstance().getHarvestData();
        ActivityTraces activityTraces = harvestData.getActivityTraces();
        if(activityTraces != null && activityTraces.count() != 0) {
            MachineMeasurements metrics = harvestData.getMetrics();
            if(metrics != null && !metrics.isEmpty()) {
                Iterator var4 = activityTraces.getActivityTraces().iterator();

                while(var4.hasNext()) {
                    ActivityTrace activityTrace = (ActivityTrace)var4.next();
                    String activityName = activityTrace.rootTrace.displayName;
                    int hashIndex = activityName.indexOf("#");
                    if(hashIndex > 0) {
                        activityName = activityName.substring(0, hashIndex);
                    }

                    String activityMetricRoot = "Mobile/Activity/Name/" + activityName;
                    boolean foundMetricForActivity = false;
                    List<Metric> unScopedMetrics = metrics.getMetrics().getAllUnscoped();
                    if(unScopedMetrics != null && unScopedMetrics.size() > 0) {
                        Iterator var11 = unScopedMetrics.iterator();

                        while(var11.hasNext()) {
                            Metric metric = (Metric)var11.next();
                            if(metric.getName().startsWith(activityMetricRoot)) {
                                foundMetricForActivity = true;
                                break;
                            }
                        }
                    }

                    if(!foundMetricForActivity) {
                        Metric activityMetric = new Metric(activityMetricRoot);
                        metrics.addMetric(activityMetric);
                    }
                }

            }
        }
    }
}
