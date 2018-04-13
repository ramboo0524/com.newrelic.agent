//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import com.newrelic.agent.android.api.v2.TraceFieldInterface;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.tracing.TracingInactiveException;
import com.newrelic.agent.android.util.ExceptionHelper;
import java.util.concurrent.Executor;

public class AsyncTaskInstrumentation {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    protected AsyncTaskInstrumentation() {
    }

    @TargetApi(14)
    @ReplaceCallSite
    public static final <Params, Progress, Result> AsyncTask execute(AsyncTask<Params, Progress, Result> task, Params... params) {
        try {
            TraceFieldInterface tfi = (TraceFieldInterface)task;
            tfi._nr_setTrace(TraceMachine.getCurrentTrace());
        } catch (ClassCastException var3) {
            ExceptionHelper.recordSupportabilityMetric(var3, "TraceFieldInterface");
            log.error("Not a TraceFieldInterface: " + var3.getMessage());
        } catch (TracingInactiveException var4) {
            ;
        } catch (NoSuchFieldError var5) {
            ;
        }

        return task.execute(params);
    }

    @TargetApi(11)
    @ReplaceCallSite
    public static final <Params, Progress, Result> AsyncTask executeOnExecutor(AsyncTask<Params, Progress, Result> task, Executor exec, Params... params) {
        try {
            TraceFieldInterface tfi = (TraceFieldInterface)task;
            tfi._nr_setTrace(TraceMachine.getCurrentTrace());
        } catch (ClassCastException var4) {
            ExceptionHelper.recordSupportabilityMetric(var4, "TraceFieldInterface");
            log.error("Not a TraceFieldInterface: " + var4.getMessage());
        } catch (TracingInactiveException var5) {
            ;
        } catch (NoSuchFieldError var6) {
            ;
        }

        return task.executeOnExecutor(exec, params);
    }
}
