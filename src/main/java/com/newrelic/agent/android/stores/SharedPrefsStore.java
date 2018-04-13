//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.util.Base64;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressLint({"NewApi"})
public abstract class SharedPrefsStore {
    protected static final AgentLog log = AgentLogManager.getAgentLog();
    protected static final Charset ENCODING;
    protected final SharedPreferences sharedPrefs;
    protected final String storeFilename;

    public SharedPrefsStore(Context context, String storeFilename) {
        this.sharedPrefs = context.getSharedPreferences(storeFilename, 0);
        this.storeFilename = storeFilename;
    }

    public String getStoreFilename() {
        return this.storeFilename;
    }

    public boolean store(String uuid, byte[] bytes) {
        try {
            Editor editor = this.sharedPrefs.edit();
            editor.putString(uuid, this.decodeBytesToString(bytes));
            return this.applyOrCommitEditor(editor);
        } catch (Exception var4) {
            log.error("SharedPrefsStore.store(String, byte[]): ", var4);
            return false;
        }
    }

    public boolean store(String uuid, Set<String> stringSet) {
        try {
            Editor editor = this.sharedPrefs.edit();
            editor.putStringSet(uuid, stringSet);
            return this.applyOrCommitEditor(editor);
        } catch (Exception var4) {
            log.error("SharedPrefsStore.store(String, Set<String>): ", var4);
            return false;
        }
    }

    public boolean store(String uuid, String string) {
        try {
            Editor editor = this.sharedPrefs.edit();
            editor.putString(uuid, string);
            return this.applyOrCommitEditor(editor);
        } catch (Exception var4) {
            log.error("SharedPrefsStore.store(String, String): ", var4);
            return false;
        }
    }

    public Map<String, ?> getAll() {
        HashMap objectList = new HashMap();

        try {
            synchronized(this) {
                objectList.putAll(this.sharedPrefs.getAll());
            }
        } catch (Exception var5) {
            log.error("SharedPrefsStore.fetchAll(): ", var5);
        }

        return objectList;
    }

    public List<?> fetchAll() {
        ArrayList objectList = new ArrayList();

        try {
            synchronized(this) {
                Map<String, ?> objectStrings = this.sharedPrefs.getAll();
                objectList.addAll(objectStrings.values());
            }
        } catch (Exception var6) {
            log.error("SharedPrefsStore.fetchAll(): ", var6);
        }

        return objectList;
    }

    public int count() {
        try {
            SharedPreferences var1 = this.sharedPrefs;
            synchronized(this.sharedPrefs) {
                return this.sharedPrefs.getAll().size();
            }
        } catch (Exception var4) {
            log.error("SharedPrefsStore.count(): ", var4);
            return 0;
        }
    }

    public void clear() {
        try {
            synchronized(this) {
                Editor editor = this.sharedPrefs.edit();
                editor.clear();
                this.applyOrCommitEditor(editor);
            }
        } catch (Exception var5) {
            log.error("SharedPrefsStore.clear(): ", var5);
        }

    }

    public void delete(String uuid) {
        try {
            synchronized(this) {
                Editor editor = this.sharedPrefs.edit();
                editor.remove(uuid);
                this.applyOrCommitEditor(editor);
            }
        } catch (Exception var6) {
            log.error("SharedPrefsStore.delete(): ", var6);
        }

    }

    protected String encodeBytes(byte[] bytes) {
        try {
            return Base64.encodeToString(bytes, 2);
        } catch (Exception var3) {
            log.error("SharedPrefsStore.encodeBytes(byte[]): ", var3);
            return null;
        }
    }

    protected byte[] decodeStringToBytes(String encodedString) {
        try {
            return Base64.decode(encodedString, 0);
        } catch (Exception var3) {
            log.error("SharedPrefsStore.decodeStringToBytes(String): ", var3);
            return null;
        }
    }

    protected String decodeBytesToString(byte[] decodedString) {
        try {
            return new String(decodedString, ENCODING);
        } catch (Exception var3) {
            log.error("SharedPrefsStore.decodeBytesToString(byte[]): ", var3);
            return null;
        }
    }

    @SuppressLint({"CommitPrefEdits"})
    protected boolean applyOrCommitEditor(Editor editor) {
        boolean result = true;

        try {
            if(VERSION.SDK_INT < 9) {
                result = editor.commit();
            } else {
                editor.apply();
            }
        } catch (Exception var4) {
            log.error("SharedPrefsStore.applyOrCommitEditor(SharedPreferences.Editor): ", var4);
        }

        return result;
    }

    static {
        ENCODING = StandardCharsets.ISO_8859_1;
    }
}
