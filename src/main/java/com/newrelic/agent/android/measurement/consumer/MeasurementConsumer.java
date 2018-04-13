//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement.consumer;

import com.newrelic.agent.android.measurement.Measurement;
import com.newrelic.agent.android.measurement.MeasurementType;
import java.util.Collection;

public interface MeasurementConsumer {
    MeasurementType getMeasurementType();

    void consumeMeasurement(Measurement var1);

    void consumeMeasurements(Collection<Measurement> var1);
}
