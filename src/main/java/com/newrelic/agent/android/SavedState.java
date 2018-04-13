//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.newrelic.agent.android.harvest.ApplicationInformation;
import com.newrelic.agent.android.harvest.ConnectInformation;
import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.harvest.HarvestAdapter;
import com.newrelic.agent.android.harvest.HarvestConfiguration;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class SavedState extends HarvestAdapter {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private final String PREFERENCE_FILE_PREFIX = "com.newrelic.android.agent.v1_";
    private final String PREF_MAX_TRANSACTION_COUNT = "maxTransactionCount";
    private final String PREF_MAX_TRANSACTION_AGE = "maxTransactionAgeInSeconds";
    private final String PREF_HARVEST_INTERVAL = "harvestIntervalInSeconds";
    private final String PREF_SERVER_TIMESTAMP = "serverTimestamp";
    private final String PREF_CROSS_PROCESS_ID = "crossProcessId";
    private final String PREF_DATA_TOKEN = "dataToken";
    private final String PREF_APP_TOKEN = "appToken";
    private final String PREF_STACK_TRACE_LIMIT = "stackTraceLimit";
    private final String PREF_RESPONSE_BODY_LIMIT = "responseBodyLimit";
    private final String PREF_COLLECT_NETWORK_ERRORS = "collectNetworkErrors";
    private final String PREF_ERROR_LIMIT = "errorLimit";
    private final String NEW_RELIC_AGENT_DISABLED_VERSION_KEY = "NewRelicAgentDisabledVersion";
    private final String PREF_ACTIVITY_TRACE_MIN_UTILIZATION = "activityTraceMinUtilization";
    private Float activityTraceMinUtilization;
    private final HarvestConfiguration configuration = new HarvestConfiguration();
    private final String PREF_APP_NAME = "appName";
    private final String PREF_APP_VERSION = "appVersion";
    private final String PREF_APP_BUILD = "appBuild";
    private final String PREF_PACKAGE_ID = "packageId";
    private final String PREF_VERSION_CODE = "versionCode";
    private final String PREF_AGENT_NAME = "agentName";
    private final String PREF_AGENT_VERSION = "agentVersion";
    private final String PREF_DEVICE_ARCHITECTURE = "deviceArchitecture";
    private final String PREF_DEVICE_ID = "deviceId";
    private final String PREF_DEVICE_MODEL = "deviceModel";
    private final String PREF_DEVICE_MANUFACTURER = "deviceManufacturer";
    private final String PREF_DEVICE_RUN_TIME = "deviceRunTime";
    private final String PREF_DEVICE_SIZE = "deviceSize";
    private final String PREF_OS_NAME = "osName";
    private final String PREF_OS_BUILD = "osBuild";
    private final String PREF_OS_VERSION = "osVersion";
    private final String PREF_PLATFORM = "platform";
    private final String PREF_PLATFORM_VERSION = "platformVersion";
    private final ConnectInformation connectInformation = new ConnectInformation(new ApplicationInformation(), new DeviceInformation());
    private final SharedPreferences prefs;
    private final Editor editor;
    private final Lock lock = new ReentrantLock();

    @SuppressLint({"CommitPrefEdits"})
    public SavedState(Context context) {
        this.prefs = context.getSharedPreferences(this.getPreferenceFileName(context.getPackageName()), 0);
        this.editor = this.prefs.edit();
        this.loadHarvestConfiguration();
        this.loadConnectInformation();
    }

    public void saveHarvestConfiguration(HarvestConfiguration newConfiguration) {
        if(!this.configuration.equals(newConfiguration)) {
            if(!newConfiguration.getDataToken().isValid()) {
                newConfiguration.setData_token(this.configuration.getData_token());
            }

            log.info("Saving configuration: " + newConfiguration);
            String newDataToken = newConfiguration.getDataToken().toJsonString();
            log.debug("!! saving data token: " + newDataToken);
            this.save(PREF_DATA_TOKEN, newDataToken);
            this.save(PREF_CROSS_PROCESS_ID, newConfiguration.getCross_process_id());
            this.save(PREF_SERVER_TIMESTAMP, newConfiguration.getServer_timestamp());
            this.save(PREF_HARVEST_INTERVAL, (long)newConfiguration.getData_report_period());
            this.save(PREF_MAX_TRANSACTION_AGE, (long)newConfiguration.getReport_max_transaction_age());
            this.save(PREF_MAX_TRANSACTION_COUNT, (long)newConfiguration.getReport_max_transaction_count());
            this.save(PREF_STACK_TRACE_LIMIT, newConfiguration.getStack_trace_limit());
            this.save(PREF_RESPONSE_BODY_LIMIT, newConfiguration.getResponse_body_limit());
            this.save(PREF_COLLECT_NETWORK_ERRORS, newConfiguration.isCollect_network_errors());
            this.save(PREF_ERROR_LIMIT, newConfiguration.getError_limit());
            this.saveActivityTraceMinUtilization((float)newConfiguration.getActivity_trace_min_utilization());
            this.loadHarvestConfiguration();
        }
    }

    public void loadHarvestConfiguration() {
        if(this.has(PREF_DATA_TOKEN)) {
            this.configuration.setData_token(this.getDataToken());
        }

        if(this.has(PREF_CROSS_PROCESS_ID)) {
            this.configuration.setCross_process_id(this.getCrossProcessId());
        }

        if(this.has(PREF_SERVER_TIMESTAMP)) {
            this.configuration.setServer_timestamp(this.getServerTimestamp());
        }

        if(this.has(PREF_HARVEST_INTERVAL)) {
            this.configuration.setData_report_period((int)this.getHarvestIntervalInSeconds());
        }

        if(this.has(PREF_MAX_TRANSACTION_AGE)) {
            this.configuration.setReport_max_transaction_age((int)this.getMaxTransactionAgeInSeconds());
        }

        if(this.has(PREF_MAX_TRANSACTION_COUNT)) {
            this.configuration.setReport_max_transaction_count((int)this.getMaxTransactionCount());
        }

        if(this.has(PREF_STACK_TRACE_LIMIT)) {
            this.configuration.setStack_trace_limit(this.getStackTraceLimit());
        }

        if(this.has(PREF_RESPONSE_BODY_LIMIT)) {
            this.configuration.setResponse_body_limit(this.getResponseBodyLimit());
        }

        if(this.has(PREF_COLLECT_NETWORK_ERRORS)) {
            this.configuration.setCollect_network_errors(this.isCollectingNetworkErrors());
        }

        if(this.has(PREF_ERROR_LIMIT)) {
            this.configuration.setError_limit(this.getErrorLimit());
        }

        if(this.has(PREF_ACTIVITY_TRACE_MIN_UTILIZATION)) {
            this.configuration.setActivity_trace_min_utilization((double)this.getActivityTraceMinUtilization());
        }

        log.info("Loaded configuration: " + this.configuration);
    }

    public void saveConnectInformation(ConnectInformation newConnectInformation) {
        if(!this.connectInformation.equals(newConnectInformation)) {
            this.saveApplicationInformation(newConnectInformation.getApplicationInformation());
            this.saveDeviceInformation(newConnectInformation.getDeviceInformation());
            this.loadConnectInformation();
        }
    }

    public void saveDeviceId(String deviceId) {
        this.save(PREF_DEVICE_ID, deviceId);
        this.connectInformation.getDeviceInformation().setDeviceId(deviceId);
    }

    public String getAppToken() {
        return this.getString(PREF_APP_TOKEN);
    }

    public void saveAppToken(String appToken) {
        this.save(PREF_APP_TOKEN, appToken);
    }

    private void saveApplicationInformation(ApplicationInformation applicationInformation) {
        this.save(PREF_APP_NAME, applicationInformation.getAppName());
        this.save(PREF_APP_VERSION, applicationInformation.getAppVersion());
        this.save(PREF_APP_BUILD, applicationInformation.getAppBuild());
        this.save(PREF_PACKAGE_ID, applicationInformation.getPackageId());
        this.save(PREF_VERSION_CODE, applicationInformation.getVersionCode());
    }

    private void saveDeviceInformation(DeviceInformation deviceInformation) {
        this.save(PREF_AGENT_NAME, deviceInformation.getAgentName());
        this.save(PREF_AGENT_VERSION, deviceInformation.getAgentVersion());
        this.save(PREF_DEVICE_ARCHITECTURE, deviceInformation.getArchitecture());
        this.save(PREF_DEVICE_ID, deviceInformation.getDeviceId());
        this.save(PREF_DEVICE_MODEL, deviceInformation.getModel());
        this.save(PREF_DEVICE_MANUFACTURER, deviceInformation.getManufacturer());
        this.save(PREF_DEVICE_RUN_TIME, deviceInformation.getRunTime());
        this.save(PREF_DEVICE_SIZE, deviceInformation.getSize());
        this.save(PREF_OS_NAME, deviceInformation.getOsName());
        this.save(PREF_OS_BUILD, deviceInformation.getOsBuild());
        this.save(PREF_OS_VERSION, deviceInformation.getOsVersion());
        this.save(PREF_PLATFORM, deviceInformation.getApplicationPlatform().toString());
        this.save(PREF_PLATFORM_VERSION, deviceInformation.getApplicationPlatformVersion());
    }

    public void loadConnectInformation() {
        ApplicationInformation applicationInformation = new ApplicationInformation();
        if(this.has(PREF_APP_NAME)) {
            applicationInformation.setAppName(this.getAppName());
        }

        if(this.has(PREF_APP_VERSION)) {
            applicationInformation.setAppVersion(this.getAppVersion());
        }

        if(this.has(PREF_APP_BUILD)) {
            applicationInformation.setAppBuild(this.getAppBuild());
        }

        if(this.has(PREF_PACKAGE_ID)) {
            applicationInformation.setPackageId(this.getPackageId());
        }

        if(this.has(PREF_VERSION_CODE)) {
            applicationInformation.setVersionCode(this.getVersionCode());
        }

        DeviceInformation deviceInformation = new DeviceInformation();
        if(this.has(PREF_AGENT_NAME)) {
            deviceInformation.setAgentName(this.getAgentName());
        }

        if(this.has(PREF_AGENT_VERSION)) {
            deviceInformation.setAgentVersion(this.getAgentVersion());
        }

        if(this.has(PREF_DEVICE_ARCHITECTURE)) {
            deviceInformation.setArchitecture(this.getDeviceArchitecture());
        }

        if(this.has(PREF_DEVICE_ID)) {
            deviceInformation.setDeviceId(this.getDeviceId());
        }

        if(this.has(PREF_DEVICE_MODEL)) {
            deviceInformation.setModel(this.getDeviceModel());
        }

        if(this.has(PREF_DEVICE_MANUFACTURER)) {
            deviceInformation.setManufacturer(this.getDeviceManufacturer());
        }

        if(this.has(PREF_DEVICE_RUN_TIME)) {
            deviceInformation.setRunTime(this.getDeviceRunTime());
        }

        if(this.has(PREF_DEVICE_SIZE)) {
            deviceInformation.setSize(this.getDeviceSize());
        }

        if(this.has(PREF_OS_NAME)) {
            deviceInformation.setOsName(this.getOsName());
        }

        if(this.has(PREF_OS_BUILD)) {
            deviceInformation.setOsBuild(this.getOsBuild());
        }

        if(this.has(PREF_OS_VERSION)) {
            deviceInformation.setOsVersion(this.getOsVersion());
        }

        if(this.has(PREF_PLATFORM)) {
            deviceInformation.setApplicationPlatform(this.getPlatform());
        }

        if(this.has(PREF_PLATFORM_VERSION)) {
            deviceInformation.setApplicationPlatformVersion(this.getPlatformVersion());
        }

        this.connectInformation.setApplicationInformation(applicationInformation);
        this.connectInformation.setDeviceInformation(deviceInformation);
    }

    public HarvestConfiguration getHarvestConfiguration() {
        return this.configuration;
    }

    public ConnectInformation getConnectInformation() {
        return this.connectInformation;
    }

    private boolean has(String key) {
        return this.prefs.contains(key);
    }

    public void onHarvestConnected() {
        this.saveHarvestConfiguration(Harvest.getHarvestConfiguration());
    }

    public void onHarvestComplete() {
        this.saveHarvestConfiguration(Harvest.getHarvestConfiguration());
    }

    public void onHarvestDisconnected() {
        log.info("Clearing harvest configuration.");
        this.clear();
    }

    public void onHarvestDisabled() {
        String agentVersion = Agent.getDeviceInformation().getAgentVersion();
        log.info("Disabling agent version " + agentVersion);
        this.saveDisabledVersion(agentVersion);
    }

    public void save(String key, String value) {
        this.lock.lock();

        try {
            this.editor.putString(key, value);
            this.editor.apply();
        } finally {
            this.lock.unlock();
        }

    }

    public void save(String key, boolean value) {
        this.lock.lock();

        try {
            this.editor.putBoolean(key, value);
            this.editor.apply();
        } finally {
            this.lock.unlock();
        }

    }

    public void save(String key, int value) {
        this.lock.lock();

        try {
            this.editor.putInt(key, value);
            this.editor.apply();
        } finally {
            this.lock.unlock();
        }

    }

    public void save(String key, long value) {
        this.lock.lock();

        try {
            this.editor.putLong(key, value);
            this.editor.apply();
        } finally {
            this.lock.unlock();
        }

    }

    public void save(String key, float value) {
        this.lock.lock();

        try {
            this.editor.putFloat(key, value);
            this.editor.apply();
        } finally {
            this.lock.unlock();
        }

    }

    public String getString(String key) {
        return !this.prefs.contains(key)?null:this.prefs.getString(key, (String)null);
    }

    public boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }

    public long getLong(String key) {
        return this.prefs.getLong(key, 0L);
    }

    public int getInt(String key) {
        return this.prefs.getInt(key, 0);
    }

    public Float getFloat(String key) {
        if(!this.prefs.contains(key)) {
            return null;
        } else {
            float f = this.prefs.getFloat(key, 0.0F);
            return  ( f * 100.0F) / 100.0F;
        }
    }

    public String getDisabledVersion() {
        return this.getString(NEW_RELIC_AGENT_DISABLED_VERSION_KEY);
    }

    public void saveDisabledVersion(String version) {
        this.save(NEW_RELIC_AGENT_DISABLED_VERSION_KEY, version);
    }

    public int[] getDataToken() {
        int[] dataToken = new int[2];
        String dataTokenString = this.getString(PREF_DATA_TOKEN);
        if(dataTokenString == null) {
            return null;
        } else {
            try {
                JSONTokener tokener = new JSONTokener(dataTokenString);
                if(tokener == null) {
                    return null;
                }

                JSONArray array = (JSONArray)tokener.nextValue();
                if(array == null) {
                    return null;
                }

                dataToken[0] = array.getInt(0);
                dataToken[1] = array.getInt(1);
            } catch (JSONException var5) {
                var5.printStackTrace();
            }

            return dataToken;
        }
    }

    public String getCrossProcessId() {
        return this.getString(PREF_CROSS_PROCESS_ID);
    }

    public boolean isCollectingNetworkErrors() {
        return this.getBoolean(PREF_COLLECT_NETWORK_ERRORS);
    }

    public long getServerTimestamp() {
        return this.getLong(PREF_SERVER_TIMESTAMP);
    }

    public long getHarvestInterval() {
        return this.getLong(PREF_HARVEST_INTERVAL);
    }

    public long getMaxTransactionAge() {
        return this.getLong(PREF_MAX_TRANSACTION_AGE);
    }

    public long getMaxTransactionCount() {
        return this.getLong(PREF_MAX_TRANSACTION_COUNT);
    }

    public int getStackTraceLimit() {
        return this.getInt(PREF_STACK_TRACE_LIMIT);
    }

    public int getResponseBodyLimit() {
        return this.getInt(PREF_RESPONSE_BODY_LIMIT);
    }

    public int getErrorLimit() {
        return this.getInt(PREF_ERROR_LIMIT);
    }

    public void saveActivityTraceMinUtilization(float activityTraceMinUtilization) {
        this.activityTraceMinUtilization = activityTraceMinUtilization;
        this.save(PREF_ACTIVITY_TRACE_MIN_UTILIZATION, activityTraceMinUtilization);
    }

    public float getActivityTraceMinUtilization() {
        if(this.activityTraceMinUtilization == null) {
            this.activityTraceMinUtilization = this.getFloat(PREF_ACTIVITY_TRACE_MIN_UTILIZATION);
        }

        return this.activityTraceMinUtilization;
    }

    public long getHarvestIntervalInSeconds() {
        return this.getHarvestInterval();
    }

    public long getMaxTransactionAgeInSeconds() {
        return this.getMaxTransactionAge();
    }

    public String getAppName() {
        return this.getString(PREF_APP_NAME);
    }

    public String getAppVersion() {
        return this.getString(PREF_APP_VERSION);
    }

    public int getVersionCode() {
        return this.getInt(PREF_VERSION_CODE);
    }

    public String getAppBuild() {
        return this.getString(PREF_APP_BUILD);
    }

    public String getPackageId() {
        return this.getString(PREF_PACKAGE_ID);
    }

    public String getAgentName() {
        return this.getString(PREF_AGENT_NAME);
    }

    public String getAgentVersion() {
        return this.getString(PREF_AGENT_VERSION);
    }

    public String getDeviceArchitecture() {
        return this.getString(PREF_DEVICE_ARCHITECTURE);
    }

    public String getDeviceId() {
        return this.getString(PREF_DEVICE_ID);
    }

    public String getDeviceModel() {
        return this.getString(PREF_DEVICE_MODEL);
    }

    public String getDeviceManufacturer() {
        return this.getString(PREF_DEVICE_MANUFACTURER);
    }

    public String getDeviceRunTime() {
        return this.getString(PREF_DEVICE_RUN_TIME);
    }

    public String getDeviceSize() {
        return this.getString(PREF_DEVICE_SIZE);
    }

    public String getOsName() {
        return this.getString(PREF_OS_NAME);
    }

    public String getOsBuild() {
        return this.getString(PREF_OS_BUILD);
    }

    public String getOsVersion() {
        return this.getString(PREF_OS_VERSION);
    }

    public String getApplicationPlatform() {
        return this.getString(PREF_PLATFORM);
    }

    public String getApplicationPlatformVersion() {
        return this.getString(PREF_PLATFORM_VERSION);
    }

    public ApplicationPlatform getPlatform() {
        ApplicationPlatform applicationPlatform = ApplicationPlatform.Native;

        try {
            applicationPlatform = ApplicationPlatform.valueOf(this.getString(PREF_PLATFORM));
        } catch (IllegalArgumentException var3) {
            ;
        }

        return applicationPlatform;
    }

    public String getPlatformVersion() {
        return this.getString(PREF_PLATFORM_VERSION);
    }

    private String getPreferenceFileName(String packageName) {
        return PREFERENCE_FILE_PREFIX + packageName;
    }

    public void clear() {
        this.lock.lock();

        try {
            this.editor.clear();
            this.editor.apply();
            this.configuration.setDefaultValues();
        } finally {
            this.lock.unlock();
        }

    }
}
