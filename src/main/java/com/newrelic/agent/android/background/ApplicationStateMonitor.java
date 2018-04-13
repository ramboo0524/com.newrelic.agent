//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.background;

import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.util.NamedThreadFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ApplicationStateMonitor implements Runnable {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private AtomicLong count;
    private AtomicLong snoozeStartTime;
    private final Lock snoozeLock;
    private final int activitySnoozeTimeInMilliseconds;
    protected final ArrayList<ApplicationStateListener> applicationStateListeners;
    protected AtomicBoolean foregrounded;
    private final Lock foregroundLock;
    private static ApplicationStateMonitor instance;
    protected final ScheduledThreadPoolExecutor executor;

    private ApplicationStateMonitor() {
        this(5, 5, TimeUnit.SECONDS, 5000);
    }

    ApplicationStateMonitor(int initialDelay, int period, TimeUnit timeUnit, int snoozeTimeInMilliseconds) {
        this.count = new AtomicLong(0L);
        this.snoozeStartTime = new AtomicLong(0L);
        this.snoozeLock = new ReentrantLock();
        this.applicationStateListeners = new ArrayList();
        this.foregrounded = new AtomicBoolean(true);
        this.foregroundLock = new ReentrantLock();
        this.executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("AppStateMon"));
        this.activitySnoozeTimeInMilliseconds = snoozeTimeInMilliseconds;
        this.executor.scheduleAtFixedRate(this, (long)initialDelay, (long)period, timeUnit);
        log.info("Application state monitor has started");
    }

    public static ApplicationStateMonitor getInstance() {
        if(instance == null) {
            instance = new ApplicationStateMonitor();
        }

        return instance;
    }

    public void addApplicationStateListener(ApplicationStateListener listener) {
//        ArrayList var2 = this.applicationStateListeners;
        synchronized(this.applicationStateListeners) {
            this.applicationStateListeners.add(listener);
        }
    }

    public void removeApplicationStateListener(ApplicationStateListener listener) {
//        ArrayList var2 = this.applicationStateListeners;
        synchronized(this.applicationStateListeners) {
            this.applicationStateListeners.remove(listener);
        }
    }

    public void run() {
        try {
            this.foregroundLock.lock();
            if(this.foregrounded.get() && this.getSnoozeTime() >= (long)this.activitySnoozeTimeInMilliseconds) {
                this.foregrounded.set(false);
                this.notifyApplicationInBackground();
            }
        } finally {
            this.foregroundLock.unlock();
        }

    }

    public void uiHidden() {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    ApplicationStateMonitor.this.foregroundLock.lock();
                    if(ApplicationStateMonitor.this.foregrounded.get()) {
                        ApplicationStateMonitor.log.info("UI has become hidden (app backgrounded)");
                        ApplicationStateMonitor.this.notifyApplicationInBackground();
                        ApplicationStateMonitor.this.foregrounded.set(false);
                    }
                } finally {
                    ApplicationStateMonitor.this.foregroundLock.unlock();
                }

            }
        };
        this.executor.execute(runner);
    }

    public void activityStopped() {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    ApplicationStateMonitor.this.foregroundLock.lock();

                    try {
                        ApplicationStateMonitor.this.snoozeLock.lock();
                        if(ApplicationStateMonitor.this.count.decrementAndGet() == 0L) {
                            ApplicationStateMonitor.this.snoozeStartTime.set(System.currentTimeMillis());
                        }
                    } finally {
                        ApplicationStateMonitor.this.snoozeLock.unlock();
                    }
                } finally {
                    ApplicationStateMonitor.this.foregroundLock.unlock();
                }

            }
        };
        this.executor.execute(runner);
    }

    public void activityStarted() {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    foregroundLock.lock();

                    try {
                        snoozeLock.lock();
                        if(count.incrementAndGet() == 1L) {
                            snoozeStartTime.set(0L);
                        }
                    } finally {
                        snoozeLock.unlock();
                    }

                    if(!foregrounded.get()) {
                        log.verbose("Application appears to be in the foreground");
                        foregrounded.set(true);
                        notifyApplicationInForeground();
                    }
                } finally {
                    foregroundLock.unlock();
                }

            }
        };
        this.executor.execute(runner);
    }

    private void notifyApplicationInBackground() {
        log.verbose("Application appears to have gone to the background");
        ArrayList var1 = this.applicationStateListeners;
        ArrayList listeners;
        synchronized(this.applicationStateListeners) {
            listeners = new ArrayList<>(this.applicationStateListeners);
        }

        ApplicationStateEvent e = new ApplicationStateEvent(this);
        Iterator var3 = listeners.iterator();

        while(var3.hasNext()) {
            ApplicationStateListener listener = (ApplicationStateListener)var3.next();
            listener.applicationBackgrounded(e);
        }

    }

    private void notifyApplicationInForeground() {
        ArrayList var1 = this.applicationStateListeners;
        ArrayList listeners;
        synchronized(this.applicationStateListeners) {
            listeners = new ArrayList<>(this.applicationStateListeners);
        }

        ApplicationStateEvent e = new ApplicationStateEvent(this);
        Iterator var3 = listeners.iterator();

        while(var3.hasNext()) {
            ApplicationStateListener listener = (ApplicationStateListener)var3.next();
            listener.applicationForegrounded(e);
        }

    }

    private long getSnoozeTime() {
        long snoozeValue = 0L;

        try {
            this.foregroundLock.lock();

            try {
                this.snoozeLock.lock();
                long snoozeTime = this.snoozeStartTime.get();
                if(snoozeTime != 0L) {
                    snoozeValue = System.currentTimeMillis() - snoozeTime;
                }
            } finally {
                this.snoozeLock.unlock();
            }
        } finally {
            this.foregroundLock.unlock();
        }

        return snoozeValue;
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public boolean getForegrounded() {
        return this.foregrounded.get();
    }

    public static boolean isAppInBackground() {
        return !getInstance().getForegrounded();
    }
}
