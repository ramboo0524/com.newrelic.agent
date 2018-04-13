//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.crash;

import com.newrelic.agent.android.payload.PayloadStore;
import java.util.List;

public interface CrashStore extends PayloadStore<Crash> {
    boolean store(Crash var1);

    List<Crash> fetchAll();

    int count();

    void clear();

    void delete(Crash var1);
}
