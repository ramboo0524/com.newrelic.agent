//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.tracing;

public class SampleValue {
    private Double value = 0.0d;
    private boolean isDouble;

    public SampleValue(double value) {
        this.setValue(value);
    }

    public SampleValue(long value) {
        this.setValue(value);
    }

    public Number getValue() {
        return (this.isDouble?this.asDouble():this.asLong());
    }

    public Double asDouble() {
        return this.value;
    }

    public Long asLong() {
        return this.value.longValue() ;
    }

    public void setValue(double value) {
        this.value = value;
        this.isDouble = true;
    }

    public void setValue(long value) {
        this.value = (double)value;
        this.isDouble = false;
    }

    public boolean isDouble() {
        return this.isDouble;
    }

    public void setDouble(boolean aDouble) {
        this.isDouble = aDouble;
    }
}
