//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.res.Configuration;
import com.newrelic.agent.android.background.ApplicationStateMonitor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@TargetApi(14)
public class UiBackgroundListener implements ComponentCallbacks2 {
    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("UiBackgroundListener"));

    public UiBackgroundListener() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onLowMemory() {
    }

    public void onTrimMemory(int level) {
        switch(level) {
            case 20:
                Runnable runner = new Runnable() {
                    public void run() {
                        ApplicationStateMonitor.getInstance().uiHidden();
                    }
                };
                this.executor.submit(runner);
            default:
        }
    }
}
