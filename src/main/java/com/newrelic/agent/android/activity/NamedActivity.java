//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.activity;

public class NamedActivity extends BaseMeasuredActivity {
    public NamedActivity(String activityName) {
        this.setName(activityName);
        this.setAutoInstrumented(false);
    }

    public void rename(String activityName) {
        this.setName(activityName);
    }
}
