//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.harvest.crash;

import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.newrelic.agent.android.util.SafeJsonPrimitive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class ThreadInfo extends HarvestableObject {
    private boolean crashed;
    private long threadId;
    private String threadName;
    private int threadPriority;
    private StackTraceElement[] stackTrace;
    private String state;

    private ThreadInfo() {
    }

    public ThreadInfo(Throwable throwable) {
        this.crashed = true;
        this.threadId = Thread.currentThread().getId();
        this.threadName = Thread.currentThread().getName();
        this.threadPriority = Thread.currentThread().getPriority();
        this.stackTrace = throwable.getStackTrace();
        this.state = Thread.currentThread().getState().toString();
    }

    public ThreadInfo(Thread thread, StackTraceElement[] stackTrace) {
        this.crashed = false;
        this.threadId = thread.getId();
        this.threadName = thread.getName();
        this.threadPriority = thread.getPriority();
        this.stackTrace = stackTrace;
        this.state = thread.getState().toString();
    }

    public long getThreadId() {
        return this.threadId;
    }

    public static List<ThreadInfo> extractThreads(Throwable throwable) {
        List<ThreadInfo> threads = new ArrayList<>();
        ThreadInfo crashedThread = new ThreadInfo(throwable);
        long crashedThreadId = crashedThread.getThreadId();
        threads.add(crashedThread);
        Iterator var5 = Thread.getAllStackTraces().entrySet().iterator();

        while(var5.hasNext()) {
            Entry<Thread, StackTraceElement[]> threadEntry = (Entry)var5.next();
            Thread thread = threadEntry.getKey();
            StackTraceElement[] threadStackTrace = threadEntry.getValue();
            if(thread.getId() != crashedThreadId) {
                threads.add(new ThreadInfo(thread, threadStackTrace));
            }
        }

        return threads;
    }

    public JsonObject asJsonObject() {
        JsonObject data = new JsonObject();
        data.add("crashed", SafeJsonPrimitive.factory(this.crashed));
        data.add("state", SafeJsonPrimitive.factory(this.state));
        data.add("threadNumber", SafeJsonPrimitive.factory(this.threadId));
        data.add("threadId", SafeJsonPrimitive.factory(this.threadName));
        data.add("priority", SafeJsonPrimitive.factory(this.threadPriority));
        data.add("stack", this.getStackAsJson());
        return data;
    }

    public static ThreadInfo newFromJson(JsonObject jsonObject) {
        ThreadInfo info = new ThreadInfo();
        info.crashed = jsonObject.get("crashed").getAsBoolean();
        info.state = jsonObject.get("state").getAsString();
        info.threadId = jsonObject.get("threadNumber").getAsLong();
        info.threadName = jsonObject.get("threadId").getAsString();
        info.threadPriority = jsonObject.get("priority").getAsInt();
        info.stackTrace = stackTraceFromJson(jsonObject.get("stack").getAsJsonArray());
        return info;
    }

    public static StackTraceElement[] stackTraceFromJson(JsonArray jsonArray) {
        StackTraceElement[] stack = new StackTraceElement[jsonArray.size()];
        int i = 0;

        StackTraceElement stackTraceElement;
        for(Iterator var3 = jsonArray.iterator(); var3.hasNext(); stack[i++] = stackTraceElement) {
            JsonElement jsonElement = (JsonElement)var3.next();
            String fileName = "unknown";
            if(jsonElement.getAsJsonObject().get("fileName") != null) {
                fileName = jsonElement.getAsJsonObject().get("fileName").getAsString();
            }

            String className = jsonElement.getAsJsonObject().get("className").getAsString();
            String methodName = jsonElement.getAsJsonObject().get("methodName").getAsString();
            int lineNumber = jsonElement.getAsJsonObject().get("lineNumber").getAsInt();
            stackTraceElement = new StackTraceElement(className, methodName, fileName, lineNumber);
        }

        return stack;
    }

    public static List<ThreadInfo> newListFromJson(JsonArray jsonArray) {
        List<ThreadInfo> list = new ArrayList<>();
        Iterator var2 = jsonArray.iterator();

        while(var2.hasNext()) {
            JsonElement jsonElement = (JsonElement)var2.next();
            list.add(newFromJson(jsonElement.getAsJsonObject()));
        }

        return list;
    }

    private JsonArray getStackAsJson() {
        JsonArray data = new JsonArray();
        StackTraceElement[] var2 = this.stackTrace;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement element = var2[var4];
            JsonObject elementJson = new JsonObject();
            if(element.getFileName() != null) {
                elementJson.add("fileName", SafeJsonPrimitive.factory(element.getFileName()));
            }

            elementJson.add("className", SafeJsonPrimitive.factory(element.getClassName()));
            elementJson.add("methodName", SafeJsonPrimitive.factory(element.getMethodName()));
            elementJson.add("lineNumber", SafeJsonPrimitive.factory(element.getLineNumber()));
            data.add(elementJson);
        }

        return data;
    }
}
