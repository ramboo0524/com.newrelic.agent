//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.payload;

import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PayloadReporter {
    protected static final AgentLog log = AgentLogManager.getAgentLog();
    protected final AtomicBoolean isEnabled = new AtomicBoolean(true);
    protected final AtomicBoolean isStarted = new AtomicBoolean(false);
    protected final AgentConfiguration agentConfiguration;

    public PayloadReporter(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    protected abstract void start();

    protected abstract void stop();

    public boolean isEnabled() {
        return this.isEnabled.get();
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled.set(enabled);
    }

    public AgentConfiguration getAgentConfiguration() {
        return this.agentConfiguration;
    }

    protected void reportSupportabilityMetrics() {
    }
}
