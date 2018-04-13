//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.v2.TraceFieldInterface;
import com.newrelic.agent.android.api.v2.TraceMachineInterface;
import com.newrelic.agent.android.harvest.ActivityHistory;
import com.newrelic.agent.android.harvest.ActivitySighting;
import com.newrelic.agent.android.harvest.AgentHealth;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.HarvestAdapter;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.util.ExceptionHelper;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class TraceMachine extends HarvestAdapter {
    public static final String NR_TRACE_FIELD = "_nr_trace";
    public static final String NR_TRACE_TYPE = "Lcom/newrelic/agent/android/tracing/Trace;";
    public static final String ACTIVITY_METRIC_PREFIX = "Mobile/Activity/Name/";
    public static final String ACTIVITY_BACKGROUND_METRIC_PREFIX = "Mobile/Activity/Background/Name/";
    public static final String ACTIVTY_DISPLAY_NAME_PREFIX = "Display ";
    public static final AtomicBoolean enabled = new AtomicBoolean(true);
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final Object TRACE_MACHINE_LOCK = new Object();
    private static final Collection<TraceLifecycleAware> traceListeners = new CopyOnWriteArrayList<>();
    private static final ThreadLocal<Trace> threadLocalTrace = new ThreadLocal<>();
    private static final ThreadLocal<TraceMachine.TraceStack> threadLocalTraceStack = new ThreadLocal<>();
    private static final List<ActivitySighting> activityHistory = new CopyOnWriteArrayList<>();
    public static int HEALTHY_TRACE_TIMEOUT = 500;
    public static int UNHEALTHY_TRACE_TIMEOUT = '\uea60';
    private static TraceMachine traceMachine = null;
    private static TraceMachineInterface traceMachineInterface;
    private ActivityTrace activityTrace;

    protected static boolean isEnabled() {

        boolean b = enabled.get() && FeatureFlag.featureEnabled(FeatureFlag.InteractionTracing);
        android.util.Log.d("TraceMachine" , "isEnable [" + b + "]");
        return b;
    }

    protected TraceMachine(Trace rootTrace) {
        this.activityTrace = new ActivityTrace(rootTrace);
        Harvest.addHarvestListener(this);
    }

    public static TraceMachine getTraceMachine() {
        return traceMachine;
    }

    public static void addTraceListener(TraceLifecycleAware listener) {
        traceListeners.add(listener);
    }

    public static void removeTraceListener(TraceLifecycleAware listener) {
        traceListeners.remove(listener);
    }

    public static void setTraceMachineInterface(TraceMachineInterface traceMachineInterface) {
        TraceMachine.traceMachineInterface = traceMachineInterface;
    }

    public static void startTracing(String name) {
        startTracing(name, false);
    }

    public static void startTracing(String name, boolean customName) {
        startTracing(name, customName, false);
    }

    public static void startTracing(String name, boolean customName, boolean customInteraction) {
        try {
            if(!isEnabled()) {
                android.util.Log.d("TraceMachine" , "startTracing dissable");
                return;
            }

            if(!customInteraction && !FeatureFlag.featureEnabled(FeatureFlag.DefaultInteractions)) {
                android.util.Log.d("TraceMachine" , "startTracing second");
                return;
            }

            if(!Harvest.shouldCollectActivityTraces()) {
                android.util.Log.d("TraceMachine" , "startTracing shouldCollectActivityTraces");
                return;
            }

//            Object var3 = TRACE_MACHINE_LOCK;
            synchronized(TRACE_MACHINE_LOCK) {
                if(isTracingActive()) {
                    traceMachine.completeActivityTrace();
                }

                threadLocalTrace.remove();
                threadLocalTraceStack.set(new TraceMachine.TraceStack());
                Trace rootTrace = new Trace();
                if(customName) {
                    rootTrace.displayName = name;
                } else {
                    rootTrace.displayName = formatActivityDisplayName(name);
                }

                rootTrace.metricName = formatActivityMetricName(rootTrace.displayName);
                rootTrace.metricBackgroundName = formatActivityBackgroundMetricName(rootTrace.displayName);
                rootTrace.entryTimestamp = System.currentTimeMillis();
                log.debug("Started trace of " + name + ":" + rootTrace.myUUID.toString());
                traceMachine = new TraceMachine(rootTrace);
                rootTrace.traceMachine = traceMachine;
                pushTraceContext(rootTrace);
                traceMachine.activityTrace.previousActivity = getLastActivitySighting();
                activityHistory.add(new ActivitySighting(rootTrace.entryTimestamp, rootTrace.displayName));
                Iterator var5 = traceListeners.iterator();

                while(var5.hasNext()) {
                    TraceLifecycleAware listener = (TraceLifecycleAware)var5.next();
                    listener.onTraceStart(traceMachine.activityTrace);
                }
            }
        } catch (Exception var9) {
            log.error("Caught error while initializing TraceMachine, shutting it down", var9);
            AgentHealth.noticeException(var9);
            traceMachine = null;
            threadLocalTrace.remove();
            threadLocalTraceStack.remove();
        }

    }

    public static void haltTracing() {
        Object var0 = TRACE_MACHINE_LOCK;
        synchronized(TRACE_MACHINE_LOCK) {
            if(!isTracingInactive()) {
                TraceMachine finishedMachine = traceMachine;
                traceMachine = null;
                finishedMachine.activityTrace.discard();
                endLastActivitySighting();
                Harvest.removeHarvestListener(finishedMachine);
                threadLocalTrace.remove();
                threadLocalTraceStack.remove();
            }
        }
    }

    public static void endTrace() {
        traceMachine.completeActivityTrace();
    }

    public static void endTrace(String id) {
        try {
            if(getActivityTrace().rootTrace.myUUID.toString().equals(id)) {
                traceMachine.completeActivityTrace();
            }
        } catch (TracingInactiveException var2) {
            ;
        }

    }

    public static String formatActivityMetricName(String name) {
        return "Mobile/Activity/Name/" + name;
    }

    public static String formatActivityBackgroundMetricName(String name) {
        return "Mobile/Activity/Background/Name/" + name;
    }

    public static String formatActivityDisplayName(String name) {
        return "Display " + name;
    }

    private static Trace registerNewTrace(String name) throws TracingInactiveException {
        if(isTracingInactive()) {
            log.debug("Tried to register a new trace but tracing is inactive!");
            throw new TracingInactiveException();
        } else {
            Trace parentTrace = getCurrentTrace();
            Trace childTrace = new Trace(name, parentTrace.myUUID, traceMachine);

            try {
                traceMachine.activityTrace.addTrace(childTrace);
            } catch (Exception var4) {
                throw new TracingInactiveException();
            }

            log.verbose("Registering trace of " + name + " with parent " + parentTrace.displayName);
            parentTrace.addChild(childTrace);
            return childTrace;
        }
    }

    protected void completeActivityTrace() {
//        Object var1 = TRACE_MACHINE_LOCK;
        synchronized(TRACE_MACHINE_LOCK) {
            if(!isTracingInactive()) {
                TraceMachine finishedMachine = traceMachine;
                traceMachine = null;
                finishedMachine.activityTrace.complete();
                endLastActivitySighting();
                Iterator var3 = traceListeners.iterator();

                while(var3.hasNext()) {
                    TraceLifecycleAware listener = (TraceLifecycleAware)var3.next();
                    listener.onTraceComplete(finishedMachine.activityTrace);
                }

                Harvest.removeHarvestListener(finishedMachine);
            }
        }
    }

    public static void enterNetworkSegment(String name) {
        try {
            if(isTracingInactive()) {
                return;
            }

            Trace currentTrace = getCurrentTrace();
            if(currentTrace.getType() == TraceType.NETWORK) {
                exitMethod();
            }

            enterMethod(null, name, null);
            Trace networkTrace = getCurrentTrace();
            networkTrace.setType(TraceType.NETWORK);
        } catch (TracingInactiveException var3) {
            ;
        } catch (Exception var4) {
            log.error("Caught error while calling enterNetworkSegment()", var4);
            AgentHealth.noticeException(var4);
        }

    }

    public static void enterMethod(String name) {
        enterMethod(null, name, null);
    }

    public static void enterMethod(String name, ArrayList<String> annotationParams) {
        enterMethod(null, name, annotationParams);
    }

    public static void enterMethod(Trace trace, String name, ArrayList<String> annotationParams) {
        try {
            if(isTracingInactive()) {
                android.util.Log.d("TraceMachine" , "not tracingInactive");
                return;
            }

            long currentTime = System.currentTimeMillis();
            long lastUpdatedAt = traceMachine.activityTrace.lastUpdatedAt;
            long inception = traceMachine.activityTrace.startedAt;
            if(lastUpdatedAt + (long)HEALTHY_TRACE_TIMEOUT < currentTime && !traceMachine.activityTrace.hasMissingChildren()) {
                log.error(String.format("LastUpdated[%d] CurrentTime[%d] Trigger[%d]", lastUpdatedAt, currentTime, currentTime - lastUpdatedAt));
                log.debug("Completing activity trace after hitting healthy timeout (" + HEALTHY_TRACE_TIMEOUT + "ms)");
                traceMachine.completeActivityTrace();
                return;
            }

            if(inception + (long)UNHEALTHY_TRACE_TIMEOUT < currentTime) {
                log.debug("Completing activity trace after hitting unhealthy timeout (" + UNHEALTHY_TRACE_TIMEOUT + "ms)");
                traceMachine.completeActivityTrace();
                return;
            }

            loadTraceContext(trace);
            Trace childTrace = registerNewTrace(name);
            pushTraceContext(childTrace);
            childTrace.scope = getCurrentScope();
            childTrace.setAnnotationParams(annotationParams);
            Iterator var10 = traceListeners.iterator();

            while(var10.hasNext()) {
                TraceLifecycleAware listener = (TraceLifecycleAware)var10.next();
                listener.onEnterMethod();
            }

            childTrace.entryTimestamp = System.currentTimeMillis();
        } catch (TracingInactiveException var13) {
            ;
        } catch (Exception var14) {
            log.error("Caught error while calling enterMethod()", var14);
            AgentHealth.noticeException(var14);
        }

    }

    public static void exitMethod() {
        try {
            if(isTracingInactive()) {
                return;
            }

            Trace trace = threadLocalTrace.get();
            if(trace == null) {
                log.debug("threadLocalTrace is null");
                return;
            }

            trace.exitTimestamp = System.currentTimeMillis();
            if(trace.threadId == 0L && traceMachineInterface != null) {
                trace.threadId = traceMachineInterface.getCurrentThreadId();
                trace.threadName = traceMachineInterface.getCurrentThreadName();
            }

            Iterator var1 = traceListeners.iterator();

            while(var1.hasNext()) {
                TraceLifecycleAware listener = (TraceLifecycleAware)var1.next();
                listener.onExitMethod();
            }

            try {
                trace.complete();
            } catch (TracingInactiveException var3) {
                threadLocalTrace.remove();
                threadLocalTraceStack.remove();
                if(trace.getType() == TraceType.TRACE) {
                    TaskQueue.queue(trace);
                }

                return;
            }

            threadLocalTraceStack.get().pop();
            if(threadLocalTraceStack.get().empty()) {
                threadLocalTrace.set(null);
            } else {
                Trace parentTrace = threadLocalTraceStack.get().peek();
                threadLocalTrace.set(parentTrace);
                parentTrace.childExclusiveTime += trace.getDurationAsMilliseconds();
            }

            if(trace.getType() == TraceType.TRACE) {
                TaskQueue.queue(trace);
            }
        } catch (Exception var4) {
            log.error("Caught error while calling exitMethod()", var4);
            AgentHealth.noticeException(var4);
        }

    }

    private static void pushTraceContext(Trace trace) {
        if(!isTracingInactive() && trace != null) {
            TraceMachine.TraceStack traceStack = threadLocalTraceStack.get();
            if(traceStack.empty()) {
                traceStack.push(trace);
            } else if(traceStack.peek() != trace) {
                traceStack.push(trace);
            }

            threadLocalTrace.set(trace);
        }
    }

    private static void loadTraceContext(Trace trace) {
        if(!isTracingInactive()) {
            if(threadLocalTrace.get() == null) {
                threadLocalTrace.set(trace);
                threadLocalTraceStack.set(new TraceMachine.TraceStack());
                if(trace == null) {
                    return;
                }

                threadLocalTraceStack.get().push(trace);
            } else if(trace == null) {
                if(threadLocalTraceStack.get().isEmpty()) {
                    log.debug("No context to load!");
                    threadLocalTrace.set(null);
                    return;
                }

                trace = threadLocalTraceStack.get().peek();
                threadLocalTrace.set(trace);
            }

            log.verbose("Trace " + trace.myUUID.toString() + " is now active");
        }
    }

    public static void unloadTraceContext(Object object) {
        try {
            if(isTracingInactive()) {
                return;
            }

            if(traceMachineInterface != null && traceMachineInterface.isUIThread()) {
                return;
            }

            if(threadLocalTrace.get() != null) {
                log.verbose("Trace " + threadLocalTrace.get().myUUID.toString() + " is now inactive");
            }

            threadLocalTrace.remove();
            threadLocalTraceStack.remove();

            try {
                TraceFieldInterface tfi = (TraceFieldInterface)object;
                tfi._nr_setTrace((Trace)null);
            } catch (ClassCastException var2) {
                ExceptionHelper.recordSupportabilityMetric(var2, "TraceFieldInterface");
                log.error("Not a TraceFieldInterface: " + var2.getMessage());
            }
        } catch (Exception var3) {
            log.error("Caught error while calling unloadTraceContext()", var3);
            AgentHealth.noticeException(var3);
        }

    }

    public static Trace getCurrentTrace() throws TracingInactiveException {
        if(isTracingInactive()) {
            throw new TracingInactiveException();
        } else {
            Trace trace = (Trace)threadLocalTrace.get();
            return trace != null?trace:getRootTrace();
        }
    }

    public static Map<String, Object> getCurrentTraceParams() throws TracingInactiveException {
        return getCurrentTrace().getParams();
    }

    public static void setCurrentTraceParam(String key, Object value) {
        if(!isTracingInactive()) {
            try {
                getCurrentTrace().getParams().put(key, value);
            } catch (TracingInactiveException var3) {
                ;
            }
        }
    }

    public static void setCurrentDisplayName(String name) {
        Object var1 = TRACE_MACHINE_LOCK;
        synchronized(TRACE_MACHINE_LOCK) {
            traceMachine = getTraceMachine();
            if(traceMachine != null) {
                try {
                    getCurrentTrace().displayName = name;
                    Iterator var2 = traceListeners.iterator();

                    while(var2.hasNext()) {
                        TraceLifecycleAware listener = (TraceLifecycleAware)var2.next();

                        try {
                            listener.onTraceRename(traceMachine.activityTrace);
                        } catch (Exception var6) {
                            log.error("Cannot name trace. Tracing is not available: " + var6.toString());
                        }
                    }
                } catch (TracingInactiveException var7) {
                    return;
                }
            }

        }
    }

    public static void setRootDisplayName(String name) {
        if(!isTracingInactive()) {
            try {
                Trace rootTrace = getRootTrace();
                Measurements.renameActivity(rootTrace.displayName, name);
                renameActivityHistory(rootTrace.displayName, name);
                rootTrace.metricName = formatActivityMetricName(name);
                rootTrace.metricBackgroundName = formatActivityBackgroundMetricName(name);
                rootTrace.displayName = name;
                Trace currentTrace = getCurrentTrace();
                currentTrace.scope = getCurrentScope();
            } catch (TracingInactiveException var3) {
                ;
            }
        }
    }

    private static void renameActivityHistory(String oldName, String newName) {
        Iterator var2 = activityHistory.iterator();

        while(var2.hasNext()) {
            ActivitySighting activitySighting = (ActivitySighting)var2.next();
            if(activitySighting.getName().equals(oldName)) {
                activitySighting.setName(newName);
            }
        }

    }

    public static String getCurrentScope() {
        try {
            return isTracingInactive()?null:(traceMachineInterface != null && !traceMachineInterface.isUIThread()?traceMachine.activityTrace.rootTrace.metricBackgroundName:traceMachine.activityTrace.rootTrace.metricName);
        } catch (Exception var1) {
            log.error("Caught error while calling getCurrentScope()", var1);
            AgentHealth.noticeException(var1);
            return null;
        }
    }

    public static boolean isTracingActive() {
        return traceMachine != null;
    }

    public static boolean isTracingInactive() {
        return !isTracingActive();
    }

    public void storeCompletedTrace(Trace trace) {
        try {
            if(isTracingInactive()) {
                log.debug("Attempted to store a completed trace with no trace machine!");
                return;
            }

            this.activityTrace.addCompletedTrace(trace);
        } catch (Exception var3) {
            log.error("Caught error while calling storeCompletedTrace()", var3);
            AgentHealth.noticeException(var3);
        }

    }

    public static Trace getRootTrace() throws TracingInactiveException {
        try {
            return traceMachine.activityTrace.rootTrace;
        } catch (NullPointerException var1) {
            throw new TracingInactiveException();
        }
    }

    public static ActivityTrace getActivityTrace() throws TracingInactiveException {
        try {
            return traceMachine.activityTrace;
        } catch (NullPointerException var1) {
            throw new TracingInactiveException();
        }
    }

    public static ActivityHistory getActivityHistory() {
        return new ActivityHistory(activityHistory);
    }

    public static ActivitySighting getLastActivitySighting() {
        return activityHistory.isEmpty()?null:activityHistory.get(activityHistory.size() - 1);
    }

    public static void endLastActivitySighting() {
        ActivitySighting activitySighting = getLastActivitySighting();
        if(activitySighting != null) {
            activitySighting.end(System.currentTimeMillis());
        }

    }

    public static void clearActivityHistory() {
        activityHistory.clear();
    }

    public void onHarvestBefore() {
        if(isTracingActive()) {
            long currentTime = System.currentTimeMillis();
            long lastUpdatedAt = traceMachine.activityTrace.lastUpdatedAt;
            long inception = traceMachine.activityTrace.startedAt;
            if(lastUpdatedAt + (long)HEALTHY_TRACE_TIMEOUT < currentTime && !traceMachine.activityTrace.hasMissingChildren()) {
                log.debug("Completing activity trace after hitting healthy timeout (" + HEALTHY_TRACE_TIMEOUT + "ms)");
                this.completeActivityTrace();
                StatsEngine.get().inc("Supportability/AgentHealth/HealthyActivityTraces");
                return;
            }

            if(inception + (long)UNHEALTHY_TRACE_TIMEOUT < currentTime) {
                log.debug("Completing activity trace after hitting unhealthy timeout (" + UNHEALTHY_TRACE_TIMEOUT + "ms)");
                this.completeActivityTrace();
                StatsEngine.get().inc("Supportability/AgentHealth/UnhealthyActivityTraces");
                return;
            }
        } else {
            log.debug("TraceMachine is inactive");
        }

    }

    public void onHarvestSendFailed() {
        try {
            traceMachine.activityTrace.incrementReportAttemptCount();
        } catch (NullPointerException var2) {
            ;
        }

    }

    private static class TraceStack extends Stack<Trace> {
        private TraceStack() {
        }
    }
}
