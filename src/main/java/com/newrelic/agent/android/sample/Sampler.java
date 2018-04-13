//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.sample;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.os.Debug.MemoryInfo;
import com.newrelic.agent.android.harvest.AgentHealth;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.metric.Metric;
import com.newrelic.agent.android.stats.TicToc;
import com.newrelic.agent.android.tracing.ActivityTrace;
import com.newrelic.agent.android.tracing.Sample;
import com.newrelic.agent.android.tracing.TraceLifecycleAware;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.tracing.Sample.SampleType;
import com.newrelic.agent.android.util.NamedThreadFactory;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Sampler implements TraceLifecycleAware, Runnable {
    protected static final long SAMPLE_FREQ_MS = 100L;
    protected static final long SAMPLE_FREQ_MS_MAX = 250L;
    private static final int[] PID = new int[]{Process.myPid()};
    private static final int KB_IN_MB = 1024;
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final ReentrantLock samplerLock = new ReentrantLock();
    protected static Sampler sampler;
    protected static boolean cpuSamplingDisabled = false;
    private final ActivityManager activityManager;
    private final EnumMap<SampleType, Collection<Sample>> samples = new EnumMap<>(SampleType.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Sampler"));
    protected final AtomicBoolean isRunning = new AtomicBoolean(false);
    protected long sampleFreqMs = SAMPLE_FREQ_MS;
    protected ScheduledFuture sampleFuture;
    private Long lastCpuTime;
    private Long lastAppCpuTime;
    private RandomAccessFile procStatFile;
    private RandomAccessFile appStatFile;
    private Metric samplerServiceMetric;

    protected Sampler(Context context) {
        this.activityManager = (ActivityManager)context.getSystemService("activity");
        this.samples.put(SampleType.MEMORY, new ArrayList());
        this.samples.put(SampleType.CPU, new ArrayList());
    }

    public static void init(Context context) {
        samplerLock.lock();

        try {
            if(sampler == null) {
                sampler = provideSampler(context);
                sampler.sampleFreqMs = SAMPLE_FREQ_MS;
                sampler.samplerServiceMetric = new Metric("samplerServiceTime");
                TraceMachine.addTraceListener(sampler);
                log.debug("Sampler initialized");
            }
        } catch (Exception var5) {
            log.error("Sampler init failed: " + var5.getMessage());
            shutdown();
        } finally {
            samplerLock.unlock();
        }

    }

    protected static Sampler provideSampler(Context context) {
        return new Sampler(context);
    }

    public static void start() {
        samplerLock.lock();

        try {
            if(sampler != null) {
                sampler.schedule();
                log.debug("Sampler started");
            }
        } finally {
            samplerLock.unlock();
        }

    }

    public static void stop() {
        samplerLock.lock();

        try {
            if(sampler != null) {
                sampler.stop(false);
                log.debug("Sampler stopped");
            }
        } finally {
            samplerLock.unlock();
        }

    }

    public static void stopNow() {
        samplerLock.lock();

        try {
            if(sampler != null) {
                sampler.stop(true);
                log.debug("Sampler hard stopped");
            }
        } finally {
            samplerLock.unlock();
        }

    }

    public static void shutdown() {
        samplerLock.lock();

        try {
            if(sampler != null) {
                TraceMachine.removeTraceListener(sampler);
                stopNow();
                sampler = null;
                log.debug("Sampler shutdown");
            }
        } finally {
            samplerLock.unlock();
        }

    }

    public void run() {
        try {
            if(this.isRunning.get()) {
                this.sample();
            }
        } catch (Exception var2) {
            log.error("Caught exception while running the sampler", var2);
            AgentHealth.noticeException(var2);
        }

    }

    protected void schedule() {
        samplerLock.lock();

        try {
            if(!this.isRunning.get()) {
                this.clear();
                this.sampleFuture = this.scheduler.scheduleWithFixedDelay(this, 0L, this.sampleFreqMs, TimeUnit.MILLISECONDS);
                this.isRunning.set(true);
                log.debug(String.format("Sampler scheduler started; sampling will occur every %d ms.", this.sampleFreqMs));
            }
        } catch (Exception var5) {
            log.error("Sampler scheduling failed: " + var5.getMessage());
            AgentHealth.noticeException(var5);
        } finally {
            samplerLock.unlock();
        }

    }

    protected void stop(boolean immediate) {
        samplerLock.lock();

        try {
            if(this.isRunning.get()) {
                this.isRunning.set(false);
                if(this.sampleFuture != null) {
                    this.sampleFuture.cancel(immediate);
                }

                this.resetCpuSampler();
                log.debug("Sampler canceled");
            }
        } catch (Exception var6) {
            log.error("Sampler stop failed: " + var6.getMessage());
            AgentHealth.noticeException(var6);
        } finally {
            samplerLock.unlock();
        }

    }

    protected static boolean isRunning() {
        return (sampler != null && sampler.sampleFuture != null) && !sampler.sampleFuture.isDone();
    }

    protected void monitorSamplerServiceTime(double serviceTime) {
        this.samplerServiceMetric.sample(serviceTime);
        Double serviceTimeAvg = this.samplerServiceMetric.getTotal() / (double)this.samplerServiceMetric.getCount();
        if(serviceTimeAvg > (double)this.sampleFreqMs) {
            log.debug("Sampler: sample service time has been exceeded. Increase by 10%");
            this.sampleFreqMs = Math.min((long)((float)this.sampleFreqMs * 1.1F), SAMPLE_FREQ_MS_MAX);
            if(this.sampleFuture != null) {
                this.sampleFuture.cancel(true);
            }

            this.sampleFuture = this.scheduler.scheduleWithFixedDelay(this, 0L, this.sampleFreqMs, TimeUnit.MILLISECONDS);
            log.debug(String.format("Sampler scheduler restarted; sampling will now occur every %d ms.", this.sampleFreqMs));
            this.samplerServiceMetric.clear();
        }

    }

    protected void sample() {
        TicToc timer = new TicToc();
        samplerLock.lock();

        try {
            timer.tic();
            Sample memorySample = sampleMemory();
            if(memorySample != null) {
                this.getSampleCollection(SampleType.MEMORY).add(memorySample);
            }

            Sample cpuSample = this.sampleCpu();
            if(cpuSample != null) {
                this.getSampleCollection(SampleType.CPU).add(cpuSample);
            }
        } catch (Exception var7) {
            log.error("Sampling failed: " + var7.getMessage());
            AgentHealth.noticeException(var7);
        } finally {
            samplerLock.unlock();
        }

        this.monitorSamplerServiceTime((double)timer.toc());
    }

    protected void clear() {
        Iterator var1 = this.samples.values().iterator();

        while(var1.hasNext()) {
            Collection sampleCollection = (Collection)var1.next();
            sampleCollection.clear();
        }

    }

    public static Sample sampleMemory() {
        return sampler == null?null:sampleMemory(sampler.activityManager);
    }

    public static Sample sampleMemory(ActivityManager activityManager) {
        try {
            MemoryInfo[] memInfo = activityManager.getProcessMemoryInfo(PID);
            if(memInfo.length > 0) {
                int totalPss = memInfo[0].getTotalPss();
                if(totalPss >= 0) {
                    Sample sample = new Sample(SampleType.MEMORY);
                    sample.setSampleValue((double)totalPss / KB_IN_MB);
                    return sample;
                }
            }
        } catch (Exception var4) {
            log.error("Sample memory failed: " + var4.getMessage());
            AgentHealth.noticeException(var4);
        }

        return null;
    }

    protected static Sample sampleCpuInstance() {
        return sampler == null?null:sampler.sampleCpu();
    }

    public Sample sampleCpu() {
        if(cpuSamplingDisabled) {
            return null;
        } else {
            try {
                if(this.procStatFile != null && this.appStatFile != null) {
                    this.procStatFile.seek(0L);
                    this.appStatFile.seek(0L);
                } else {
                    this.procStatFile = new RandomAccessFile("/proc/stat", "r");
                    this.appStatFile = new RandomAccessFile("/proc/" + PID[0] + "/stat", "r");
                }

                String procStatString = this.procStatFile.readLine();
                String appStatString = this.appStatFile.readLine();
                String[] procStats = procStatString.split(" ");
                String[] appStats = appStatString.split(" ");
                long cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3]) + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5]) + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7]) + Long.parseLong(procStats[8]);
                long appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
                if(this.lastCpuTime == null && this.lastAppCpuTime == null) {
                    this.lastCpuTime = cpuTime;
                    this.lastAppCpuTime = appTime;
                    return null;
                } else {
                    Sample sample = new Sample(SampleType.CPU);
                    sample.setSampleValue((double)(appTime - this.lastAppCpuTime) / (double)(cpuTime - this.lastCpuTime) * 100.0D);
                    this.lastCpuTime = cpuTime;
                    this.lastAppCpuTime = appTime;
                    return sample;
                }
            } catch (Exception var10) {
                cpuSamplingDisabled = true;
                log.debug("Exception hit while CPU sampling: " + var10.getMessage());
                AgentHealth.noticeException(var10);
                return null;
            }
        }
    }

    private void resetCpuSampler() {
        this.lastCpuTime = null;
        this.lastAppCpuTime = null;
        if(this.appStatFile != null && this.procStatFile != null) {
            try {
                this.appStatFile.close();
                this.procStatFile.close();
                this.appStatFile = null;
                this.procStatFile = null;
            } catch (IOException var2) {
                log.debug("Exception hit while resetting CPU sampler: " + var2.getMessage());
                AgentHealth.noticeException(var2);
            }
        }

    }

    public static Map<SampleType, Collection<Sample>> copySamples() {
        samplerLock.lock();

        HashMap<SampleType, Collection<Sample>> var0;
        try {
            if(sampler != null) {
                EnumMap<SampleType, Collection<Sample>> copy = new EnumMap<>(sampler.samples);
                Iterator var6 = sampler.samples.keySet().iterator();

                while(var6.hasNext()) {
                    SampleType key = (SampleType)var6.next();
                    copy.put(key, new ArrayList<>(sampler.samples.get(key)));
                }

                return Collections.unmodifiableMap(copy);
            }

            samplerLock.unlock();
            var0 = new HashMap<>();
        } finally {
            samplerLock.unlock();
        }

        return var0;
    }

    private Collection<Sample> getSampleCollection(SampleType type) {
        return this.samples.get(type);
    }

    public void onEnterMethod() {
        if(!this.isRunning.get()) {
            start();
        }
    }

    public void onExitMethod() {
    }

    public void onTraceStart(ActivityTrace activityTrace) {
        start();
    }

    public void onTraceComplete(final ActivityTrace activityTrace) {
        this.scheduler.execute(new Runnable() {
            public void run() {
                try {
                    Sampler.this.stop(true);
                    activityTrace.setVitals(Sampler.copySamples());
                    Sampler.this.clear();
                } catch (RuntimeException var2) {
                    Sampler.log.error(var2.toString());
                }

            }
        });
    }

    public void onTraceRename(ActivityTrace activityTrace) {
    }
}
