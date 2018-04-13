//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stores;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticAttributeStore;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SharedPrefsAnalyticAttributeStore extends SharedPrefsStore implements AnalyticAttributeStore {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final String STORE_FILE = "NRAnalyticAttributeStore";

    public SharedPrefsAnalyticAttributeStore(Context context) {
        super(context, STORE_FILE);
    }

    public SharedPrefsAnalyticAttributeStore(Context context, String storeFilename) {
        super(context, storeFilename);
    }

    public boolean store(AnalyticAttribute attribute) {
        synchronized(this) {
            if(attribute.isPersistent()) {
                Editor editor = this.sharedPrefs.edit();
                switch(attribute.getAttributeDataType().ordinal()) {
                    case 1:
                        log.verbose("SharedPrefsAnalyticAttributeStore.store - storing analytic attribute " + attribute.getName() + "=" + attribute.getStringValue());
                        editor.putString(attribute.getName(), attribute.getStringValue());
                        break;
                    case 2:
                        log.verbose("SharedPrefsAnalyticAttributeStore.store - storing analytic attribute " + attribute.getName() + "=" + attribute.getFloatValue());
                        editor.putFloat(attribute.getName(), attribute.getFloatValue());
                        break;
                    case 3:
                        log.verbose("SharedPrefsAnalyticAttributeStore.store - storing analytic attribute " + attribute.getName() + "=" + attribute.getBooleanValue());
                        editor.putBoolean(attribute.getName(), attribute.getBooleanValue());
                        break;
                    default:
                        log.error("SharedPrefsAnalyticAttributeStore.store - unsupported analytic attribute data type" + attribute.getName());
                        return false;
                }

                return this.applyOrCommitEditor(editor);
            } else {
                return false;
            }
        }
    }

    public List<AnalyticAttribute> fetchAll() {
        ArrayList<AnalyticAttribute> analyticAttributeArrayList = new ArrayList<>();
        Map<String, ?> storedAttributes = this.sharedPrefs.getAll();
        Iterator var3 = storedAttributes.entrySet().iterator();

        while(var3.hasNext()) {
            Entry entry = (Entry)var3.next();
            log.verbose("SharedPrefsAnalyticAttributeStore.fetchAll - found analytic attribute " + entry.getKey() + "=" + entry.getValue());
            if(entry.getValue() instanceof String) {
                analyticAttributeArrayList.add(new AnalyticAttribute(entry.getKey().toString(), entry.getValue().toString(), true));
            } else if(entry.getValue() instanceof Float) {
                analyticAttributeArrayList.add(new AnalyticAttribute(entry.getKey().toString(), Float.valueOf(entry.getValue().toString()), true));
            } else if(entry.getValue() instanceof Boolean) {
                analyticAttributeArrayList.add(new AnalyticAttribute(entry.getKey().toString(), Boolean.valueOf(entry.getValue().toString()), true));
            } else {
                log.error("SharedPrefsAnalyticAttributeStore.fetchAll - unsupported analytic attribute " + entry.getKey() + "=" + entry.getValue());
            }
        }

        return analyticAttributeArrayList;
    }

    public void delete(AnalyticAttribute attribute) {
        synchronized(this) {
            log.verbose("SharedPrefsAnalyticAttributeStore.delete - deleting attribute " + attribute.getName());
            super.delete(attribute.getName());
        }
    }
}
