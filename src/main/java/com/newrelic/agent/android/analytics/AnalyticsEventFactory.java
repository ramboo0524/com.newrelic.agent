//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import java.util.Set;

class AnalyticsEventFactory {

    static AnalyticsEvent createEvent(String name, AnalyticsEventCategory eventCategory, String eventType, Set<AnalyticAttribute> eventAttributes) {
        AnalyticsEvent event = null;
        switch(eventCategory) {
            case Session:
                event = new SessionEvent(eventAttributes);
                break;
            case RequestError:
                event = new NetworkRequestErrorEvent(eventAttributes);
                break;
            case Interaction:
                event = new InteractionEvent(name, eventAttributes);
                break;
            case Crash:
                event = new CrashEvent(name, eventAttributes);
                break;
            case Custom:
                event = new CustomEvent(name, eventType, eventAttributes);
                break;
            case Breadcrumb:
                event = new BreadcrumbEvent(name, eventAttributes);
                break;
            case NetworkRequest:
                event = new NetworkRequestEvent(eventAttributes);
        }

        return event;
    }

    private AnalyticsEventFactory() {
    }
}
