//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.consumer.MeasurementConsumer;
import com.newrelic.agent.android.measurement.producer.BaseMeasurementProducer;
import com.newrelic.agent.android.measurement.producer.MeasurementProducer;
import com.newrelic.agent.android.util.ExceptionHelper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MeasurementPool extends BaseMeasurementProducer implements MeasurementConsumer {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private final Collection<MeasurementProducer> producers = new CopyOnWriteArrayList<>();
    private final Collection<MeasurementConsumer> consumers = new CopyOnWriteArrayList<>();

    public MeasurementPool() {
        super(MeasurementType.Any);
        this.addMeasurementProducer(this);
    }

    public void addMeasurementProducer(MeasurementProducer producer) {
        if(producer != null) {
            Collection var2 = this.producers;
            synchronized(this.producers) {
                if(this.producers.contains(producer)) {
                    log.debug("Attempted to add the same MeasurementProducer " + producer + "  multiple times.");
                    return;
                }

                this.producers.add(producer);
            }
        } else {
            log.debug("Attempted to add null MeasurementProducer.");
        }

    }

    public void removeMeasurementProducer(MeasurementProducer producer) {
        Collection var2 = this.producers;
        synchronized(this.producers) {
            if(!this.producers.contains(producer)) {
                log.debug("Attempted to remove MeasurementProducer " + producer + " which is not registered.");
            } else {
                this.producers.remove(producer);
            }
        }
    }

    public void addMeasurementConsumer(MeasurementConsumer consumer) {
        if(consumer != null) {
            Collection var2 = this.consumers;
            synchronized(this.consumers) {
                if(this.consumers.contains(consumer)) {
                    log.debug("Attempted to add the same MeasurementConsumer " + consumer + " multiple times.");
                    return;
                }

                this.consumers.add(consumer);
            }
        } else {
            log.debug("Attempted to add null MeasurementConsumer.");
        }

    }

    public void removeMeasurementConsumer(MeasurementConsumer consumer) {
        Collection var2 = this.consumers;
        synchronized(this.consumers) {
            if(!this.consumers.contains(consumer)) {
                log.debug("Attempted to remove MeasurementConsumer " + consumer + " which is not registered.");
            } else {
                this.consumers.remove(consumer);
            }
        }
    }

    public void broadcastMeasurements() {
        List<Measurement> allProducedMeasurements = new ArrayList<>();
        Collection var2 = this.producers;
        Iterator var3;
        synchronized(this.producers) {
            var3 = this.producers.iterator();

            while(var3.hasNext()) {
                MeasurementProducer producer = (MeasurementProducer)var3.next();
                Collection<Measurement> measurements = producer.drainMeasurements();
                if(measurements.size() > 0) {
                    allProducedMeasurements.addAll(measurements);

                    while(allProducedMeasurements.remove(null)) {
                        ;
                    }
                }
            }
        }

        if(allProducedMeasurements.size() > 0) {
            var2 = this.consumers;
            synchronized(this.consumers) {
                var3 = this.consumers.iterator();

                label53:
                while(var3.hasNext()) {
                    MeasurementConsumer consumer = (MeasurementConsumer)var3.next();
                    List<Measurement> measurements = new ArrayList<>(allProducedMeasurements);
                    Iterator var6 = measurements.iterator();

                    while(true) {
                        Measurement measurement;
                        do {
                            if(!var6.hasNext()) {
                                continue label53;
                            }

                            measurement = (Measurement)var6.next();
                        } while(consumer.getMeasurementType() != measurement.getType() && consumer.getMeasurementType() != MeasurementType.Any);

                        try {
                            consumer.consumeMeasurement(measurement);
                        } catch (Exception var10) {
                            ExceptionHelper.exceptionToErrorCode(var10);
                            log.error("broadcastMeasurements exception[" + var10.getClass().getName() + "]");
                        }
                    }
                }
            }
        }

    }

    public void consumeMeasurement(Measurement measurement) {
        this.produceMeasurement(measurement);
    }

    public void consumeMeasurements(Collection<Measurement> measurements) {
        this.produceMeasurements(measurements);
    }

    public MeasurementType getMeasurementType() {
        return MeasurementType.Any;
    }

    public Collection<MeasurementProducer> getMeasurementProducers() {
        return this.producers;
    }

    public Collection<MeasurementConsumer> getMeasurementConsumers() {
        return this.consumers;
    }
}
