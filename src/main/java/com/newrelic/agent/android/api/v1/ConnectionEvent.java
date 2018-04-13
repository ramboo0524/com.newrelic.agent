//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.api.v1;

import com.newrelic.agent.android.api.common.ConnectionState;
import java.util.EventObject;

public final class ConnectionEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private final ConnectionState connectionState;

    public ConnectionEvent(Object source) {
        this(source, null);
    }

    public ConnectionEvent(Object source, ConnectionState connectionState) {
        super(source);
        this.connectionState = connectionState;
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }
}
