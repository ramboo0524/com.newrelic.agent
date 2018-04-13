//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

import com.newrelic.agent.android.instrumentation.MetricCategory;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.util.Util;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Trace {
    private static final String CATEGORY_PARAMETER = "category";
    private static final AgentLog log = AgentLogManager.getAgentLog();
    public final UUID parentUUID;
    public final UUID myUUID = new UUID(Util.getRandom().nextLong(), Util.getRandom().nextLong());
    public long entryTimestamp = 0L;
    public long exitTimestamp = 0L;
    public long exclusiveTime = 0L;
    public long childExclusiveTime = 0L;
    public String metricName;
    public String metricBackgroundName;
    public String displayName;
    public String scope;
    public long threadId = 0L;
    public String threadName = "main";
    private volatile Map<String, Object> params;
    private List<String> rawAnnotationParams;
    private volatile Set<UUID> children;
    private TraceType type;
    private boolean isComplete;
    public TraceMachine traceMachine;

    public Trace() {
        this.type = TraceType.TRACE;
        this.isComplete = false;
        this.parentUUID = null;
    }

    public Trace(String displayName, UUID parentUUID, TraceMachine traceMachine) {
        this.type = TraceType.TRACE;
        this.isComplete = false;
        this.displayName = displayName;
        this.parentUUID = parentUUID;
        this.traceMachine = traceMachine;
    }

    public void addChild(Trace trace) {
        if(this.children == null) {
            synchronized(this) {
                if(this.children == null) {
                    this.children = new HashSet<>();
                }
            }
        }

        this.children.add(trace.myUUID);
    }

    public Set<UUID> getChildren() {
        if(this.children == null) {
            synchronized(this) {
                if(this.children == null) {
                    this.children = new HashSet<>();
                }
            }
        }

        return this.children;
    }

    public Map<String, Object> getParams() {
        if(this.params == null) {
            synchronized(this) {
                if(this.params == null) {
                    this.params = new ConcurrentHashMap<>();
                }
            }
        }

        return this.params;
    }

    public void setAnnotationParams(List<String> rawAnnotationParams) {
        this.rawAnnotationParams = rawAnnotationParams;
    }

    public Map<String, Object> getAnnotationParams() {
        HashMap<String, Object> annotationParams = new HashMap<>();
        if(this.rawAnnotationParams != null && this.rawAnnotationParams.size() > 0) {
            Iterator i = this.rawAnnotationParams.iterator();

            while(i.hasNext()) {
                String paramName = (String)i.next();
                String paramClass = (String)i.next();
                String paramValue = (String)i.next();
                Object param = createParameter(paramName, paramClass, paramValue);
                if(param != null) {
                    annotationParams.put(paramName, param);
                }
            }
        }

        return annotationParams;
    }

    public boolean isComplete() {
        return this.isComplete;
    }

    public void complete() throws TracingInactiveException {
        if(this.isComplete) {
            log.warning("Attempted to double complete trace " + this.myUUID.toString());
        } else {
            if(this.exitTimestamp == 0L) {
                this.exitTimestamp = System.currentTimeMillis();
            }

            this.exclusiveTime = this.getDurationAsMilliseconds() - this.childExclusiveTime;
            this.isComplete = true;

            try {
                this.traceMachine.storeCompletedTrace(this);
            } catch (NullPointerException var2) {
                throw new TracingInactiveException();
            }
        }
    }

    public void prepareForSerialization() {
        this.getParams().put("type", this.type.toString());
    }

    public TraceType getType() {
        return this.type;
    }

    public void setType(TraceType type) {
        this.type = type;
    }

    public long getDurationAsMilliseconds() {
        return this.exitTimestamp - this.entryTimestamp;
    }

    public float getDurationAsSeconds() {
        return (float)(this.exitTimestamp - this.entryTimestamp) / 1000.0F;
    }

    public MetricCategory getCategory() {
        if(!this.getAnnotationParams().containsKey(Trace.CATEGORY_PARAMETER)) {
            return null;
        } else {
            Object category = this.getAnnotationParams().get(Trace.CATEGORY_PARAMETER);
            if(!(category instanceof MetricCategory)) {
                log.error("Category annotation parameter is not of type MetricCategory");
                return null;
            } else {
                return (MetricCategory)category;
            }
        }
    }

    private static Object createParameter(String parameterName, String parameterClass, String parameterValue) {
        Class clazz;
        try {
            clazz = Class.forName(parameterClass);
        } catch (ClassNotFoundException var5) {
            log.error("Unable to resolve parameter class in enterMethod: " + var5.getMessage(), var5);
            return null;
        }

        return MetricCategory.class == clazz?MetricCategory.valueOf(parameterValue):(String.class == clazz?parameterValue:null);
    }
}
