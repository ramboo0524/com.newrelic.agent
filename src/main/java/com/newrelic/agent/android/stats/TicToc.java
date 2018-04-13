//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stats;

public class TicToc {
    private long startTime;
    private long endTime;
    private TicToc.State state;

    public TicToc() {
    }

    public void tic() {
        this.state = TicToc.State.STARTED;
        this.startTime = System.currentTimeMillis();
    }

    public long toc() {
        this.endTime = System.currentTimeMillis();
        if(this.state == TicToc.State.STARTED) {
            this.state = TicToc.State.STOPPED;
            return this.endTime - this.startTime;
        } else {
            return -1L;
        }
    }

    public long peek() {
        return this.state == TicToc.State.STARTED?System.currentTimeMillis() - this.startTime:0L;
    }

    private enum State {
        STOPPED,
        STARTED

    }
}
