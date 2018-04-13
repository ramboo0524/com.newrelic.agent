//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

public interface TraceLifecycleAware {
    void onEnterMethod();

    void onExitMethod();

    void onTraceStart(ActivityTrace var1);

    void onTraceComplete(ActivityTrace var1);

    void onTraceRename(ActivityTrace var1);
}
