//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.agentdata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HexAttributes {
    public static final String HEX_ATTR_APP_BUILD_ID = "appBuild";
    public static final String HEX_ATTR_APP_VERSION = "appVersion";
    public static final String HEX_ATTR_APP_UUID_HI = "appUuidHigh";
    public static final String HEX_ATTR_APP_UUID_LO = "appUuidLow";
    public static final String HEX_ATTR_SESSION_ID = "sessionId";
    public static final String HEX_ATTR_TIMESTAMP_MS = "timestampMs";
    public static final String HEX_ATTR_MESSAGE = "message";
    public static final String HEX_ATTR_CAUSE = "cause";
    public static final String HEX_ATTR_NAME = "name";
    public static final String HEX_ATTR_THREAD = "thread";
    public static final String HEX_ATTR_CLASS_NAME = "className";
    public static final String HEX_ATTR_METHOD_NAME = "methodName";
    public static final String HEX_ATTR_LINE_NUMBER = "lineNumber";
    public static final String HEX_ATTR_FILENAME = "fileName";
    public static final String HEX_ATTR_THREAD_CRASHED = "crashed";
    public static final String HEX_ATTR_THREAD_STATE = "state";
    public static final String HEX_ATTR_THREAD_NUMBER = "threadNumber";
    public static final String HEX_ATTR_THREAD_ID = "threadId";
    public static final String HEX_ATTR_THREAD_PRI = "priority";
    public static final Set<String> HEX_SESSION_ATTR_WHITELIST = new HashSet<>(Arrays.asList("osName", "osVersion", "osBuild", "osMajorVersion", "deviceManufacturer", "deviceModel", "uuid", "carrier", "newRelicVersion", "memUsageMb", "sessionId", "platform", "platformVersion", "runTime", "architecture", "appBuild"));
    public static final Set<String> HEX_REQUIRED_ATTRIBUTES = new HashSet<>(Arrays.asList("appBuild", "appUuidHigh", "appUuidLow", "sessionId", "message", "cause", "name", "timestampMs", "timeSinceLoad"));

    public HexAttributes() {
    }
}
