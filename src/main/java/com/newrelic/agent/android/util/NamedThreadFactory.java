//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
    final ThreadGroup group;
    final String namePrefix;
    final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String factoryName) {
        SecurityManager s = System.getSecurityManager();
        this.group = s != null?s.getThreadGroup():Thread.currentThread().getThreadGroup();
        this.namePrefix = "NR_" + factoryName + "-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
        if(t.isDaemon()) {
            t.setDaemon(false);
        }

        if(t.getPriority() != 5) {
            t.setPriority(5);
        }

        return t;
    }
}
