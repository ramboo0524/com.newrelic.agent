//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.api.v2;

public interface TraceMachineInterface {
    long getCurrentThreadId();

    String getCurrentThreadName();

    boolean isUIThread();
}
