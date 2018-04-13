//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import com.newrelic.agent.android.background.ApplicationStateMonitor;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(14)
public class ActivityLifecycleBackgroundListener extends UiBackgroundListener implements ActivityLifecycleCallbacks {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private AtomicBoolean isInBackground = new AtomicBoolean(false);

    public ActivityLifecycleBackgroundListener() {
    }

    public void onActivityResumed(Activity activity) {
        log.info("ActivityLifecycleBackgroundListener.onActivityResumed");
        if(this.isInBackground.getAndSet(false)) {
            Runnable runner = new Runnable() {
                public void run() {
                    ApplicationStateMonitor.getInstance().activityStarted();
                }
            };
            this.executor.submit(runner);
        }

    }

    public void onTrimMemory(int level) {
        log.info("ActivityLifecycleBackgroundListener.onTrimMemory level: " + level);
        if(20 == level) {
            this.isInBackground.set(true);
        }

        super.onTrimMemory(level);
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        log.info("ActivityLifecycleBackgroundListener.onActivityCreated");
        this.isInBackground.set(false);
    }

    public void onActivityDestroyed(Activity activity) {
        log.info("ActivityLifecycleBackgroundListener.onActivityDestroyed");
        this.isInBackground.set(false);
    }

    public void onActivityStarted(Activity activity) {
        if(this.isInBackground.compareAndSet(true, false)) {
            Runnable runner = new Runnable() {
                public void run() {
                    ActivityLifecycleBackgroundListener.log.debug("ActivityLifecycleBackgroundListener.onActivityStarted - notifying ApplicationStateMonitor");
                    ApplicationStateMonitor.getInstance().activityStarted();
                }
            };
            this.executor.submit(runner);
        }

    }

    public void onActivityPaused(Activity activity) {
        if(this.isInBackground.compareAndSet(false, true)) {
            Runnable runner = new Runnable() {
                public void run() {
                    ActivityLifecycleBackgroundListener.log.debug("ActivityLifecycleBackgroundListener.onActivityPaused - notifying ApplicationStateMonitor");
                    ApplicationStateMonitor.getInstance().uiHidden();
                }
            };
            this.executor.submit(runner);
        }

    }

    public void onActivityStopped(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
}
