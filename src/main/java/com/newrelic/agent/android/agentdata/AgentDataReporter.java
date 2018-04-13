//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.agentdata;

import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.HarvestLifecycleAware;
import com.newrelic.agent.android.payload.Payload;
import com.newrelic.agent.android.payload.PayloadController;
import com.newrelic.agent.android.payload.PayloadReporter;
import com.newrelic.agent.android.payload.PayloadSender;
import com.newrelic.agent.android.payload.PayloadStore;
import com.newrelic.agent.android.payload.PayloadSender.CompletionHandler;
import com.newrelic.agent.android.stats.StatsEngine;
import com.google.flatbuffers.FlatBufferBuilder;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class AgentDataReporter extends PayloadReporter implements HarvestLifecycleAware {
    protected static final AtomicReference<AgentDataReporter> instance = new AtomicReference(null);
    private static boolean reportExceptions = false;
    protected final PayloadStore<Payload> payloadStore;
    protected final Callable reportCachedAgentDataCallable = new Callable() {
        public Object call() throws Exception {
            AgentDataReporter.this.reportCachedAgentData();
            return null;
        }
    };

    public static AgentDataReporter getInstance() {
        return (AgentDataReporter)instance.get();
    }

    public static AgentDataReporter initialize(AgentConfiguration agentConfiguration) {
        instance.compareAndSet(null, new AgentDataReporter(agentConfiguration));
        reportExceptions = agentConfiguration.getReportHandledExceptions();
        return (AgentDataReporter)instance.get();
    }

    public static void shutdown() {
        if(isInitialized()) {
            try {
                ((AgentDataReporter)instance.get()).stop();
            } finally {
                instance.set(null);
            }
        }

    }

    public static boolean reportAgentData(byte[] bytes) {
        boolean reported = false;
        if(isInitialized()) {
            if(reportExceptions) {
                Payload payload = new Payload(bytes);
                ((AgentDataReporter)instance.get()).storeAndReportAgentData(payload);
                reported = true;
            }
        } else {
            log.error("AgentDataReporter not initialized");
        }

        return reported;
    }

    protected static boolean isInitialized() {
        return instance.get() != null;
    }

    protected AgentDataReporter(AgentConfiguration agentConfiguration) {
        super(agentConfiguration);
        this.payloadStore = agentConfiguration.getPayloadStore();
        this.isEnabled.set(agentConfiguration.getReportHandledExceptions());
    }

    public void start() {
        if(PayloadController.isInitialized()) {
            if(this.isEnabled() && this.isStarted.compareAndSet(false, true)) {
                PayloadController.submitCallable(this.reportCachedAgentDataCallable);
                Harvest.addHarvestListener(this);
            }
        } else {
            log.error("AgentDataReporter.start(): Must initialize PayloadController first.");
        }

    }

    public void stop() {
        Harvest.removeHarvestListener(this);
    }

    protected void reportCachedAgentData() {
        if(isInitialized()) {
            if(this.payloadStore != null) {
                Iterator var1 = this.payloadStore.fetchAll().iterator();

                while(var1.hasNext()) {
                    Payload payload = (Payload)var1.next();
                    if(!this.isPayloadStale(payload)) {
                        this.reportAgentData(payload);
                    }
                }
            }
        } else {
            log.error("AgentDataReporter not initialized");
        }

    }

    protected Future reportAgentData(Payload payload) {
        Future future = null;
        PayloadSender payloadSender = new AgentDataSender(payload, this.getAgentConfiguration());
        future = PayloadController.submitPayload(payloadSender, new CompletionHandler() {
            public void onResponse(PayloadSender payloadSender) {
                if(payloadSender.isSuccessfulResponse() && AgentDataReporter.this.payloadStore != null) {
                    AgentDataReporter.this.payloadStore.delete(payloadSender.getPayload());
                }

            }

            public void onException(PayloadSender payloadSender, Exception e) {
                AgentDataReporter.log.error("AgentDataReporter.reportAgentData(Payload): " + e);
            }
        });
        return future;
    }

    public Future storeAndReportAgentData(Payload payload) {
        if(this.payloadStore != null && payload.isPersisted() && this.payloadStore.store(payload)) {
            payload.setPersisted(false);
        }

        return this.reportAgentData(payload);
    }

    private boolean isPayloadStale(Payload payload) {
        if(payload.isStale((long)this.agentConfiguration.getPayloadTTL())) {
            this.payloadStore.delete(payload);
            log.info("Payload [" + payload.getUuid() + "] has become stale, and has been removed");
            StatsEngine.get().inc("Supportability/AgentHealth/Payload/Removed/Stale");
            return true;
        } else {
            return false;
        }
    }

    private byte[] submitBatchedPayload(final List<Payload> payloads) {
        if(!payloads.isEmpty()) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Iterator iter = payloads.iterator();

            while(iter.hasNext()) {
                try {
                    Payload payload = (Payload)iter.next();
                    output.write(payload.getBytes());
                } catch (Exception var7) {
                    ;
                }
            }

            FlatBufferBuilder flat = new FlatBufferBuilder(ByteBuffer.wrap(output.toByteArray()));
            ByteBuffer byteBuffer = flat.dataBuffer().slice();
            byte[] modifiedBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(modifiedBytes);
            PayloadSender agentDataSender = new AgentDataSender(byteBuffer.array(), ((AgentDataReporter)instance.get()).agentConfiguration);
            PayloadController.submitPayload(agentDataSender, new CompletionHandler() {
                public void onResponse(PayloadSender payloadSender) {
                    if(payloadSender.isSuccessfulResponse() && AgentDataReporter.this.payloadStore != null) {
                        Iterator var2 = payloads.iterator();

                        while(var2.hasNext()) {
                            Payload payload = (Payload)var2.next();
                            AgentDataReporter.this.payloadStore.delete(payload);
                        }
                    }

                }

                public void onException(PayloadSender payloadSender, Exception e) {
                    AgentDataReporter.log.error("AgentDataReporter.submitBatchedPayload(List<Payload>): " + e);
                }
            });
        }

        return null;
    }

    public void onHarvestStart() {
    }

    public void onHarvestStop() {
    }

    public void onHarvestBefore() {
    }

    public void onHarvest() {
        PayloadController.submitCallable(this.reportCachedAgentDataCallable);
    }

    public void onHarvestFinalize() {
    }

    public void onHarvestError() {
    }

    public void onHarvestSendFailed() {
    }

    public void onHarvestComplete() {
    }

    public void onHarvestConnected() {
    }

    public void onHarvestDisconnected() {
    }

    public void onHarvestDisabled() {
    }
}
