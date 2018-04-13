//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.crash;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.payload.PayloadController;
import com.newrelic.agent.android.stats.StatsEngine;
import java.util.concurrent.atomic.AtomicBoolean;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    protected static final AgentLog log = AgentLogManager.getAgentLog();
    protected final AtomicBoolean handledException = new AtomicBoolean(false);
    private final CrashReporter crashReporter;
    private java.lang.Thread.UncaughtExceptionHandler previousExceptionHandler = null;

    public UncaughtExceptionHandler(CrashReporter crashReporter) {
        this.crashReporter = crashReporter;
    }

    public void installExceptionHandler() {
        java.lang.Thread.UncaughtExceptionHandler currentExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        if(currentExceptionHandler != null) {
            if(currentExceptionHandler instanceof UncaughtExceptionHandler) {
                log.debug("New Relic crash handler already installed.");
                return;
            }

            this.previousExceptionHandler = currentExceptionHandler;
            log.debug("Installing New Relic crash handler and chaining " + this.previousExceptionHandler.getClass().getName());
        } else {
            log.debug("Installing New Relic crash handler.");
        }

        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if(!Agent.getUnityInstrumentationFlag().equals("YES") && !this.handledException.compareAndSet(false, true)) {
            StatsEngine.get().inc("Supportability/AgentHealth/Recursion/UncaughtExceptionHandler");
        } else {
            try {
                AgentConfiguration agentConfiguration = this.crashReporter.getAgentConfiguration();
                if(!this.crashReporter.isEnabled() || !FeatureFlag.featureEnabled(FeatureFlag.CrashReporting)) {
                    log.debug("A crash has been detected but crash reporting is disabled!");
                    this.chainExceptionHandler(thread, throwable);
                    return;
                }

                log.debug("A crash has been detected in " + thread.getStackTrace()[0].getClassName() + " and will be reported ASAP.");
                log.debug("Analytics data is currently " + (agentConfiguration.getEnableAnalyticsEvents()?"enabled ":"disabled"));
                AnalyticsControllerImpl analyticsController = AnalyticsControllerImpl.getInstance();
                analyticsController.setEnabled(true);
                long sessionDuration = Harvest.getMillisSinceStart();
                if(sessionDuration != 0L) {
                    analyticsController.setAttribute("sessionDuration", (float)sessionDuration / 1000.0F, false);
                }

                Crash crash = new Crash(throwable, analyticsController.getSessionAttributes(), analyticsController.getEventManager().getQueuedEvents(), agentConfiguration.getEnableAnalyticsEvents());
                this.crashReporter.storeAndReportCrash(crash);
                if(!Agent.getUnityInstrumentationFlag().equals("YES")) {
                    PayloadController.shutdown();
                }
            } finally {
                if(!Agent.getUnityInstrumentationFlag().equals("YES")) {
                    this.chainExceptionHandler(thread, throwable);
                }

            }

        }
    }

    protected void chainExceptionHandler(Thread thread, Throwable throwable) {
        if(this.previousExceptionHandler != null) {
            log.debug("Chaining crash reporting duties to " + this.previousExceptionHandler.getClass().getSimpleName());
            this.previousExceptionHandler.uncaughtException(thread, throwable);
        }

    }

    public void resetExceptionHandler() {
        if(this.previousExceptionHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(this.previousExceptionHandler);
            this.previousExceptionHandler = null;
            this.handledException.set(false);
        }

    }

    public java.lang.Thread.UncaughtExceptionHandler getPreviousExceptionHandler() {
        return this.previousExceptionHandler;
    }
}
