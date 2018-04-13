//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.payload;

import java.util.List;

public interface PayloadStore<T> {
    boolean store(T var1);

    List<T> fetchAll();

    int count();

    void clear();

    void delete(T var1);
}
