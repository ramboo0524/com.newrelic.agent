//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.harvest.HarvestAdapter;
import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Collection;
import java.util.Iterator;

public class BaseMeasurementConsumer extends HarvestAdapter implements MeasurementConsumer {
    private final MeasurementType measurementType;

    public BaseMeasurementConsumer(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    public MeasurementType getMeasurementType() {
        return this.measurementType;
    }

    public void consumeMeasurement(Measurement measurement) {
    }

    public void consumeMeasurements(Collection<Measurement> measurements) {
        Iterator var2 = measurements.iterator();

        while(var2.hasNext()) {
            Measurement measurement = (Measurement)var2.next();
            this.consumeMeasurement(measurement);
        }

    }
}
