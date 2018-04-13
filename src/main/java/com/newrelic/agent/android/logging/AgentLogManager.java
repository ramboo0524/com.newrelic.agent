//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.logging;

public class AgentLogManager {
    private static DefaultAgentLog instance = new DefaultAgentLog();

    public AgentLogManager() {
    }

    public static AgentLog getAgentLog() {
        return instance;
    }

    public static void setAgentLog(AgentLog instance) {
        AgentLogManager.instance.setImpl(instance);
    }
}
