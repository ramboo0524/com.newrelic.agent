//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import com.newrelic.agent.android.Agent;

public class AgentBuildOptionsReporter {
    public AgentBuildOptionsReporter() {
    }

    public static void main(String[] args) {
        System.out.println("Agent version: " + Agent.getVersion());
        System.out.println("Unity instrumentation: " + Agent.getUnityInstrumentationFlag());
        System.out.println("Build ID: " + Agent.getBuildId());
    }
}
