//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

public interface HarvestLifecycleAware {
    void onHarvestStart();

    void onHarvestStop();

    void onHarvestBefore();

    void onHarvest();

    void onHarvestFinalize();

    void onHarvestError();

    void onHarvestSendFailed();

    void onHarvestComplete();

    void onHarvestConnected();

    void onHarvestDisconnected();

    void onHarvestDisabled();
}
