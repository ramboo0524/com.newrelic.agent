//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.agentdata.builder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newrelic.mobile.fbs.AgentData;
import com.newrelic.mobile.fbs.AgentDataBundle;
import com.newrelic.mobile.fbs.ApplicationInfo;
import com.newrelic.mobile.fbs.BoolSessionAttribute;
import com.newrelic.mobile.fbs.DoubleSessionAttribute;
import com.newrelic.mobile.fbs.LongSessionAttribute;
import com.newrelic.mobile.fbs.StringSessionAttribute;
import com.newrelic.mobile.fbs.hex.Frame;
import com.newrelic.mobile.fbs.hex.HandledException;
import com.newrelic.mobile.fbs.hex.Thread;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import static java.lang.Long.valueOf;
import static java.lang.System.*;

public class AgentDataBuilder {
    public AgentDataBuilder() {
    }

    protected static void computeIfAbsent(String s, Map<String, Integer> map, FlatBufferBuilder flat) {
        if(null != s && !map.containsValue(s)) {
            int offset = flat.createString(s);
            map.put(s, offset);
        }

    }

    public static FlatBufferBuilder startAndFinishAgentData(Map<String, Object> attributesMap, Set<Map<String, Object>> agentData) {
        Map<String, Integer> stringIndexMap = new HashMap<>();
        FlatBufferBuilder flat = new FlatBufferBuilder();
        Iterator var4 = attributesMap.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<String, Object> attribute = (Entry<String, Object>) var4.next();
            String key = attribute.getKey();
            Object val = attribute.getValue();
            computeIfAbsent(key, stringIndexMap, flat);
            if(val instanceof String) {
                String s = (String)val;
                computeIfAbsent(s, stringIndexMap, flat);
            }
        }

        List<Map<String, Object>> thread = null;

        Map hex;
        for(Iterator var34 = agentData.iterator(); var34.hasNext(); thread = (List)hex.get("thread")) {
            hex = (Map)var34.next();
            computeIfAbsent((String)hex.get("name"), stringIndexMap, flat);
            computeIfAbsent((String)hex.get("message"), stringIndexMap, flat);
            computeIfAbsent((String)hex.get("cause"), stringIndexMap, flat);
        }

        List<Integer> framesOffsets = new ArrayList<>();
        List<Integer> threadsOffsets = new ArrayList<>();
        int stringSessionAttributesVector;
        if(thread != null) {
            Iterator var38 = thread.iterator();

            while(var38.hasNext()) {
                Map<String, Object> frame = (Map<String, Object>)var38.next();
                Map<String, Integer> frameValStringIndexMap = new HashMap<>();
                frameValStringIndexMap.put("fileName", flat.createString(""));
                Iterator var10 = frame.entrySet().iterator();

                while(var10.hasNext()) {
                    Entry<String, Object> frameElement = (Entry)var10.next();
                    String key = (String)frameElement.getKey();
                    Object val = frameElement.getValue();
                    if(val instanceof String) {
                        stringSessionAttributesVector = flat.createString((String)val);
                        frameValStringIndexMap.put(key, stringSessionAttributesVector);
                    }
                }

                Frame.startFrame(flat);
                if(frameValStringIndexMap.get("className") != null) {
                    Frame.addClassName(flat, frameValStringIndexMap.get("className"));
                }

                if(frameValStringIndexMap.get("methodName") != null) {
                    Frame.addMethodName(flat, frameValStringIndexMap.get("methodName"));
                }

                if(frameValStringIndexMap.get("fileName") != null) {
                    Frame.addFileName(flat, frameValStringIndexMap.get("fileName"));
                }

                if(frame.get("lineNumber") != null) {
                    Frame.addLineNumber(flat, (long) frame.get("lineNumber"));
                }

                int frameOffset = Frame.endFrame(flat);
                framesOffsets.add(frameOffset);
            }
        }

        int framesOffset = Thread.createFramesVector(flat, toArray(framesOffsets));
        int threadOffset = Thread.createThread(flat, framesOffset);
        threadsOffsets.add(threadOffset);
        int threadVectorOffset = HandledException.createThreadsVector(flat, toArray(threadsOffsets));
        Set<Integer> stringSessionAttributes = new HashSet<>();
        Set<Integer> doubleSessionAttributes = new HashSet<>();
        Set<Integer> longSessionAttributes = new HashSet<>();
        Set<Integer> boolSessionAttributes = new HashSet<>();
        Iterator var48 = attributesMap.entrySet().iterator();

        while(true) {
            int booleanSessionAttributesVector;
            while(var48.hasNext()) {
                Entry<String, Object> attribute = (Entry)var48.next();
                String key = attribute.getKey();
                booleanSessionAttributesVector = stringIndexMap.get(key);
                Object val = attribute.getValue();
                if(val instanceof String) {
                    stringSessionAttributes.add(StringSessionAttribute.createStringSessionAttribute(flat, booleanSessionAttributesVector, stringIndexMap.get(val)));
                } else {
                    Number n;
                    if(!(val instanceof Double) && !(val instanceof Float)) {
                        if(val instanceof Number) {
                            n = (Number)val;
                            longSessionAttributes.add(LongSessionAttribute.createLongSessionAttribute(flat, booleanSessionAttributesVector, n.longValue()));
                        } else if(val instanceof Boolean) {
                            boolSessionAttributes.add(BoolSessionAttribute.createBoolSessionAttribute(flat, booleanSessionAttributesVector, (Boolean) val));
                        }
                    } else {
                        n = (Number)val;
                        doubleSessionAttributes.add(DoubleSessionAttribute.createDoubleSessionAttribute(flat, booleanSessionAttributesVector, n.doubleValue()));
                    }
                }
            }

            stringSessionAttributesVector = -1;
            if(!stringSessionAttributes.isEmpty()) {
                stringSessionAttributesVector = AgentData.createStringAttributesVector(flat, toArray(stringSessionAttributes));
            }

            int doubleSessionAttributesVector = -1;
            if(!doubleSessionAttributes.isEmpty()) {
                doubleSessionAttributesVector = AgentData.createDoubleAttributesVector(flat, toArray(doubleSessionAttributes));
            }

            int longSessionAttributesVector = -1;
            if(!longSessionAttributes.isEmpty()) {
                longSessionAttributesVector = AgentData.createLongAttributesVector(flat, toArray(longSessionAttributes));
            }

            booleanSessionAttributesVector = -1;
            if(!boolSessionAttributes.isEmpty()) {
                booleanSessionAttributesVector = AgentData.createBoolAttributesVector(flat, toArray(boolSessionAttributes));
            }

            Set<Integer> handledExceptionOffsets = new HashSet<>();
            int agentDataOffset;
            int causeOffset;
            if(!agentData.isEmpty()) {
                Iterator var52 = agentData.iterator();

                while(var52.hasNext()) {
                    Map<String, Object> map = (Map)var52.next();
                    int nameOffset = stringIndexMapOffset(stringIndexMap, map.get("name"));
                    agentDataOffset = stringIndexMapOffset(stringIndexMap, map.get("message"));
                    causeOffset = stringIndexMapOffset(stringIndexMap, map.get("cause"));
                    long timeStampMs =  map.containsKey("timestampMs") ? (Long)map.get("timestampMs") : currentTimeMillis();
                    long appUuidHigh = 0L;
                    long appUuidLow = 0L;

                    try {
                        appUuidHigh = (Long) map.get("appUuidHigh");
                        appUuidLow = (Long) map.get("appUuidLow");
                    } catch (ClassCastException var32) {
                        appUuidHigh = 0L;
                        appUuidLow = 0L;
                    }

                    HandledException.startHandledException(flat);
                    HandledException.addAppUuidHigh(flat, appUuidHigh);
                    HandledException.addAppUuidLow(flat, appUuidLow);
                    if(-1L != timeStampMs) {
                        HandledException.addTimestampMs(flat, timeStampMs);
                    }

                    if(-1 != nameOffset) {
                        HandledException.addName(flat, nameOffset);
                    }

                    if(-1 != agentDataOffset) {
                        HandledException.addMessage(flat, agentDataOffset);
                    }

                    if(-1 != causeOffset) {
                        HandledException.addCause(flat, causeOffset);
                    }

                    HandledException.addThreads(flat, threadVectorOffset);
                    int handledExceptionOffset = HandledException.endHandledException(flat);
                    handledExceptionOffsets.add(handledExceptionOffset);
                }
            }

            int handledExceptionVector = -1;
            if(!handledExceptionOffsets.isEmpty()) {
                handledExceptionVector = AgentData.createHandledExceptionsVector(flat, toArray(handledExceptionOffsets));
            }

            ApplicationInfo.startApplicationInfo(flat);
            ApplicationInfo.addPlatform(flat, 0);
            int applicationInfoOffset = ApplicationInfo.endApplicationInfo(flat);
            AgentData.startAgentData(flat);
            if(stringSessionAttributesVector != -1) {
                AgentData.addStringAttributes(flat, stringSessionAttributesVector);
            }

            if(doubleSessionAttributesVector != -1) {
                AgentData.addDoubleAttributes(flat, doubleSessionAttributesVector);
            }

            if(longSessionAttributesVector != -1) {
                AgentData.addLongAttributes(flat, longSessionAttributesVector);
            }

            if(booleanSessionAttributesVector != -1) {
                AgentData.addBoolAttributes(flat, booleanSessionAttributesVector);
            }

            if(handledExceptionVector != -1) {
                AgentData.addHandledExceptions(flat, handledExceptionVector);
            }

            AgentData.addApplicationInfo(flat, applicationInfoOffset);
            Set<Integer> agentDataOffsets = new HashSet<>();
            agentDataOffset = AgentData.endAgentData(flat);
            agentDataOffsets.add(agentDataOffset);
            causeOffset = AgentDataBundle.createAgentDataVector(flat, toArray(agentDataOffsets));
            AgentDataBundle.startAgentDataBundle(flat);
            AgentDataBundle.addAgentData(flat, causeOffset);
            int agentDataBundleOffset = AgentDataBundle.endAgentDataBundle(flat);
            flat.finish(agentDataBundleOffset);
            return flat;
        }
    }

    private static int stringIndexMapOffset(Map<String, Integer> stringIndexMap, Object hexKey) {
        Integer offset = -1;
        if(hexKey != null) {
            Integer index = (Integer)stringIndexMap.get(hexKey);
            if(index != null) {
                offset = index;
            }
        }

        return offset;
    }

    private static int[] toArray(Collection<Integer> c) {
        int[] a = new int[c.size()];
        Iterator<Integer> i = c.iterator();

        for(int var3 = 0; i.hasNext(); a[var3++] = i.next()) {
            ;
        }

        return a;
    }

    public static String toJsonString(AgentDataBundle agentDataBundle, int index) {
        Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().serializeNulls().setPrettyPrinting().create();
        AgentData agentData = agentDataBundle.agentData(index);
        return gson.toJson(attributesMapFromAgentData(agentData));
    }

    public static Map<String, Object> attributesMapFromAgentData(AgentData agentData) {
        Map<String, Object> map = new HashMap<>();

        int si;
        for(si = 0; si < agentData.stringAttributesLength(); ++si) {
            StringSessionAttribute a = agentData.stringAttributes(si);
            map.put(a.name(), a.value());
        }

        for(si = 0; si < agentData.longAttributesLength(); ++si) {
            LongSessionAttribute a = agentData.longAttributes(si);
            map.put(a.name(), valueOf(a.value()));
        }

        for(si = 0; si < agentData.doubleAttributesLength(); ++si) {
            DoubleSessionAttribute a = agentData.doubleAttributes(si);
            map.put(a.name(), a);
        }

        for(si = 0; si < agentData.boolAttributesLength(); ++si) {
            BoolSessionAttribute a = agentData.boolAttributes(si);
            map.put(a.name(), a);
        }

        for(si = 0; si < agentData.handledExceptionsLength(); ++si) {
            HandledException hex = agentData.handledExceptions(si);
            map.put("timestampMs", valueOf(hex.timestampMs()));
            map.put("appUuidHigh", valueOf(hex.appUuidHigh()));
            map.put("appUuidLow", valueOf(hex.appUuidLow()));
            map.put("name", hex.name());
            map.put("cause", hex.cause());
            map.put("message", hex.message());

            for(int t = 0; t < hex.threadsLength(); ++t) {
                java.lang.Thread currentThread = java.lang.Thread.currentThread();
                Map<String, Object> threadMap = new LinkedHashMap<>();

                for(int f = 0; f < hex.threads(t).framesLength(); ++f) {
                    Map<String, Object> frameMap = new LinkedHashMap<>();
                    frameMap.put("fileName", hex.threads(t).frames(f).fileName());
                    frameMap.put("lineNumber", valueOf(hex.threads(t).frames(f).lineNumber()));
                    frameMap.put("className", hex.threads(t).frames(f).className());
                    frameMap.put("methodName", hex.threads(t).frames(f).methodName());
                    threadMap.put("frame " + f, frameMap);
                }

                threadMap.put("crashed", Boolean.FALSE);
                threadMap.put("state", currentThread.getState().toString());
                threadMap.put("threadNumber", valueOf(currentThread.getId()));
                threadMap.put("threadId", currentThread.getName());
                threadMap.put("priority", currentThread.getPriority());
                map.put("thread " + t, threadMap);
            }
        }

        return map;
    }
}
