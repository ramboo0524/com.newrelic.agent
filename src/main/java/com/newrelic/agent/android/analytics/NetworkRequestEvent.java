//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import com.newrelic.agent.android.harvest.HttpTransaction;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class NetworkRequestEvent extends AnalyticsEvent {
    static final AgentLog log = AgentLogManager.getAgentLog();

    public NetworkRequestEvent(Set<AnalyticAttribute> attributeSet) {
        super(null, AnalyticsEventCategory.NetworkRequest, "MobileRequest", attributeSet);
    }

    public static NetworkRequestEvent createNetworkEvent(HttpTransaction txn) {
        Set<AnalyticAttribute> attributes = createDefaultAttributeSet(txn);
        attributes.add(new AnalyticAttribute("responseTime", Double.valueOf(txn.getTotalTime()).floatValue()));
        attributes.add(new AnalyticAttribute("statusCode", (float)txn.getStatusCode()));
        attributes.add(new AnalyticAttribute("bytesSent", Double.valueOf((double)txn.getBytesSent()).floatValue()));
        attributes.add(new AnalyticAttribute("bytesReceived", Double.valueOf((double)txn.getBytesReceived()).floatValue()));
        return new NetworkRequestEvent(attributes);
    }

    static Set<AnalyticAttribute> createDefaultAttributeSet(HttpTransaction txn) {
        HashSet attributes = new HashSet();

        try {
            URL url = new URL(txn.getUrl());
            attributes.add(new AnalyticAttribute("requestDomain", url.getHost()));
            attributes.add(new AnalyticAttribute("requestPath", url.getPath()));
        } catch (MalformedURLException var9) {
            log.error(txn.getUrl() + " is not a valid URL. Unable to set host or path attributes.");
        }

        attributes.add(new AnalyticAttribute("requestUrl", txn.getUrl()));
        attributes.add(new AnalyticAttribute("connectionType", txn.getWanType()));
        attributes.add(new AnalyticAttribute("requestMethod", txn.getHttpMethod()));
        double totalTime = txn.getTotalTime();
        if(totalTime != 0.0D) {
            attributes.add(new AnalyticAttribute("responseTime", Double.valueOf(totalTime).floatValue()));
        }

        double bytesSent = (double)txn.getBytesSent();
        if(bytesSent != 0.0D) {
            attributes.add(new AnalyticAttribute("bytesSent", Double.valueOf(bytesSent).floatValue()));
        }

        double bytesReceived = (double)txn.getBytesReceived();
        if(bytesReceived != 0.0D) {
            attributes.add(new AnalyticAttribute("bytesReceived", Double.valueOf(bytesReceived).floatValue()));
        }

        return attributes;
    }
}
