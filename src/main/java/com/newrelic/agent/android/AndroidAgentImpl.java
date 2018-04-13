//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.api.v1.ConnectionEvent;
import com.newrelic.agent.android.api.v1.ConnectionListener;
import com.newrelic.agent.android.api.v1.DeviceForm;
import com.newrelic.agent.android.api.v2.TraceMachineInterface;
import com.newrelic.agent.android.background.ApplicationStateEvent;
import com.newrelic.agent.android.background.ApplicationStateListener;
import com.newrelic.agent.android.background.ApplicationStateMonitor;
import com.newrelic.agent.android.harvest.AgentHealth;
import com.newrelic.agent.android.harvest.ApplicationInformation;
import com.newrelic.agent.android.harvest.ConnectInformation;
import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.EnvironmentInformation;
import com.newrelic.agent.android.harvest.Harvest;
import com.newrelic.agent.android.instrumentation.MetricCategory;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.metric.MetricUnit;
import com.newrelic.agent.android.payload.PayloadController;
import com.newrelic.agent.android.sample.MachineMeasurementConsumer;
import com.newrelic.agent.android.sample.Sampler;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.stores.SharedPrefsAnalyticAttributeStore;
import com.newrelic.agent.android.stores.SharedPrefsCrashStore;
import com.newrelic.agent.android.stores.SharedPrefsPayloadStore;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.util.ActivityLifecycleBackgroundListener;
import com.newrelic.agent.android.util.AndroidEncoder;
import com.newrelic.agent.android.util.Connectivity;
import com.newrelic.agent.android.util.Encoder;
import com.newrelic.agent.android.util.PersistentUUID;
import com.newrelic.agent.android.util.Reachability;
import com.newrelic.agent.android.util.UiBackgroundListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import proguard.canary.NewRelicCanary;

public class AndroidAgentImpl implements AgentImpl, ConnectionListener, ApplicationStateListener, TraceMachineInterface {
    private static final float LOCATION_ACCURACY_THRESHOLD = 500.0F;
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private final Context context;
    private SavedState savedState;
    private LocationListener locationListener;
    private final Lock lock = new ReentrantLock();
    private final Encoder encoder = new AndroidEncoder();
    private DeviceInformation deviceInformation;
    private ApplicationInformation applicationInformation;
    private final AgentConfiguration agentConfiguration;
    private MachineMeasurementConsumer machineMeasurementConsumer;
    private static final Comparator<TransactionData> cmp = new Comparator<TransactionData>() {
        public int compare(TransactionData lhs, TransactionData rhs) {
            return lhs.getTimestamp() > rhs.getTimestamp()?-1:(lhs.getTimestamp() < rhs.getTimestamp()?1:0);
        }
    };

    public AndroidAgentImpl(Context context, AgentConfiguration agentConfiguration) throws AgentInitializationException {
        this.context = appContext(context);
        this.agentConfiguration = agentConfiguration;
        this.savedState = new SavedState(this.context);
        if(this.isDisabled()) {
            throw new AgentInitializationException("This version of the agent has been disabled");
        } else {
            this.initApplicationInformation();
            if(agentConfiguration.useLocationService() && this.context.getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", this.getApplicationInformation().getPackageId()) == 0) {
                log.debug("Location stats enabled");
                this.addLocationListener();
            }

            TraceMachine.setTraceMachineInterface(this);
            agentConfiguration.setCrashStore(new SharedPrefsCrashStore(context));
            agentConfiguration.setPayloadStore(new SharedPrefsPayloadStore(context));
            agentConfiguration.setAnalyticAttributeStore(new SharedPrefsAnalyticAttributeStore(context));
            ApplicationStateMonitor.getInstance().addApplicationStateListener(this);
            if(VERSION.SDK_INT >= 14) {
                Object backgroundListener;
                if(Agent.getUnityInstrumentationFlag().equals("YES")) {
                    backgroundListener = new ActivityLifecycleBackgroundListener();
                    if(backgroundListener instanceof ActivityLifecycleCallbacks) {
                        try {
                            if(context.getApplicationContext() instanceof Application) {
                                Application application = (Application)context.getApplicationContext();
                                application.registerActivityLifecycleCallbacks((ActivityLifecycleCallbacks)backgroundListener);
                            }
                        } catch (Exception var5) {
                        }
                    }
                } else {
                    backgroundListener = new UiBackgroundListener();
                }

                context.registerComponentCallbacks((ComponentCallbacks)backgroundListener);
                this.setupSession();
            }

        }
    }

    protected void initialize() {
        this.setupSession();
        AnalyticsControllerImpl.getInstance();
        AnalyticsControllerImpl.initialize(this.agentConfiguration, this);
        Harvest.addHarvestListener(this.savedState);
        Harvest.initialize(this.agentConfiguration);
        Harvest.setHarvestConfiguration(this.savedState.getHarvestConfiguration());
        Harvest.setHarvestConnectInformation(this.savedState.getConnectInformation());
        Measurements.initialize();
        log.info(MessageFormat.format("New Relic Agent v{0}", Agent.getVersion()));
        log.verbose(MessageFormat.format("Application token: {0}", this.agentConfiguration.getApplicationToken()));
        this.machineMeasurementConsumer = new MachineMeasurementConsumer();
        Measurements.addMeasurementConsumer(this.machineMeasurementConsumer);
        StatsEngine.get().inc("Supportability/AgentHealth/UncaughtExceptionHandler/" + this.getUnhandledExceptionHandlerName());
        PayloadController.initialize(this.agentConfiguration);
        Sampler.init(this.context);
    }

    protected void setupSession() {
        TraceMachine.clearActivityHistory();
        this.agentConfiguration.provideSessionId();
    }

    protected void finalizeSession() {
    }

    public boolean updateSavedConnectInformation() {
        ConnectInformation savedConnectInformation = this.savedState.getConnectInformation();
        ConnectInformation newConnectInformation = new ConnectInformation(this.getApplicationInformation(), this.getDeviceInformation());
        String savedAppToken = this.savedState.getAppToken();
        if(newConnectInformation.equals(savedConnectInformation) && this.agentConfiguration.getApplicationToken().equals(savedAppToken)) {
            return false;
        } else {
            if(newConnectInformation.getApplicationInformation().isAppUpgrade(savedConnectInformation.getApplicationInformation())) {
                StatsEngine.get().inc("Mobile/App/Upgrade");
                AnalyticAttribute attribute = new AnalyticAttribute("upgradeFrom", savedConnectInformation.getApplicationInformation().getAppVersion());
                AnalyticsControllerImpl.getInstance().addAttributeUnchecked(attribute, false);
            }

            this.savedState.clear();
            this.savedState.saveConnectInformation(newConnectInformation);
            this.savedState.saveAppToken(this.agentConfiguration.getApplicationToken());
            return true;
        }
    }

    public DeviceInformation getDeviceInformation() {
        if(this.deviceInformation != null) {
            return this.deviceInformation;
        } else {
            DeviceInformation info = new DeviceInformation();
            info.setOsName("Android");
            info.setOsVersion(VERSION.RELEASE);
            info.setOsBuild(VERSION.INCREMENTAL);
            info.setModel(Build.MODEL);
            info.setAgentName("AndroidAgent");
            info.setAgentVersion(Agent.getVersion());
            info.setManufacturer(Build.MANUFACTURER);
            info.setDeviceId(this.getUUID());
            info.setArchitecture(System.getProperty("os.arch"));
            info.setRunTime(System.getProperty("java.vm.version"));
            info.setSize(deviceForm(this.context).name().toLowerCase(Locale.getDefault()));
            info.setApplicationPlatform(this.agentConfiguration.getApplicationPlatform());
            info.setApplicationPlatformVersion(this.agentConfiguration.getApplicationPlatformVersion());
            this.deviceInformation = info;
            return this.deviceInformation;
        }
    }

    public EnvironmentInformation getEnvironmentInformation() {
        EnvironmentInformation envInfo = new EnvironmentInformation();
        ActivityManager activityManager = (ActivityManager)this.context.getSystemService("activity");
        long[] free = new long[2];

        try {
            StatFs rootStatFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            StatFs externalStatFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            if(VERSION.SDK_INT >= 18) {
                free[0] = rootStatFs.getAvailableBlocksLong() * rootStatFs.getBlockSizeLong();
                free[1] = externalStatFs.getAvailableBlocksLong() * rootStatFs.getBlockSizeLong();
            } else {
                free[0] = (long)(rootStatFs.getAvailableBlocks() * rootStatFs.getBlockSize());
                free[1] = (long)(externalStatFs.getAvailableBlocks() * externalStatFs.getBlockSize());
            }
        } catch (Exception var9) {
            AgentHealth.noticeException(var9);
        } finally {
            if(free[0] < 0L) {
                free[0] = 0L;
            }

            if(free[1] < 0L) {
                free[1] = 0L;
            }

            envInfo.setDiskAvailable(free);
        }

        envInfo.setMemoryUsage(Sampler.sampleMemory(activityManager).getSampleValue().asLong());
        envInfo.setOrientation(this.context.getResources().getConfiguration().orientation);
        envInfo.setNetworkStatus(this.getNetworkCarrier());
        envInfo.setNetworkWanType(this.getNetworkWanType());
        return envInfo;
    }

    public void initApplicationInformation() throws AgentInitializationException {
        if(this.applicationInformation != null) {
            log.debug("attempted to reinitialize ApplicationInformation.");
        } else {
            String packageName = this.context.getPackageName();
            PackageManager packageManager = this.context.getPackageManager();
            PackageInfo packageInfo = null;

            try {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            } catch (NameNotFoundException var9) {
                throw new AgentInitializationException("Could not determine package version: " + var9.getMessage());
            }

            String appVersion = this.agentConfiguration.getCustomApplicationVersion();
            if(TextUtils.isEmpty(appVersion)) {
                if(packageInfo == null || packageInfo.versionName == null || packageInfo.versionName.length() <= 0) {
                    throw new AgentInitializationException("Your app doesn't appear to have a version defined. Ensure you have defined 'versionName' in your manifest.");
                }

                appVersion = packageInfo.versionName;
            }

            log.debug("Using application version " + appVersion);

            String appName;
            try {
                ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
                if(info != null) {
                    appName = packageManager.getApplicationLabel(info).toString();
                } else {
                    appName = packageName;
                }
            } catch (NameNotFoundException var7) {
                log.warning(var7.toString());
                appName = packageName;
            } catch (SecurityException var8) {
                log.warning(var8.toString());
                appName = packageName;
            }

            log.debug("Using application name " + appName);
            String build = this.agentConfiguration.getCustomBuildIdentifier();
            if(TextUtils.isEmpty(build)) {
                if(packageInfo != null) {
                    build = String.valueOf(packageInfo.versionCode);
                } else {
                    build = "";
                    log.warning("Your app doesn't appear to have a version code defined. Ensure you have defined 'versionCode' in your manifest.");
                }
            }

            log.debug("Using build " + build);
            this.applicationInformation = new ApplicationInformation(appName, appVersion, packageName, build);
            this.applicationInformation.setVersionCode(packageInfo.versionCode);
        }
    }

    public ApplicationInformation getApplicationInformation() {
        return this.applicationInformation;
    }

    public long getSessionDurationMillis() {
        return Harvest.getMillisSinceStart();
    }

    private static DeviceForm deviceForm(Context context) {
        int deviceSize = context.getResources().getConfiguration().screenLayout & 15;
        switch(deviceSize) {
            case 1:
                return DeviceForm.SMALL;
            case 2:
                return DeviceForm.NORMAL;
            case 3:
                return DeviceForm.LARGE;
            default:
                return deviceSize > 3?DeviceForm.XLARGE:DeviceForm.UNKNOWN;
        }
    }

    private static Context appContext(Context context) {
        return !(context instanceof Application)?context.getApplicationContext():context;
    }

    /** @deprecated */
    @Deprecated
    public void addTransactionData(TransactionData transactionData) {
    }

    /** @deprecated */
    @Deprecated
    public void mergeTransactionData(List<TransactionData> transactionDataList) {
    }

    /** @deprecated */
    @Deprecated
    public List<TransactionData> getAndClearTransactionData() {
        return null;
    }

    public String getCrossProcessId() {
        this.lock.lock();

        String var1;
        try {
            var1 = this.savedState.getCrossProcessId();
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public int getStackTraceLimit() {
        this.lock.lock();

        int var1;
        try {
            var1 = this.savedState.getStackTraceLimit();
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public int getResponseBodyLimit() {
        this.lock.lock();

        int var1;
        try {
            var1 = this.savedState.getHarvestConfiguration().getResponse_body_limit();
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    public void start() {
        if(!this.isDisabled()) {
            this.initialize();
            Harvest.start();
        } else {
            this.stop(false);
        }

    }

    public void stop() {
        this.stop(true);
    }

    private void stop(boolean finalSendData) {
        this.finalizeSession();
        Sampler.shutdown();
        TraceMachine.haltTracing();
        int eventsRecorded = AnalyticsControllerImpl.getInstance().getEventManager().getEventsRecorded();
        int eventsEjected = AnalyticsControllerImpl.getInstance().getEventManager().getEventsEjected();
        Measurements.addCustomMetric("Supportability/Events/Recorded", MetricCategory.NONE.name(), eventsRecorded, (double)eventsEjected, (double)eventsEjected, MetricUnit.OPERATIONS, MetricUnit.OPERATIONS);
        if(finalSendData) {
            if(this.isUIThread()) {
                StatsEngine.get().inc("Supportability/AgentHealth/HarvestOnMainThread");
            }

            Harvest.harvestNow();
        }

        AnalyticsControllerImpl.shutdown();
        TraceMachine.clearActivityHistory();
        Harvest.shutdown();
        Measurements.shutdown();
        PayloadController.shutdown();
    }

    public void disable() {
        log.warning("PERMANENTLY DISABLING AGENT v" + Agent.getVersion());

        try {
            this.savedState.saveDisabledVersion(Agent.getVersion());
        } finally {
            try {
                this.stop(false);
            } finally {
                Agent.setImpl(NullAgentImpl.instance);
            }
        }

    }

    public boolean isDisabled() {
        return Agent.getVersion().equals(this.savedState.getDisabledVersion());
    }

    public String getNetworkCarrier() {
        return Connectivity.carrierNameFromContext(this.context);
    }

    public String getNetworkWanType() {
        return Connectivity.wanType(this.context);
    }

    public static void init(Context context, AgentConfiguration agentConfiguration) {
        try {
            Agent.setImpl(new AndroidAgentImpl(context, agentConfiguration));
            Agent.start();
        } catch (AgentInitializationException var3) {
            log.error("Failed to initialize the agent: " + var3.toString());
        }
    }

    /** @deprecated */
    @Deprecated
    public void connected(ConnectionEvent e) {
        log.error("AndroidAgentImpl: connected ");
    }

    /** @deprecated */
    @Deprecated
    public void disconnected(ConnectionEvent e) {
        this.savedState.clear();
    }

    public void applicationForegrounded(ApplicationStateEvent e) {
        log.info("AndroidAgentImpl: application foregrounded ");
        this.start();
    }

    public void applicationBackgrounded(ApplicationStateEvent e) {
        log.info("AndroidAgentImpl: application backgrounded ");
        this.stop();
    }

    public void setLocation(String countryCode, String adminRegion) {
        if(countryCode == null || adminRegion == null) {
            throw new IllegalArgumentException("Country code and administrative region are required.");
        }
    }

    public void setLocation(Location location) {
        if(location == null) {
            throw new IllegalArgumentException("Location must not be null.");
        } else {
            Geocoder coder = new Geocoder(this.context);
            List addresses = null;

            try {
                addresses = coder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException var7) {
                log.error("Unable to geocode location: " + var7.toString());
            }

            if(addresses != null && addresses.size() != 0) {
                Address address = (Address)addresses.get(0);
                if(address != null) {
                    String countryCode = address.getCountryCode();
                    String adminArea = address.getAdminArea();
                    if(countryCode != null && adminArea != null) {
                        this.setLocation(countryCode, adminArea);
                        this.removeLocationListener();
                    }

                }
            }
        }
    }

    @SuppressLint({"MissingPermission"})
    private void addLocationListener() {
        LocationManager locationManager = (LocationManager)this.context.getSystemService("location");
        if(locationManager == null) {
            log.error("Unable to retrieve reference to LocationManager. Disabling location listener.");
        } else {
            this.locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if(AndroidAgentImpl.this.isAccurate(location)) {
                        AndroidAgentImpl.this.setLocation(location);
                    }

                }

                public void onProviderDisabled(String provider) {
                    if("passive".equals(provider)) {
                        AndroidAgentImpl.this.removeLocationListener();
                    }

                }

                public void onProviderEnabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            };
            locationManager.requestLocationUpdates("passive", 1000L, 0.0F, this.locationListener);
        }
    }

    @SuppressLint({"MissingPermission"})
    private void removeLocationListener() {
        if(this.locationListener != null) {
            LocationManager locationManager = (LocationManager)this.context.getSystemService("location");
            if(locationManager == null) {
                log.error("Unable to retrieve reference to LocationManager. Can't unregister location listener.");
            } else {
                synchronized(locationManager) {
                    locationManager.removeUpdates(this.locationListener);
                    this.locationListener = null;
                }
            }
        }
    }

    private boolean isAccurate(Location location) {
        return location != null && LOCATION_ACCURACY_THRESHOLD >= location.getAccuracy();
    }

    private String getUUID() {
        String uuid = this.savedState.getConnectInformation().getDeviceInformation().getDeviceId();
        if(TextUtils.isEmpty(uuid)) {
            PersistentUUID persistentUUID = new PersistentUUID(this.context);
            uuid = persistentUUID.getPersistentUUID();
            this.savedState.saveDeviceId(uuid);
        }

        return uuid;
    }

    private String getUnhandledExceptionHandlerName() {
        try {
            return Thread.getDefaultUncaughtExceptionHandler().getClass().getName();
        } catch (Exception var2) {
            return "unknown";
        }
    }

    public Encoder getEncoder() {
        return this.encoder;
    }

    public long getCurrentThreadId() {
        return Thread.currentThread().getId();
    }

    public boolean isUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

//    private void pokeCanary() {
//        NewRelicCanary.canaryMethod();
//    }

    protected SavedState getSavedState() {
        return this.savedState;
    }

    protected void setSavedState(SavedState savedState) {
        this.savedState = savedState;
    }

    public boolean hasReachableNetworkConnection(String reachableHost) {
        return Reachability.hasReachableNetworkConnection(this.context, reachableHost);
    }
}
