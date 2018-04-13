//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.harvest.type.HarvestableArray;
import com.google.gson.JsonArray;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class HttpTransactions extends HarvestableArray {

    private final Collection<HttpTransaction> httpTransactions = new CopyOnWriteArrayList<>();

    public synchronized void add(HttpTransaction httpTransaction) {
        this.httpTransactions.add(httpTransaction);
    }

    public synchronized void remove(HttpTransaction transaction) {
        this.httpTransactions.remove(transaction);
    }

    public void clear() {
        this.httpTransactions.clear();
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        Iterator var2 = this.httpTransactions.iterator();

        while(var2.hasNext()) {
            HttpTransaction transaction = (HttpTransaction)var2.next();
            array.add(transaction.asJson());
        }

        return array;
    }

    public Collection<HttpTransaction> getHttpTransactions() {
        return this.httpTransactions;
    }

    public int count() {
        return this.httpTransactions.size();
    }

    public String toString() {
        return "HttpTransactions{httpTransactions=" + this.httpTransactions + '}';
    }
}
