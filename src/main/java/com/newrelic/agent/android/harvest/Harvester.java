//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.activity.config.ActivityTraceConfiguration;
import com.newrelic.agent.android.activity.config.ActivityTraceConfigurationDeserializer;
import com.newrelic.agent.android.analytics.AnalyticAttribute;
import com.newrelic.agent.android.analytics.AnalyticsControllerImpl;
import com.newrelic.agent.android.analytics.AnalyticsEvent;
import com.newrelic.agent.android.analytics.EventManager;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.stats.StatsEngine;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Harvester {
    private final AgentLog log = AgentLogManager.getAgentLog();
    private Harvester.State state;
    protected boolean stateChanged;
    private HarvestConnection harvestConnection;
    private AgentConfiguration agentConfiguration;
    private HarvestConfiguration configuration;
    private HarvestData harvestData;
    private final Collection<HarvestLifecycleAware> harvestListeners;

    public Harvester() {
        this.state = Harvester.State.UNINITIALIZED;
        this.configuration = HarvestConfiguration.getDefaultHarvestConfiguration();
        this.harvestListeners = new ArrayList<>();
    }

    public void start() {
        this.fireOnHarvestStart();
    }

    public void stop() {
        this.fireOnHarvestStop();
    }

    protected void uninitialized() {
        if(this.agentConfiguration == null) {
            this.log.error("Agent configuration unavailable.");
        } else {
            if(Agent.getImpl().updateSavedConnectInformation()) {
                this.configureHarvester(HarvestConfiguration.getDefaultHarvestConfiguration());
                this.harvestData.getDataToken().clear();
            }

            Harvest.setHarvestConnectInformation(new ConnectInformation(Agent.getApplicationInformation(), Agent.getDeviceInformation()));
            this.harvestConnection.setApplicationToken(this.agentConfiguration.getApplicationToken());
            this.harvestConnection.setCollectorHost(this.agentConfiguration.getCollectorHost());
            this.harvestConnection.useSsl(this.agentConfiguration.useSsl());
            this.transition(Harvester.State.DISCONNECTED);
            this.execute();
        }
    }

    protected void disconnected() {
        if(null == this.configuration) {
            this.configureHarvester(HarvestConfiguration.getDefaultHarvestConfiguration());
        }

        if(this.harvestData.isValid()) {
            this.log.verbose("Skipping connect call, saved state is available: " + this.harvestData.getDataToken());
            StatsEngine.get().sample("Session/Start", 1.0F);
            this.fireOnHarvestConnected();
            this.transition(Harvester.State.CONNECTED);
            this.execute();
        } else {
            this.log.info("Connecting, saved state is not available: " + this.harvestData.getDataToken());
            HarvestResponse response = this.harvestConnection.sendConnect();
            if(response == null) {
                this.log.error("Unable to connect to the Collector.");
            } else if(response.isOK()) {
                HarvestConfiguration configuration = this.parseHarvesterConfiguration(response);
                if(configuration == null) {
                    this.log.error("Unable to configure Harvester using Collector configuration.");
                } else {
                    this.configureHarvester(configuration);
                    StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Harvest", response.getResponseTime());
                    this.fireOnHarvestConnected();
                    this.transition(Harvester.State.CONNECTED);
                }
            } else {
                this.log.debug("Harvest connect response: " + response.getResponseCode());
                switch(response.getResponseCode().ordinal()) {
                    case 1:
                    case 2:
                        this.harvestData.getDataToken().clear();
                        this.fireOnHarvestDisconnected();
                        return;
                    case 3:
                        if(response.isDisableCommand()) {
                            this.log.error("Collector has commanded Agent to disable.");
                            this.fireOnHarvestDisabled();
                            this.transition(Harvester.State.DISABLED);
                            return;
                        }

                        this.log.error("Unexpected Collector response: FORBIDDEN");
                        break;
                    case 4:
                    case 5:
                        this.log.error("Invalid ConnectionInformation was sent to the Collector.");
                        break;
                    default:
                        this.log.error("An unknown error occurred when connecting to the Collector.");
                }

                this.fireOnHarvestError();
            }
        }
    }

    protected void connected() {
        this.log.info("Harvester: connected");
        this.log.info("Harvester: Sending " + this.harvestData.getHttpTransactions().count() + " HTTP transactions.");
        this.log.info("Harvester: Sending " + this.harvestData.getHttpErrors().count() + " HTTP errors.");
        this.log.info("Harvester: Sending " + this.harvestData.getActivityTraces().count() + " activity traces.");
        this.harvestData.setAnalyticsEnabled(this.agentConfiguration.getEnableAnalyticsEvents());
        if(this.agentConfiguration.getEnableAnalyticsEvents() && FeatureFlag.featureEnabled(FeatureFlag.AnalyticsEvents)) {
            EventManager eventManager = AnalyticsControllerImpl.getInstance().getEventManager();
            if(eventManager.isTransmitRequired()) {
                Set<AnalyticAttribute> sessionAttributes = new HashSet();
                sessionAttributes.addAll(AnalyticsControllerImpl.getInstance().getSystemAttributes());
                sessionAttributes.addAll(AnalyticsControllerImpl.getInstance().getUserAttributes());
                this.harvestData.setSessionAttributes(sessionAttributes);
                this.log.info("Harvester: Sending " + this.harvestData.getSessionAttributes().size() + " session attributes.");
                Collection<AnalyticsEvent> events = eventManager.getQueuedEvents();
                this.harvestData.setAnalyticsEvents(events);
                eventManager.empty();
            }

            this.log.info("Harvester: Sending " + this.harvestData.getAnalyticsEvents().size() + " analytics events.");
        }

        HarvestResponse response = this.harvestConnection.sendData(this.harvestData);
        if(response != null && !response.isUnknown()) {
            this.harvestData.reset();
            StatsEngine.get().sampleTimeMs("Supportability/AgentHealth/Collector/Harvest", response.getResponseTime());
            this.log.debug("Harvest data response: " + response.getResponseCode());
            this.log.debug("Harvest data response status code: " + response.getStatusCode());
            this.log.debug("Harvest data response BODY: " + response.getResponseBody());
            if(response.isError()) {
                this.fireOnHarvestError();
                switch(response.getResponseCode().ordinal()) {
                    case 1:
                    case 2:
                        this.harvestData.getDataToken().clear();
                        this.transition(Harvester.State.DISCONNECTED);
                        break;
                    case 3:
                        if(response.isDisableCommand()) {
                            this.log.error("Collector has commanded Agent to disable.");
                            this.transition(Harvester.State.DISABLED);
                        } else {
                            this.log.error("Unexpected Collector response: FORBIDDEN");
                            this.transition(Harvester.State.DISCONNECTED);
                        }
                        break;
                    case 4:
                    case 5:
                        this.log.error("Invalid ConnectionInformation was sent to the Collector.");
                        break;
                    default:
                        this.log.error("An unknown error occurred when connecting to the Collector.");
                }

            } else {
                HarvestConfiguration configuration = this.parseHarvesterConfiguration(response);
                if(configuration == null) {
                    this.log.error("Unable to configure Harvester using Collector configuration.");
                } else {
                    this.configureHarvester(configuration);
                    this.fireOnHarvestComplete();
                }
            }
        } else {
            this.fireOnHarvestSendFailed();
        }
    }

    protected void disabled() {
        Harvest.stop();
        this.fireOnHarvestDisabled();
    }

    protected void execute() {
        this.log.debug("Harvester state: " + this.state);
        this.stateChanged = false;

        try {
            this.expireHarvestData();
            switch(this.state.ordinal()) {
                case 1:
                    this.uninitialized();
                    break;
                case 2:
                    this.fireOnHarvestBefore();
                    this.disconnected();
                    break;
                case 3:
                    this.fireOnHarvestBefore();
                    this.fireOnHarvest();
                    this.fireOnHarvestFinalize();
                    TaskQueue.synchronousDequeue();
                    this.connected();
                    break;
                case 4:
                    this.disabled();
                    break;
                default:
                    throw new IllegalStateException();
            }
        } catch (Exception var2) {
            this.log.error("Exception encountered while attempting to harvest", var2);
            AgentHealth.noticeException(var2);
        }

    }

    protected void transition(Harvester.State newState) {
        if(this.stateChanged) {
            this.log.debug("Ignoring multiple transition: " + newState);
        } else if(this.state != newState) {
            switch(this.state) {
                case UNINITIALIZED:
                    if(!this.stateIn(newState, Harvester.State.DISCONNECTED, newState, Harvester.State.CONNECTED, Harvester.State.DISABLED)) {
                        throw new IllegalStateException();
                    }
                    break;
                case DISCONNECTED:
                    if(!this.stateIn(newState, Harvester.State.UNINITIALIZED, Harvester.State.CONNECTED, Harvester.State.DISABLED)) {
                        throw new IllegalStateException();
                    }
                    break;
                case CONNECTED:
                    if(!this.stateIn(newState, Harvester.State.DISCONNECTED, Harvester.State.DISABLED)) {
                        throw new IllegalStateException();
                    }
                    break;
                case DISABLED:
                default:
                    throw new IllegalStateException();
            }

            this.changeState(newState);
        }
    }

    private HarvestConfiguration parseHarvesterConfiguration(HarvestResponse response) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ActivityTraceConfiguration.class, new ActivityTraceConfigurationDeserializer());
        Gson gson = gsonBuilder.create();
        HarvestConfiguration config = null;

        try {
            config = gson.fromJson(response.getResponseBody(), HarvestConfiguration.class);
        } catch (JsonSyntaxException var6) {
            this.log.error("Unable to parse collector configuration: " + var6.getMessage());
            AgentHealth.noticeException(var6);
        }

        return config;
    }

    private void configureHarvester(HarvestConfiguration harvestConfiguration) {
        this.configuration.reconfigure(harvestConfiguration);
        this.harvestData.setDataToken(this.configuration.getDataToken());
        Harvest.setHarvestConfiguration(this.configuration);
    }

    private void changeState(Harvester.State newState) {
        this.log.debug("Harvester changing state: " + this.state + " -> " + newState);
        if(this.state == Harvester.State.CONNECTED) {
            if(newState == Harvester.State.DISCONNECTED) {
                this.fireOnHarvestDisconnected();
            } else if(newState == Harvester.State.DISABLED) {
                this.fireOnHarvestDisabled();
            }
        }

        this.state = newState;
        this.stateChanged = true;
    }

    private boolean stateIn(Harvester.State testState, Harvester.State... legalStates) {
        Harvester.State[] var3 = legalStates;
        int var4 = legalStates.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Harvester.State state = var3[var5];
            if(testState == state) {
                return true;
            }
        }

        return false;
    }

    public Harvester.State getCurrentState() {
        return this.state;
    }

    public boolean isDisabled() {
        return Harvester.State.DISABLED == this.state;
    }

    public void addHarvestListener(HarvestLifecycleAware harvestAware) {
        if(harvestAware == null) {
            this.log.error("Can't add null harvest listener");
            (new Exception()).printStackTrace();
        } else {
//            Collection var2 = this.harvestListeners;
            synchronized(this.harvestListeners) {
                if(!this.harvestListeners.contains(harvestAware)) {
                    this.harvestListeners.add(harvestAware);
                }
            }
        }
    }

    public void removeHarvestListener(HarvestLifecycleAware harvestAware) {
//        Collection var2 = this.harvestListeners;
        synchronized(this.harvestListeners) {
            if(this.harvestListeners.contains(harvestAware)) {
                this.harvestListeners.remove(harvestAware);
            }
        }
    }

    public void expireHarvestData() {
        this.expireHttpErrors();
        this.expireHttpTransactions();
        this.expireActivityTraces();
    }

    public void expireHttpErrors() {
        HttpErrors errors = this.harvestData.getHttpErrors();
        synchronized(errors) {
            Collection<HttpError> oldErrors = new ArrayList<>();
            long now = System.currentTimeMillis();
            long maxAge = this.configuration.getReportMaxTransactionAgeMilliseconds();
            Iterator var8 = errors.getHttpErrors().iterator();

            HttpError error;
            while(var8.hasNext()) {
                error = (HttpError)var8.next();
                if(error.getTimestamp() < now - maxAge) {
                    this.log.debug("HttpError too old, purging: " + error);
                    oldErrors.add(error);
                }
            }

            var8 = oldErrors.iterator();

            while(var8.hasNext()) {
                error = (HttpError)var8.next();
                errors.removeHttpError(error);
            }

        }
    }

    public void expireHttpTransactions() {
        HttpTransactions transactions = this.harvestData.getHttpTransactions();
        synchronized(transactions) {
            Collection<HttpTransaction> oldTransactions = new ArrayList<>();
            long now = System.currentTimeMillis();
            long maxAge = this.configuration.getReportMaxTransactionAgeMilliseconds();
            Iterator var8 = transactions.getHttpTransactions().iterator();

            HttpTransaction txn;
            while(var8.hasNext()) {
                txn = (HttpTransaction)var8.next();
                if(txn.getTimestamp() < now - maxAge) {
                    this.log.debug("HttpTransaction too old, purging: " + txn);
                    oldTransactions.add(txn);
                }
            }

            var8 = oldTransactions.iterator();

            while(var8.hasNext()) {
                txn = (HttpTransaction)var8.next();
                transactions.remove(txn);
            }

        }
    }

    public void expireActivityTraces() {
        ActivityTraces traces = this.harvestData.getActivityTraces();
        synchronized(traces) {
            Collection<ActivityTrace> expiredTraces = new ArrayList<>();
            long maxAttempts = (long)this.configuration.getActivity_trace_max_report_attempts();
            Iterator var6 = traces.getActivityTraces().iterator();

            ActivityTrace trace;
            while(var6.hasNext()) {
                trace = (ActivityTrace)var6.next();
                if(trace.getReportAttemptCount() >= maxAttempts) {
                    this.log.debug("ActivityTrace has had " + trace.getReportAttemptCount() + " report attempts, purging: " + trace);
                    expiredTraces.add(trace);
                }
            }

            var6 = expiredTraces.iterator();

            while(var6.hasNext()) {
                trace = (ActivityTrace)var6.next();
                traces.remove(trace);
            }

        }
    }

    public void setAgentConfiguration(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }

    public void setHarvestConnection(HarvestConnection connection) {
        this.harvestConnection = connection;
    }

    public HarvestConnection getHarvestConnection() {
        return this.harvestConnection;
    }

    public void setHarvestData(HarvestData harvestData) {
        this.harvestData = harvestData;
    }

    public HarvestData getHarvestData() {
        return this.harvestData;
    }

    private void fireOnHarvestBefore() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestBefore();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestBefore", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestStart() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestStart();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestStart", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestStop() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestStop();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestStop", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvest() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvest();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvest", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestFinalize() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestFinalize();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestFinalize", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestDisabled() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestDisabled();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestDisabled", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestDisconnected() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestDisconnected();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestDisconnected", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestError() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestError();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestError", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestSendFailed() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestSendFailed();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestSendFailed", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestComplete() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestComplete();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestComplete", var3);
            AgentHealth.noticeException(var3);
        }

    }

    private void fireOnHarvestConnected() {
        try {
            Iterator var1 = this.getHarvestListeners().iterator();

            while(var1.hasNext()) {
                HarvestLifecycleAware harvestAware = (HarvestLifecycleAware)var1.next();
                harvestAware.onHarvestConnected();
            }
        } catch (Exception var3) {
            this.log.error("Error in fireOnHarvestConnected", var3);
            AgentHealth.noticeException(var3);
        }

    }

    public void setConfiguration(HarvestConfiguration configuration) {
        this.configuration = configuration;
    }

    private Collection<HarvestLifecycleAware> getHarvestListeners() {
        return new ArrayList<>(this.harvestListeners);
    }

    protected enum State {
        UNINITIALIZED,
        DISCONNECTED,
        CONNECTED,
        DISABLED
    }
}
