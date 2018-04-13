//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.measurement;

public class ThreadInfo {
    private long id;
    private String name;

    public ThreadInfo() {
        this(Thread.currentThread());
    }

    public ThreadInfo(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public ThreadInfo(Thread thread) {
        this(thread.getId(), thread.getName());
    }

    public static ThreadInfo fromThread(Thread thread) {
        return new ThreadInfo(thread);
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "ThreadInfo{id=" + this.id + ", name='" + this.name + '\'' + '}';
    }
}
