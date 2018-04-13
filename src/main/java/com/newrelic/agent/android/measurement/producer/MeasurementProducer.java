//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.producer;

import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Collection;

public interface MeasurementProducer {
    MeasurementType getMeasurementType();

    void produceMeasurement(Measurement var1);

    void produceMeasurements(Collection<Measurement> var1);

    Collection<Measurement> drainMeasurements();
}
