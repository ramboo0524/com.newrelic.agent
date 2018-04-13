//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.api.v1;

public interface Defaults {
    long MAX_TRANSACTION_COUNT = 1000L;
    long MAX_TRANSACTION_AGE_IN_SECONDS = 600L;
    long HARVEST_INTERVAL_IN_SECONDS = 60L;
    long MIN_HARVEST_DELTA_IN_SECONDS = 50L;
    long MIN_HTTP_ERROR_STATUS_CODE = 400L;
    boolean COLLECT_NETWORK_ERRORS = true;
    int ERROR_LIMIT = 10;
    int RESPONSE_BODY_LIMIT = 1024;
    int STACK_TRACE_LIMIT = 50;
    float ACTIVITY_TRACE_MIN_UTILIZATION = 0.3F;
}
