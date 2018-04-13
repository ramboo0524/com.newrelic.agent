//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.StatsEngine;
import com.google.gson.JsonArray;
import java.text.MessageFormat;

public class AgentHealth extends HarvestableArray {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    public static final String DEFAULT_KEY = "Exception";
    protected final AgentHealthExceptions agentHealthExceptions = new AgentHealthExceptions();

    public AgentHealth() {
    }

    public static void noticeException(Exception exception) {
        AgentHealthException agentHealthException = null;
        if(exception != null) {
            agentHealthException = new AgentHealthException(exception);
        }

        noticeException(agentHealthException);
    }

    public static void noticeException(AgentHealthException exception) {
        noticeException(exception, "Exception");
    }

    public static void noticeException(AgentHealthException exception, String key) {
        if(exception != null) {
            StatsEngine statsEngine = StatsEngine.get();
            if(statsEngine != null) {
                if(key == null) {
                    log.warning("Passed metric key is null. Defaulting to Exception");
                }

                statsEngine.inc(MessageFormat.format("Supportability/AgentHealth/{0}/{1}/{2}/{3}", key == null?DEFAULT_KEY:key, exception.getSourceClass(), exception.getSourceMethod(), exception.getExceptionClass()));
                TaskQueue.queue(exception);
            } else {
                log.error("StatsEngine is null. Exception not recorded.");
            }
        } else {
            log.error("AgentHealthException is null. StatsEngine not updated");
        }

    }

    public void addException(AgentHealthException exception) {
        this.agentHealthExceptions.add(exception);
    }

    public void clear() {
        this.agentHealthExceptions.clear();
    }

    public JsonArray asJsonArray() {
        JsonArray data = new JsonArray();
        if(!this.agentHealthExceptions.isEmpty()) {
            data.add(this.agentHealthExceptions.asJsonObject());
        }

        return data;
    }
}
