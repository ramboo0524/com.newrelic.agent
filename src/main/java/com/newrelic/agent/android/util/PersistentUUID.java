//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.StatsEngine;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class PersistentUUID {
    private static final String UUID_KEY = "nr_uuid";
    private static final String UUID_FILENAME = "nr_installation";
    public static final String METRIC_UUID_RECOVERED = "UUIDRecovered";
    private static File UUID_FILE = new File(Environment.getDataDirectory(), UUID_FILENAME);
    private static AgentLog log = AgentLogManager.getAgentLog();

    public PersistentUUID(Context context) {
        UUID_FILE = new File(context.getFilesDir(), UUID_FILENAME);
    }

    public String getDeviceId(Context context) {
        String id = this.generateUniqueID(context);
        if(TextUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString();
        }

        return id;
    }

    private String generateUniqueID(Context context) {
        String hardwareDeviceId = Build.SERIAL;
        String androidDeviceId = Build.ID;

        String uuid;
        try {
            androidDeviceId = Secure.getString(context.getContentResolver(), "android_id");
            if(!TextUtils.isEmpty(androidDeviceId)) {
                try {
                    TelephonyManager tm = (TelephonyManager)context.getSystemService("phone");
                    if(tm != null) {
                        hardwareDeviceId = tm.getDeviceId();
                    }
                } catch (Exception var6) {
                    hardwareDeviceId = "badf00d";
                }

                if(TextUtils.isEmpty(hardwareDeviceId)) {
                    hardwareDeviceId = Build.HARDWARE + Build.DEVICE + Build.BOARD + Build.BRAND;
                }

                uuid = this.intToHexString(androidDeviceId.hashCode(), 8) + "-" + this.intToHexString(hardwareDeviceId.hashCode(), 4) + "-" + this.intToHexString(VERSION.SDK_INT, 4) + "-" + this.intToHexString(VERSION.RELEASE.hashCode(), 12);
                throw new RuntimeException("Not supported (TODO)");
            }

            uuid = UUID.randomUUID().toString();
        } catch (Exception var7) {
            uuid = UUID.randomUUID().toString();
        }

        return uuid;
    }

    private String intToHexString(int value, int sublen) {
        String result = "";
        String string = Integer.toHexString(value);
        int remain = sublen - string.length();
        char[] chars = new char[remain];
        Arrays.fill(chars, '0');
        string = new String(chars) + string;
        int count = 0;

        for(int i = string.length() - 1; i >= 0; --i) {
            ++count;
            result = string.substring(i, i + 1) + result;
            if(0 == count % sublen) {
                result = "-" + result;
            }
        }

        if(result.startsWith("-")) {
            result = result.substring(1, result.length());
        }

        return result;
    }

    protected void noticeUUIDMetric(String tag) {
        StatsEngine statsEngine = StatsEngine.get();
        if(statsEngine != null) {
            statsEngine.inc("Supportability/AgentHealth/" + tag);
        } else {
            log.error("StatsEngine is null. " + tag + "  not recorded.");
        }

    }

    public String getPersistentUUID() {
        String uuid = this.getUUIDFromFileStore();
        if(!TextUtils.isEmpty(uuid)) {
            this.noticeUUIDMetric(METRIC_UUID_RECOVERED);
        } else {
            uuid = UUID.randomUUID().toString();
            log.info("Created random UUID: " + uuid);
            StatsEngine.get().inc("Mobile/App/Install");
            AnalyticAttribute attribute = new AnalyticAttribute("install", true);
            AnalyticsControllerImpl.getInstance().addAttributeUnchecked(attribute, false);
            this.setPersistedUUID(uuid);
        }

        return uuid;
    }

    protected void setPersistedUUID(String uuid) {
        this.putUUIDToFileStore(uuid);
    }

    protected String getUUIDFromFileStore() {
        String uuid = "";
        if(UUID_FILE.exists()) {
            BufferedReader in = null;

            try {
                in = new BufferedReader(new FileReader(UUID_FILE));
                String uuidJson = in.readLine();
                JSONObject jsonObject = new JSONObject(uuidJson);
                uuid = jsonObject.getString(UUID_KEY);
            } catch (FileNotFoundException var19) {
                log.error(var19.getMessage());
            } catch (IOException var20) {
                log.error(var20.getMessage());
            } catch (JSONException var21) {
                log.error(var21.getMessage());
            } catch (NullPointerException var22) {
                log.error(var22.getMessage());
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var18) {
                        log.error(var18.getMessage());
                    }
                }

            }
        }

        return uuid;
    }

    protected void putUUIDToFileStore(String uuid) {
        BufferedWriter out = null;

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(UUID_KEY, uuid);
            out = new BufferedWriter(new FileWriter(UUID_FILE));
            out.write(jsonObject.toString());
            out.flush();
        } catch (IOException var14) {
            log.error(var14.getMessage());
        } catch (JSONException var15) {
            log.error(var15.getMessage());
        } finally {
            if(out != null) {
                try {
                    out.close();
                } catch (IOException var13) {
                    log.error(var13.getMessage());
                }
            }

        }

    }
}
