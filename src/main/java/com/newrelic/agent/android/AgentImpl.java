//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.harvest.ApplicationInformation;
import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.EnvironmentInformation;
import com.newrelic.agent.android.util.Encoder;
import java.util.List;

public interface AgentImpl {

    void addTransactionData(TransactionData var1);

    List<TransactionData> getAndClearTransactionData();

    void mergeTransactionData(List<TransactionData> var1);

    String getCrossProcessId();

    int getStackTraceLimit();

    int getResponseBodyLimit();

    void start();

    void stop();

    void disable();

    boolean isDisabled();

    String getNetworkCarrier();

    String getNetworkWanType();

    void setLocation(String var1, String var2);

    Encoder getEncoder();

    DeviceInformation getDeviceInformation();

    ApplicationInformation getApplicationInformation();

    EnvironmentInformation getEnvironmentInformation();

    boolean updateSavedConnectInformation();

    long getSessionDurationMillis();

    boolean hasReachableNetworkConnection(String var1);
}
