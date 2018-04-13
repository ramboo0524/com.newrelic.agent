//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.crash;

import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.payload.PayloadController;
import com.newrelic.agent.android.payload.PayloadReporter;
import com.newrelic.agent.android.payload.PayloadSender;
import com.newrelic.agent.android.payload.PayloadSender.CompletionHandler;
import com.newrelic.agent.android.stats.StatsEngine;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class CrashReporter extends PayloadReporter {
    protected static AtomicReference<CrashReporter> instance = new AtomicReference<>(null);
    private static boolean reportCrashes = false;
    private final UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler(this);
    protected final CrashStore crashStore;

    public static CrashReporter getInstance() {
        return instance.get();
    }

    public static CrashReporter initialize(AgentConfiguration agentConfiguration) {
        instance.compareAndSet(null, new CrashReporter(agentConfiguration));
        return instance.get();
    }

    public static void shutdown() {
        if(isInitialized()) {
            instance.get().stop();
            instance.set(null);
        }

    }

    public static void setReportCrashes(boolean reportCrashes) {
        if(isInitialized()) {
            CrashReporter var10000 = (CrashReporter)instance.get();
            reportCrashes = reportCrashes;
        }

    }

    public static UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return isInitialized()?(instance.get()).uncaughtExceptionHandler:null;
    }

    protected static boolean isInitialized() {
        return instance.get() != null;
    }

    protected CrashReporter(AgentConfiguration agentConfiguration) {
        super(agentConfiguration);
        this.crashStore = agentConfiguration.getCrashStore();
        this.isEnabled.set(agentConfiguration.getReportCrashes());
    }

    public void start() {
        if(isInitialized()) {
            if(this.isEnabled() && this.isStarted.compareAndSet(false, true)) {
                PayloadController.submitCallable(new Callable() {
                    public Void call() {
                        CrashReporter.this.reportSavedCrashes();
                        CrashReporter.this.reportSupportabilityMetrics();
                        return null;
                    }
                });
                this.uncaughtExceptionHandler.installExceptionHandler();
                reportCrashes = this.agentConfiguration.getReportCrashes();
            }
        } else {
            log.error("AgentDataReporter.start(): Must initialize PayloadController first.");
        }

    }

    protected void stop() {
    }

    protected void reportSavedCrashes() {
        if(this.crashStore != null) {
            Iterator var1 = this.crashStore.fetchAll().iterator();

            while(var1.hasNext()) {
                Crash crash = (Crash)var1.next();
                if(crash.isStale()) {
                    this.crashStore.delete(crash);
                    log.info("Crash [" + crash.getUuid().toString() + "] has become stale, and has been removed");
                    StatsEngine.get().inc("Supportability/AgentHealth/Crash/Removed/Stale");
                } else {
                    this.reportCrash(crash);
                }
            }
        }

    }

    protected Future reportCrash(final Crash crash) {
        if(reportCrashes) {
            if(crash != null) {
                CrashSender sender = new CrashSender(crash, this.agentConfiguration);
                CompletionHandler completionHandler = new CompletionHandler() {
                    public void onResponse(PayloadSender payloadSender) {
                        if(payloadSender.isSuccessfulResponse() && CrashReporter.this.crashStore != null) {
                            CrashReporter.this.crashStore.delete(crash);
                        }

                    }

                    public void onException(PayloadSender payloadSender, Exception e) {
                        CrashReporter.log.error("Crash upload failed: " + e);
                    }
                };
                return PayloadController.submitPayload(sender, completionHandler);
            }
        } else {
            log.warning("CrashReporter.reportCrash(Crash): attempted to report null crash.");
        }

        return null;
    }

    protected void storeAndReportCrash(Crash crash) {
        if(this.crashStore != null) {
            if(crash != null) {
                boolean stored = this.crashStore.store(crash);
                if(!stored) {
                    log.warning("CrashReporter.storeAndReportCrash(Crash): failed to store passed crash.");
                }
            } else {
                log.warning("CrashReporter.storeAndReportCrash(Crash): attempted to store null crash.");
            }
        } else {
            log.warning("CrashReporter.storeAndReportCrash(Crash): attempted to store crash without a crash store.");
        }

        try {
            Future future = this.reportCrash(crash);
            if(future != null) {
                future.get();
            }
        } catch (Exception var3) {
            log.warning("CrashReporter.storeAndReportCrash(Crash): " + var3);
        }

    }
}
