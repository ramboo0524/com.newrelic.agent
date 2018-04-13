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

public class HttpErrors extends HarvestableArray {
    private final Collection<HttpError> httpErrors = new CopyOnWriteArrayList<>();

    public HttpErrors() {
    }

    public void addHttpError(HttpError httpError) {
        synchronized(httpError) {
            Iterator var3 = this.httpErrors.iterator();

            HttpError error;
            do {
                if(!var3.hasNext()) {
                    this.httpErrors.add(httpError);
                    return;
                }

                error = (HttpError)var3.next();
            } while(!httpError.getHash().equals(error.getHash()));

            error.incrementCount();
        }
    }

    public synchronized void removeHttpError(HttpError error) {
        this.httpErrors.remove(error);
    }

    public void clear() {
        this.httpErrors.clear();
    }

    public JsonArray asJsonArray() {
        JsonArray array = new JsonArray();
        Iterator var2 = this.httpErrors.iterator();

        while(var2.hasNext()) {
            HttpError httpError = (HttpError)var2.next();
            array.add(httpError.asJson());
        }

        return array;
    }

    public Collection<HttpError> getHttpErrors() {
        return this.httpErrors;
    }

    public int count() {
        return this.httpErrors.size();
    }
}
