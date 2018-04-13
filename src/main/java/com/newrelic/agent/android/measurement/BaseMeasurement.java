//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;

public class BaseMeasurement implements Measurement {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private MeasurementType type;
    private String name;
    private String scope;
    private long startTime;
    private long endTime;
    private long exclusiveTime;
    private ThreadInfo threadInfo;
    private boolean finished;

    public BaseMeasurement(MeasurementType measurementType) {
        this.setType(measurementType);
    }

    public BaseMeasurement(Measurement measurement) {
        this.setType(measurement.getType());
        this.setName(measurement.getName());
        this.setScope(measurement.getScope());
        this.setStartTime(measurement.getStartTime());
        this.setEndTime(measurement.getEndTime());
        this.setExclusiveTime(measurement.getExclusiveTime());
        this.setThreadInfo(measurement.getThreadInfo());
        this.finished = measurement.isFinished();
    }

    void setType(MeasurementType type) {
        if(!this.logIfFinished()) {
            this.type = type;
        }

    }

    public void setName(String name) {
        if(!this.logIfFinished()) {
            this.name = name;
        }

    }

    public void setScope(String scope) {
        if(!this.logIfFinished()) {
            this.scope = scope;
        }

    }

    public void setStartTime(long startTime) {
        if(!this.logIfFinished()) {
            this.startTime = startTime;
        }

    }

    public void setEndTime(long endTime) {
        if(!this.logIfFinished()) {
            if(endTime < this.startTime) {
                log.error("Measurement end time must not precede start time - startTime: " + this.startTime + " endTime: " + endTime);
                return;
            }

            this.endTime = endTime;
        }

    }

    public void setExclusiveTime(long exclusiveTime) {
        if(!this.logIfFinished()) {
            this.exclusiveTime = exclusiveTime;
        }

    }

    public MeasurementType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getScope() {
        return this.scope;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public double getStartTimeInSeconds() {
        return (double)this.startTime / 1000.0D;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public double getEndTimeInSeconds() {
        return (double)this.endTime / 1000.0D;
    }

    public long getExclusiveTime() {
        return this.exclusiveTime;
    }

    public double getExclusiveTimeInSeconds() {
        return (double)this.exclusiveTime / 1000.0D;
    }

    public double asDouble() {
        throw new UnsupportedOperationException();
    }

    public ThreadInfo getThreadInfo() {
        return this.threadInfo;
    }

    public void setThreadInfo(ThreadInfo threadInfo) {
        this.threadInfo = threadInfo;
    }

    public boolean isInstantaneous() {
        return this.endTime == 0L;
    }

    public void finish() {
        if(this.finished) {
            throw new MeasurementException("Finish called on already finished Measurement");
        } else {
            this.finished = true;
        }
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

    public String toString() {
        return "BaseMeasurement{type=" + this.type + ", name='" + this.name + '\'' + ", scope='" + this.scope + '\'' + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", exclusiveTime=" + this.exclusiveTime + ", threadInfo=" + this.threadInfo + ", finished=" + this.finished + '}';
    }
}
