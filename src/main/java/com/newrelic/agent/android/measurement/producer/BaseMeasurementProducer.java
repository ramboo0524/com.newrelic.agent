//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.producer;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BaseMeasurementProducer implements MeasurementProducer {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private final MeasurementType producedMeasurementType;
    private final ArrayList<Measurement> producedMeasurements = new ArrayList<>();

    public BaseMeasurementProducer(MeasurementType measurementType) {
        this.producedMeasurementType = measurementType;
    }

    public MeasurementType getMeasurementType() {
        return this.producedMeasurementType;
    }

    public void produceMeasurement(Measurement measurement) {
        ArrayList var2 = this.producedMeasurements;
        synchronized(this.producedMeasurements) {
            if(measurement != null) {
                this.producedMeasurements.add(measurement);
            }

        }
    }

    public void produceMeasurements(Collection<Measurement> measurements) {
        ArrayList var2 = this.producedMeasurements;
        synchronized(this.producedMeasurements) {
            if(measurements != null) {
                this.producedMeasurements.addAll(measurements);

                while(true) {
                    if(this.producedMeasurements.remove(null)) {
                        continue;
                    }
                }
            }

        }
    }

    public Collection<Measurement> drainMeasurements() {
        ArrayList var1 = this.producedMeasurements;
        synchronized(this.producedMeasurements) {
            if(this.producedMeasurements.size() == 0) {
                return Collections.emptyList();
            } else {
                Collection<Measurement> measurements = new ArrayList(this.producedMeasurements);
                this.producedMeasurements.clear();
                return measurements;
            }
        }
    }
}
