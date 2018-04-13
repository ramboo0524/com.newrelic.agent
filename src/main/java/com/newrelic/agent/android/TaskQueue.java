//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import com.newrelic.agent.android.harvest.AgentHealth;
import com.newrelic.agent.android.harvest.AgentHealthException;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.HarvestAdapter;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.newrelic.agent.android.tracing.Trace;
import com.newrelic.agent.android.util.NamedThreadFactory;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskQueue extends HarvestAdapter {
    private static final long DEQUEUE_PERIOD_MS = 1000L;
    private static final ScheduledExecutorService queueExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("TaskQueue"));
    private static final ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
    private static final Runnable dequeueTask = new Runnable() {
        public void run() {
            TaskQueue.dequeue();
        }
    };
    private static Future dequeueFuture;

    public TaskQueue() {
    }

    public static void queue(Object object) {
        queue.add(object);
    }

    public static void backgroundDequeue() {
        queueExecutor.execute(dequeueTask);
    }

    public static void synchronousDequeue() {
        Future future = queueExecutor.submit(dequeueTask);

        try {
            future.get();
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        } catch (ExecutionException var3) {
            var3.printStackTrace();
        }

    }

    public static void start() {
        if(dequeueFuture == null) {
            dequeueFuture = queueExecutor.scheduleAtFixedRate(dequeueTask, 0L, DEQUEUE_PERIOD_MS, TimeUnit.MILLISECONDS);
        }
    }

    public static void stop() {
        if(dequeueFuture != null) {
            dequeueFuture.cancel(true);
            dequeueFuture = null;
        }
    }

    private static void dequeue() {
        if(queue.size() != 0) {
            Measurements.setBroadcastNewMeasurements(false);

            while(!queue.isEmpty()) {
                try {
                    Object object = queue.remove();
                    if(object instanceof ActivityTrace) {
                        Harvest.addActivityTrace((ActivityTrace)object);
                    } else if(object instanceof Metric) {
                        Harvest.addMetric((Metric)object);
                    } else if(object instanceof AgentHealthException) {
                        Harvest.addAgentHealthException((AgentHealthException)object);
                    } else if(object instanceof Trace) {
                        Measurements.addTracedMethod((Trace)object);
                    } else if(object instanceof HttpTransactionMeasurement) {
                        Measurements.addHttpTransaction((HttpTransactionMeasurement)object);
                    }
                } catch (Exception var1) {
                    var1.printStackTrace();
                    AgentHealth.noticeException(var1);
                }
            }

            Measurements.broadcast();
            Measurements.setBroadcastNewMeasurements(true);
        }
    }

    public static int size() {
        return queue.size();
    }

    public static void clear() {
        queue.clear();
    }
}
