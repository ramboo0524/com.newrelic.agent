//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.payload;

import java.util.ArrayList;
import java.util.List;

public class NullPayloadStore<T> implements PayloadStore<T> {
    public NullPayloadStore() {
    }

    public boolean store(T payload) {
        return true;
    }

    public List<T> fetchAll() {
        return new ArrayList<T>();
    }

    public int count() {
        return 0;
    }

    public void clear() {
    }

    public void delete(T payload) {
    }
}
