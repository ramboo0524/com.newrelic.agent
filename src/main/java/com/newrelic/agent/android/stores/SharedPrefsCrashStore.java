//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stores;

import android.content.Context;
import com.newrelic.agent.android.crash.Crash;
import com.newrelic.agent.android.crash.CrashStore;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SharedPrefsCrashStore extends SharedPrefsStore implements CrashStore {
    private static final String STORE_FILE = "NRCrashStore";

    public SharedPrefsCrashStore(Context context) {
        this(context, "NRCrashStore");
    }

    public SharedPrefsCrashStore(Context context, String storeFilename) {
        super(context, storeFilename);
    }

    public boolean store(Crash crash) {
        synchronized(this) {
            JsonObject jsonObj = crash.asJsonObject();
            jsonObj.add("uploadCount", SafeJsonPrimitive.factory(crash.getUploadCount()));
            return this.store(crash.getUuid().toString(), jsonObj.toString());
        }
    }

    public List<Crash> fetchAll() {
        List<Crash> crashes = new ArrayList<>();
        Iterator var2 = super.fetchAll().iterator();

        while(var2.hasNext()) {
            Object object = var2.next();
            if(object instanceof String) {
                try {
                    crashes.add(Crash.crashFromJsonString((String)object));
                } catch (Exception var5) {
                    log.error("Exception encountered while deserializing crash", var5);
                }
            }
        }

        return crashes;
    }

    public void delete(Crash crash) {
        super.delete(crash.getUuid().toString());
    }
}
