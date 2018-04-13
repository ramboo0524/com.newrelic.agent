//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.analytics;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.AgentConfiguration;
import com.newrelic.agent.android.AgentImpl;
import com.newrelic.agent.android.harvest.DeviceInformation;
import com.newrelic.agent.android.harvest.EnvironmentInformation;
import com.newrelic.agent.android.harvest.HttpTransaction;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.newrelic.agent.android.tracing.TraceLifecycleAware;
import com.newrelic.agent.android.tracing.TraceMachine;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnalyticsControllerImpl implements AnalyticsController {
    protected static final int MAX_ATTRIBUTES = 64;
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final AnalyticsControllerImpl instance = new AnalyticsControllerImpl();
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final List<String> reservedNames = new ArrayList();
    private static final String NEW_RELIC_PREFIX = "newRelic";
    private static final String NR_PREFIX = "nr.";
    private final ConcurrentLinkedQueue<AnalyticAttribute> systemAttributes = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<AnalyticAttribute> userAttributes = new ConcurrentLinkedQueue<>();
    private final EventManagerImpl eventManager = new EventManagerImpl();
    private final AtomicBoolean isEnabled = new AtomicBoolean(false);
    private final AnalyticsControllerImpl.InteractionCompleteListener listener = new AnalyticsControllerImpl.InteractionCompleteListener();
    private AgentImpl agentImpl;
    private AnalyticAttributeStore attributeStore;

    public static void initialize(AgentConfiguration agentConfiguration, AgentImpl agentImpl) {
        log.verbose("AnalyticsControllerImpl.initialize invoked.");
        if(!initialized.compareAndSet(false, true)) {
            log.verbose("AnalyticsControllerImpl has already been initialized.  Bypassing..");
        } else {
            instance.clear();
            reservedNames.add(AnalyticAttribute.EVENT_TYPE_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.TYPE_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.EVENT_TIMESTAMP_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.EVENT_CATEGORY_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.ACCOUNT_ID_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APP_ID_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APP_NAME_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.UUID_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.SESSION_ID_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.OS_NAME_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.OS_VERSION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.OS_MAJOR_VERSION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.DEVICE_MANUFACTURER_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.DEVICE_MODEL_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.MEM_USAGE_MB_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.CARRIER_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.NEW_RELIC_VERSION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.INTERACTION_DURATION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APP_INSTALL_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APP_UPGRADE_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APPLICATION_PLATFORM_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APPLICATION_PLATFORM_VERSION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.LAST_INTERACTION_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.OS_BUILD_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.RUNTIME_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.ARCHITECTURE_ATTRIBUTE);
            reservedNames.add(AnalyticAttribute.APP_BUILD_ATTRIBUTE);
            instance.reinitialize(agentConfiguration, agentImpl);
            TraceMachine.addTraceListener(instance.listener);
            log.info("Analytics Controller started.");
        }
    }

    public static void shutdown() {
        TraceMachine.removeTraceListener(instance.listener);
        instance.getEventManager().shutdown();
        initialized.compareAndSet(true, false);
    }

    private AnalyticsControllerImpl() {
    }

    void reinitialize(AgentConfiguration agentConfiguration, AgentImpl agentImpl) {
        this.agentImpl = agentImpl;
        this.eventManager.initialize();
        this.isEnabled.set(agentConfiguration.getEnableAnalyticsEvents());
        this.attributeStore = agentConfiguration.getAnalyticAttributeStore();
        this.loadPersistentAttributes();
        DeviceInformation deviceInformation = agentImpl.getDeviceInformation();
        String osVersion = deviceInformation.getOsVersion();
        osVersion = osVersion.replace(" ", "");
        String[] osMajorVersionArr = osVersion.split("[.:-]");
        String osMajorVersion;
        if(osMajorVersionArr.length > 0) {
            osMajorVersion = osMajorVersionArr[0];
        } else {
            osMajorVersion = osVersion;
        }

        EnvironmentInformation environmentInformation = agentImpl.getEnvironmentInformation();
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.OS_NAME_ATTRIBUTE, deviceInformation.getOsName()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.OS_VERSION_ATTRIBUTE, osVersion));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.OS_BUILD_ATTRIBUTE, deviceInformation.getOsBuild()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.OS_MAJOR_VERSION_ATTRIBUTE, osMajorVersion));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.DEVICE_MANUFACTURER_ATTRIBUTE, deviceInformation.getManufacturer()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.DEVICE_MODEL_ATTRIBUTE, deviceInformation.getModel()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.UUID_ATTRIBUTE, deviceInformation.getDeviceId()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.CARRIER_ATTRIBUTE, agentImpl.getNetworkCarrier()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.NEW_RELIC_VERSION_ATTRIBUTE, deviceInformation.getAgentVersion()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.MEM_USAGE_MB_ATTRIBUTE, (float)environmentInformation.getMemoryUsage()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.SESSION_ID_ATTRIBUTE, agentConfiguration.getSessionID()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.APPLICATION_PLATFORM_ATTRIBUTE, agentConfiguration.getApplicationPlatform().toString()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.APPLICATION_PLATFORM_VERSION_ATTRIBUTE, agentConfiguration.getApplicationPlatformVersion()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.RUNTIME_ATTRIBUTE, deviceInformation.getRunTime()));
        this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.ARCHITECTURE_ATTRIBUTE, deviceInformation.getArchitecture()));
        if(agentConfiguration.getCustomBuildIdentifier() != null) {
            this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.APP_BUILD_ATTRIBUTE, agentConfiguration.getCustomBuildIdentifier()));
        } else {
            String appBuildString = String.valueOf(Agent.getApplicationInformation().getVersionCode());
            if(!appBuildString.isEmpty()) {
                this.systemAttributes.add(new AnalyticAttribute(AnalyticAttribute.APP_BUILD_ATTRIBUTE, appBuildString));
            }
        }

    }

    public AnalyticAttribute getAttribute(String name) {
        log.verbose("AnalyticsControllerImpl.getAttribute - retrieving " + name);
        AnalyticAttribute attribute = this.getUserAttribute(name);
        if(attribute == null) {
            attribute = this.getSystemAttribute(name);
        }

        return attribute;
    }

    public Set<AnalyticAttribute> getSystemAttributes() {
        Set<AnalyticAttribute> attrs = new HashSet<>(this.systemAttributes.size());
        Iterator var2 = this.systemAttributes.iterator();

        while(var2.hasNext()) {
            AnalyticAttribute attr = (AnalyticAttribute)var2.next();
            attrs.add(new AnalyticAttribute(attr));
        }

        return Collections.unmodifiableSet(attrs);
    }

    public Set<AnalyticAttribute> getUserAttributes() {
        Set<AnalyticAttribute> attrs = new HashSet<>(this.userAttributes.size());
        Iterator var2 = this.userAttributes.iterator();

        while(var2.hasNext()) {
            AnalyticAttribute attr = (AnalyticAttribute)var2.next();
            attrs.add(new AnalyticAttribute(attr));
            if(attrs.size() == MAX_ATTRIBUTES) {
                break;
            }
        }

        return Collections.unmodifiableSet(attrs);
    }

    public Set<AnalyticAttribute> getSessionAttributes() {
        Set<AnalyticAttribute> attrs = new HashSet<>(this.getSessionAttributeCount());
        attrs.addAll(this.getSystemAttributes());
        attrs.addAll(this.getUserAttributes());
        return Collections.unmodifiableSet(attrs);
    }

    public int getSystemAttributeCount() {
        return this.systemAttributes.size();
    }

    public int getUserAttributeCount() {
        return Math.min(this.userAttributes.size(), MAX_ATTRIBUTES);
    }

    public int getSessionAttributeCount() {
        return this.systemAttributes.size() + this.userAttributes.size();
    }

    public boolean setAttribute(String name, String value) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value);
        return this.setAttribute(name, value, true);
    }

    public boolean setAttribute(String name, String value, boolean persistent) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value + (persistent?" (persistent)":" (transient)"));
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(this.isAttributeNameValid(name) && this.isStringValueValid(name, value)) {
            AnalyticAttribute attribute = this.getAttribute(name);
            if(attribute == null) {
                return this.addNewUserAttribute(new AnalyticAttribute(name, value, persistent));
            } else {
                attribute.setStringValue(value);
                attribute.setPersistent(persistent);
                if(attribute.isPersistent()) {
                    if(!this.attributeStore.store(attribute)) {
                        log.error("Failed to store attribute " + attribute + " to attribute store.");
                        return false;
                    }
                } else {
                    this.attributeStore.delete(attribute);
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean setAttribute(String name, float value) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value);
        return this.setAttribute(name, value, true);
    }

    public boolean setAttribute(String name, float value, boolean persistent) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value + (persistent?" (persistent)":" (transient)"));
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(!this.isAttributeNameValid(name)) {
            return false;
        } else {
            AnalyticAttribute attribute = this.getAttribute(name);
            if(attribute == null) {
                return this.addNewUserAttribute(new AnalyticAttribute(name, value, persistent));
            } else {
                attribute.setFloatValue(value);
                attribute.setPersistent(persistent);
                if(attribute.isPersistent()) {
                    if(!this.attributeStore.store(attribute)) {
                        log.error("Failed to store attribute " + attribute + " to attribute store.");
                        return false;
                    }
                } else {
                    this.attributeStore.delete(attribute);
                }

                return true;
            }
        }
    }

    public boolean setAttribute(String name, boolean value) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value);
        return this.setAttribute(name, value, true);
    }

    public boolean setAttribute(String name, boolean value, boolean persistent) {
        log.verbose("AnalyticsControllerImpl.setAttribute - " + name + ": " + value + (persistent?" (persistent)":" (transient)"));
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(!this.isAttributeNameValid(name)) {
            return false;
        } else {
            AnalyticAttribute attribute = this.getAttribute(name);
            if(attribute == null) {
                return this.addNewUserAttribute(new AnalyticAttribute(name, value, persistent));
            } else {
                attribute.setBooleanValue(value);
                attribute.setPersistent(persistent);
                if(attribute.isPersistent()) {
                    if(!this.attributeStore.store(attribute)) {
                        log.error("Failed to store attribute " + attribute + " to attribute store.");
                        return false;
                    }
                } else {
                    this.attributeStore.delete(attribute);
                }

                return true;
            }
        }
    }

    public boolean addAttributeUnchecked(AnalyticAttribute attribute, boolean persistent) {
        log.verbose("AnalyticsControllerImpl.setAttributeUnchecked - " + attribute.getName() + ": " + attribute.getStringValue() + (persistent?" (persistent)":" (transient)"));
        if(!initialized.get()) {
            log.warning("Analytics controller is not initialized!");
            return false;
        } else if(!this.isEnabled.get()) {
            log.warning("Analytics controller is not enabled!");
            return false;
        } else {
            String name = attribute.getName();
            if(!this.isNameValid(name)) {
                return false;
            } else if(attribute.isStringAttribute() && !this.isStringValueValid(name, attribute.getStringValue())) {
                return false;
            } else {
                AnalyticAttribute cachedAttribute = this.getSystemAttribute(name);
                if(cachedAttribute == null) {
                    this.systemAttributes.add(attribute);
                    if(attribute.isPersistent() && !this.attributeStore.store(attribute)) {
                        log.error("Failed to store attribute " + attribute + " to attribute store.");
                        return false;
                    }
                } else {
                    switch(attribute.getAttributeDataType().ordinal()) {
                        case 1:
                            cachedAttribute.setStringValue(attribute.getStringValue());
                            break;
                        case 2:
                            cachedAttribute.setFloatValue(attribute.getFloatValue());
                            break;
                        case 3:
                            cachedAttribute.setBooleanValue(attribute.getBooleanValue());
                    }

                    cachedAttribute.setPersistent(persistent);
                    if(cachedAttribute.isPersistent()) {
                        if(!this.attributeStore.store(cachedAttribute)) {
                            log.error("Failed to store attribute " + cachedAttribute + " to attribute store.");
                            return false;
                        }
                    } else {
                        this.attributeStore.delete(cachedAttribute);
                    }
                }

                return true;
            }
        }
    }

    public boolean incrementAttribute(String name, float value) {
        log.verbose("AnalyticsControllerImpl.incrementAttribute - " + name + ": " + value);
        return this.incrementAttribute(name, value, true);
    }

    public boolean incrementAttribute(String name, float value, boolean persistent) {
        log.verbose("AnalyticsControllerImpl.incrementAttribute - " + name + ": " + value + (persistent?" (persistent)":" (transient)"));
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(!this.isAttributeNameValid(name)) {
            return false;
        } else {
            AnalyticAttribute attribute = this.getAttribute(name);
            if(attribute != null && attribute.isFloatAttribute()) {
                attribute.setFloatValue(attribute.getFloatValue() + value);
                attribute.setPersistent(persistent);
                if(attribute.isPersistent() && !this.attributeStore.store(attribute)) {
                    log.error("Failed to store attribute " + attribute + " to attribute store.");
                    return false;
                } else {
                    return true;
                }
            } else if(attribute == null) {
                return this.addNewUserAttribute(new AnalyticAttribute(name, value, persistent));
            } else {
                log.warning("Cannot increment attribute " + name + ": the attribute is already defined as a non-float value.");
                return false;
            }
        }
    }

    public boolean removeAttribute(String name) {
        log.verbose("AnalyticsControllerImpl.removeAttribute - " + name);
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            AnalyticAttribute attribute = this.getAttribute(name);
            if(attribute != null) {
                this.userAttributes.remove(attribute);
                if(attribute.isPersistent()) {
                    this.attributeStore.delete(attribute);
                }
            }

            return true;
        }
    }

    public boolean removeAllAttributes() {
        log.verbose("AnalyticsControllerImpl.removeAttributes - ");
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            this.attributeStore.clear();
            this.userAttributes.clear();
            return false;
        }
    }

    public boolean addEvent(String name, Set<AnalyticAttribute> eventAttributes) {
        return this.addEvent(name, AnalyticsEventCategory.Custom, "Mobile", eventAttributes);
    }

    public boolean addEvent(String name, AnalyticsEventCategory eventCategory, String eventType, Set<AnalyticAttribute> eventAttributes) {
        log.verbose("AnalyticsControllerImpl.addEvent - " + name + ": category=" + eventCategory + ", eventType: " + eventType + ", eventAttributes:" + eventAttributes);
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            Set<AnalyticAttribute> validatedAttributes = new HashSet<>();
            Iterator var6 = eventAttributes.iterator();

            while(var6.hasNext()) {
                AnalyticAttribute attribute = (AnalyticAttribute)var6.next();
                if(this.isAttributeNameValid(attribute.getName())) {
                    validatedAttributes.add(attribute);
                }
            }

            AnalyticsEvent event = AnalyticsEventFactory.createEvent(name, eventCategory, eventType, validatedAttributes);
            return this.addEvent(event);
        }
    }

    public boolean addEvent(AnalyticsEvent event) {
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            Set<AnalyticAttribute> sessionAttributes = new HashSet<>();
            long sessionDuration = this.agentImpl.getSessionDurationMillis();
            if(0L == sessionDuration) {
                log.error("Harvest instance is not running! Session duration will be invalid");
            } else {
                sessionAttributes.add(new AnalyticAttribute("timeSinceLoad", (float)sessionDuration / 1000.0F));
                event.addAttributes(sessionAttributes);
            }

            return this.eventManager.addEvent(event);
        }
    }

    public int getMaxEventPoolSize() {
        return this.eventManager.getMaxEventPoolSize();
    }

    public void setMaxEventPoolSize(int maxSize) {
        this.eventManager.setMaxEventPoolSize(maxSize);
    }

    public void setMaxEventBufferTime(int maxBufferTimeInSec) {
        this.eventManager.setMaxEventBufferTime(maxBufferTimeInSec);
    }

    public int getMaxEventBufferTime() {
        return this.eventManager.getMaxEventBufferTime();
    }

    public EventManager getEventManager() {
        return this.eventManager;
    }

    public static AnalyticsControllerImpl getInstance() {
        return instance;
    }

    void loadPersistentAttributes() {
        log.verbose("AnalyticsControllerImpl.loadPersistentAttributes - loading userAttributes from the attribute store...");
        List<AnalyticAttribute> storedAttrs = this.attributeStore.fetchAll();
        log.debug("AnalyticsControllerImpl.loadPersistentAttributes - found " + storedAttrs.size() + " userAttributes in the attribute store...");
        int size = this.userAttributes.size();
        Iterator var3 = storedAttrs.iterator();

        while(var3.hasNext()) {
            AnalyticAttribute attr = (AnalyticAttribute)var3.next();
            if(!this.userAttributes.contains(attr) && size <= MAX_ATTRIBUTES) {
                this.userAttributes.add(attr);
                ++size;
            }
        }

    }

    private AnalyticAttribute getSystemAttribute(String name) {
        AnalyticAttribute attribute = null;
        Iterator var3 = this.systemAttributes.iterator();

        while(var3.hasNext()) {
            AnalyticAttribute nextAttribute = (AnalyticAttribute)var3.next();
            if(nextAttribute.getName().equals(name)) {
                attribute = nextAttribute;
                break;
            }
        }

        return attribute;
    }

    private AnalyticAttribute getUserAttribute(String name) {
        AnalyticAttribute attribute = null;
        Iterator var3 = this.userAttributes.iterator();

        while(var3.hasNext()) {
            AnalyticAttribute nextAttribute = (AnalyticAttribute)var3.next();
            if(nextAttribute.getName().equals(name)) {
                attribute = nextAttribute;
                break;
            }
        }

        return attribute;
    }

    private void clear() {
        log.verbose("AnalyticsControllerImpl.clear - clearing out attributes and events");
        this.systemAttributes.clear();
        this.userAttributes.clear();
        this.eventManager.empty();
    }

    private boolean isAttributeNameValid(String name) {
        boolean valid = this.isNameValid(name);
        if(valid) {
            valid = !this.isNameReserved(name);
            if(!valid) {
                log.error("Attribute name " + name + " is reserved for internal use and will be ignored.");
            }
        }

        return valid;
    }

    private boolean isNameValid(String name) {
        boolean valid = name != null && !name.equals("") && name.length() < 256;
        if(!valid) {
            log.error("Attribute name " + name + " is null, empty, or exceeds the maximum length of " + 256 + " characters.");
        }

        return valid;
    }

    private boolean isStringValueValid(String name, String value) {
        boolean valid = value != null && !value.equals("") && value.getBytes().length < 4096;
        if(!valid) {
            log.error("Attribute value for name " + name + " is null, empty, or exceeds the maximum length of " + 4096 + " bytes.");
        }

        return valid;
    }

    private boolean isNameReserved(String name) {
        if(reservedNames.contains(name)) {
            log.verbose("Name " + name + " is in the reserved names list.");
            return true;
        } else if(name.startsWith(NEW_RELIC_PREFIX)) {
            log.verbose("Name " + name + " starts with reserved prefix " + NEW_RELIC_PREFIX);
            return true;
        } else if(name.startsWith(NR_PREFIX)) {
            log.verbose("Name " + name + " starts with reserved prefix " + NR_PREFIX);
            return true;
        } else {
            return false;
        }
    }

    private AnalyticAttribute createAttribute(String key, Object value) {
        try {
            if(value instanceof String) {
                return new AnalyticAttribute(key, String.valueOf(value));
            } else if(value instanceof Float) {
                return new AnalyticAttribute(key, (Float) value);
            } else if(value instanceof Double) {
                return new AnalyticAttribute(key, ((Double) value).floatValue());
            } else if(value instanceof Integer) {
                return new AnalyticAttribute(key, (float) (Integer) value);
            } else if(value instanceof Short) {
                return new AnalyticAttribute(key, (float) (Short) value);
            } else if(value instanceof Long) {
                return new AnalyticAttribute(key, (float) (Long) value);
            } else if(value instanceof BigDecimal) {
                return new AnalyticAttribute(key, ((BigDecimal) value).floatValue());
            } else if(value instanceof BigInteger) {
                return new AnalyticAttribute(key, ((BigInteger) value).floatValue());
            } else if(value instanceof Boolean) {
                return new AnalyticAttribute(key, (Boolean) value);
            } else {
                log.error("Unsupported event attribute type for key [" + key + "]: " + value.getClass().getName());
                return null;
            }
        } catch (ClassCastException var4) {
            log.error(String.format("Error casting attribute [%s] to String or Float: ", key), var4);
            return null;
        }
    }

    public boolean recordCustomEvent(String eventType, Map<String, Object> eventAttributes) {
        log.verbose("AnalyticsControllerImpl.recordCustomEvent - " + eventType + ": " + eventAttributes.size() + " attributes");
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(!this.eventManager.isEventTypeReserved(eventType) && this.eventManager.isEventTypeValid(eventType)) {
            String eventName = eventType;
            HashSet attributes = new HashSet();

            AnalyticAttribute attr;
            try {
                for(Iterator var5 = eventAttributes.keySet().iterator(); var5.hasNext(); attributes.add(attr)) {
                    String key = (String)var5.next();
                    Object value = eventAttributes.get(key);
                    attr = this.createAttribute(key, value);
                    if(attr == null) {
                        return false;
                    }

                    if(attr.getName().equals("name")) {
                        String name = attr.getStringValue();
                        if(name != null && !name.isEmpty()) {
                            eventName = attr.getStringValue();
                        }
                    }
                }
            } catch (Exception var10) {
                log.error(String.format("Error occurred while recording event [%s]: ", eventType), var10);
            }

            return this.addEvent(eventName, AnalyticsEventCategory.Custom, eventType, attributes);
        } else {
            return false;
        }
    }

    public boolean recordBreadcrumb(String name, Map<String, Object> eventAttributes) {
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            HashSet attributes = new HashSet();

            try {
                Iterator var4 = eventAttributes.keySet().iterator();

                while(var4.hasNext()) {
                    String key = (String)var4.next();
                    Object value = eventAttributes.get(key);
                    AnalyticAttribute attr = this.createAttribute(key, value);
                    if(attr == null) {
                        return false;
                    }

                    attributes.add(attr);
                }
            } catch (Exception var8) {
                log.error(String.format("Error occurred while recording event [%s]: ", name), var8);
            }

            return this.addEvent(name, AnalyticsEventCategory.Breadcrumb, "MobileBreadcrumb", attributes);
        }
    }

    public boolean recordEvent(String name, AnalyticsEventCategory eventCategory, String eventType, Map<String, Object> eventAttributes) {
        log.verbose("AnalyticsControllerImpl.recordEvent - " + name + ": " + eventAttributes.size() + " attributes");
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else if(!this.eventManager.isEventTypeValid(eventType)) {
            return false;
        } else {
            HashSet attributes = new HashSet();

            try {
                Iterator var6 = eventAttributes.keySet().iterator();

                while(var6.hasNext()) {
                    String key = (String)var6.next();
                    Object value = eventAttributes.get(key);
                    AnalyticAttribute attr = this.createAttribute(key, value);
                    if(attr == null) {
                        return false;
                    }

                    attributes.add(attr);
                }
            } catch (Exception var10) {
                log.error(String.format("Error occurred while recording event [%s]: ", name), var10);
            }

            return this.addEvent(name, eventCategory, eventType, attributes);
        }
    }

    public boolean recordEvent(String name, Map<String, Object> eventAttributes) {
        log.verbose("AnalyticsControllerImpl.recordEvent - " + name + ": " + eventAttributes.size() + " attributes");
        if(!this.isInitializedAndEnabled()) {
            return false;
        } else {
            HashSet attributes = new HashSet();

            try {
                Iterator var4 = eventAttributes.keySet().iterator();

                while(var4.hasNext()) {
                    String key = (String)var4.next();
                    Object value = eventAttributes.get(key);
                    AnalyticAttribute attr = this.createAttribute(key, value);
                    if(attr == null) {
                        return false;
                    }

                    attributes.add(attr);
                }
            } catch (Exception var8) {
                log.error(String.format("Error occurred while recording event [%s]: ", name), var8);
            }

            return this.addEvent(name, AnalyticsEventCategory.Custom, "Mobile", attributes);
        }
    }

    void createHttpErrorEvent(HttpTransaction txn) {
        if(this.isInitializedAndEnabled()) {
            NetworkEventController.createHttpErrorEvent(txn);
        }

    }

    void createNetworkFailureEvent(HttpTransaction txn) {
        if(this.isInitializedAndEnabled()) {
            NetworkEventController.createNetworkFailureEvent(txn);
        }

    }

    void createNetworkRequestEvent(HttpTransaction txn) {
        if(this.isInitializedAndEnabled()) {
            NetworkEventController.createNetworkRequestEvent(txn);
        }

    }

    public void createNetworkRequestEvents(HttpTransaction txn) {
        if(this.isInitializedAndEnabled()) {
            if(this.isHttpError(txn)) {
                NetworkEventController.createHttpErrorEvent(txn);
            } else if(this.isNetworkFailure(txn)) {
                NetworkEventController.createNetworkFailureEvent(txn);
            } else if(this.isSuccessfulRequest(txn)) {
                NetworkEventController.createNetworkRequestEvent(txn);
            }
        }

    }

    private boolean isNetworkFailure(HttpTransaction txn) {
        return txn.getErrorCode() != 0;
    }

    private boolean isHttpError(HttpTransaction txn) {
        return (long)txn.getStatusCode() >= 400L;
    }

    private boolean isSuccessfulRequest(HttpTransaction txn) {
        return txn.getStatusCode() > 0 && txn.getStatusCode() < 400;
    }

    private boolean isInitializedAndEnabled() {
        if(!initialized.get()) {
            log.warning("Analytics controller is not initialized!");
            return false;
        } else if(!this.isEnabled.get()) {
            log.warning("Analytics controller is not enabled!");
            return false;
        } else {
            return true;
        }
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled.set(enabled);
    }

    private boolean addNewUserAttribute(AnalyticAttribute attribute) {
        if(this.userAttributes.size() < MAX_ATTRIBUTES) {
            this.userAttributes.add(attribute);
            if(attribute.isPersistent() && !this.attributeStore.store(attribute)) {
                log.error("Failed to store attribute " + attribute + " to attribute store.");
                return false;
            }
        } else {
            log.warning("Attribute limit exceeded: at most 64 are allowed.");
            log.debug("Currently defined attributes:");
            Iterator var2 = this.userAttributes.iterator();

            while(var2.hasNext()) {
                AnalyticAttribute attr = (AnalyticAttribute)var2.next();
                log.debug("\t" + attr.getName() + ": " + attr.valueAsString());
            }
        }

        return true;
    }

    class InteractionCompleteListener implements TraceLifecycleAware {
        InteractionCompleteListener() {
        }

        public void onEnterMethod() {
        }

        public void onExitMethod() {
        }

        public void onTraceStart(ActivityTrace activityTrace) {
            AnalyticAttribute lastInteraction = new AnalyticAttribute("lastInteraction", activityTrace.getActivityName());
            AnalyticsControllerImpl.this.addAttributeUnchecked(lastInteraction, true);
        }

        public void onTraceComplete(ActivityTrace activityTrace) {
            AnalyticsControllerImpl.log.verbose("AnalyticsControllerImpl.InteractionCompleteListener.onTraceComplete invoke.");
            AnalyticsEvent event = this.createTraceEvent(activityTrace);
            AnalyticsControllerImpl.instance.addEvent(event);
        }

        public void onTraceRename(ActivityTrace activityTrace) {
            AnalyticAttribute lastInteraction = new AnalyticAttribute("lastInteraction", activityTrace.getActivityName());
            AnalyticsControllerImpl.this.addAttributeUnchecked(lastInteraction, true);
        }

        private AnalyticsEvent createTraceEvent(ActivityTrace activityTrace) {
            float durationInSec = activityTrace.rootTrace.getDurationAsSeconds();
            Set<AnalyticAttribute> attrs = new HashSet<>();
            attrs.add(new AnalyticAttribute("interactionDuration", durationInSec));
            return AnalyticsEventFactory.createEvent(activityTrace.rootTrace.displayName, AnalyticsEventCategory.Interaction, "Mobile", attrs);
        }
    }
}
