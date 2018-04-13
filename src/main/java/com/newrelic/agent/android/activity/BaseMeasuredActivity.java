//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.activity;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementException;
import com.newrelic.agent.android.measurement.MeasurementPool;
import com.newrelic.agent.android.measurement.ThreadInfo;
import com.newrelic.agent.android.tracing.TraceMachine;

public class BaseMeasuredActivity implements MeasuredActivity {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private String name;
    private long startTime;
    private long endTime;
    private ThreadInfo startingThread;
    private ThreadInfo endingThread;
    private boolean autoInstrumented;
    private Measurement startingMeasurement;
    private Measurement endingMeasurement;
    private MeasurementPool measurementPool;
    private boolean finished;

    public BaseMeasuredActivity() {
    }

    public String getName() {
        return this.name;
    }

    public String getMetricName() {
        return TraceMachine.formatActivityMetricName(this.name);
    }

    public String getBackgroundMetricName() {
        return TraceMachine.formatActivityBackgroundMetricName(this.name);
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public ThreadInfo getStartingThread() {
        return this.startingThread;
    }

    public ThreadInfo getEndingThread() {
        return this.endingThread;
    }

    public boolean isAutoInstrumented() {
        return this.autoInstrumented;
    }

    public Measurement getStartingMeasurement() {
        return this.startingMeasurement;
    }

    public Measurement getEndingMeasurement() {
        return this.endingMeasurement;
    }

    public MeasurementPool getMeasurementPool() {
        return this.measurementPool;
    }

    public void setName(String name) {
        if(!this.logIfFinished()) {
            this.name = name;
        }

    }

    public void setStartTime(long startTime) {
        if(!this.logIfFinished()) {
            this.startTime = startTime;
        }

    }

    public void setEndTime(long endTime) {
        if(!this.logIfFinished()) {
            this.endTime = endTime;
        }

    }

    public void setStartingThread(ThreadInfo startingThread) {
        if(!this.logIfFinished()) {
            this.startingThread = startingThread;
        }

    }

    public void setEndingThread(ThreadInfo endingThread) {
        if(!this.logIfFinished()) {
            this.endingThread = endingThread;
        }

    }

    public void setAutoInstrumented(boolean autoInstrumented) {
        if(!this.logIfFinished()) {
            this.autoInstrumented = autoInstrumented;
        }

    }

    public void setStartingMeasurement(Measurement startingMeasurement) {
        if(!this.logIfFinished()) {
            this.startingMeasurement = startingMeasurement;
        }

    }

    public void setEndingMeasurement(Measurement endingMeasurement) {
        if(!this.logIfFinished()) {
            this.endingMeasurement = endingMeasurement;
        }

    }

    public void setMeasurementPool(MeasurementPool measurementPool) {
        if(!this.logIfFinished()) {
            this.measurementPool = measurementPool;
        }

    }

    public void finish() {
        this.finished = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    private void throwIfFinished() {
        if(this.finished) {
            throw new MeasurementException("Attempted to modify finished Measurement");
        }
    }

    private boolean logIfFinished() {
        if(this.finished) {
            log.warning("BaseMeasuredActivity: cannot modify finished Activity");
        }

        return this.finished;
    }
}
