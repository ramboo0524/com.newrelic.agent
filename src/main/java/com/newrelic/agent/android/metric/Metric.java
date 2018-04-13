//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.metric;

import com.newrelic.agent.android.harvest.type.HarvestableObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Metric extends HarvestableObject {
    private String name;
    private String scope;
    private Double min;
    private Double max;
    private Double total;
    private Double sumOfSquares;
    private Double exclusive;
    private long count;

    public Metric(String name) {
        this(name, (String)null);
    }

    public Metric(String name, String scope) {
        this.name = name;
        this.scope = scope;
        this.count = 0L;
    }

    public Metric(Metric metric) {
        this.name = metric.getName();
        this.scope = metric.getScope();
        this.min = metric.getMin();
        this.max = metric.getMax();
        this.total = metric.getTotal();
        this.sumOfSquares = metric.getSumOfSquares();
        this.exclusive = metric.getExclusive();
        this.count = metric.getCount();
    }

    public void sample(double value) {
        ++this.count;
        if(this.total == null) {
            this.total = value;
            this.sumOfSquares = value * value;
        } else {
            this.total = this.total.doubleValue() + value;
            this.sumOfSquares = this.sumOfSquares.doubleValue() + value * value;
        }

        this.setMin(value);
        this.setMax(value);
    }

    public void setMin(Double value) {
        if(value != null) {
            if(this.min == null) {
                this.min = value;
            } else if(value < this.min) {
                this.min = value;
            }

        }
    }

    public void setMinFieldValue(Double value) {
        this.min = value;
    }

    public void setMax(Double value) {
        if(value != null) {
            if(this.max == null) {
                this.max = value;
            } else if(value > this.max) {
                this.max = value;
            }

        }
    }

    public void setMaxFieldValue(Double value) {
        this.max = value;
    }

    public void aggregate(Metric metric) {
        if(metric != null) {
            this.increment(metric.getCount());
            if(!metric.isCountOnly()) {
                this.total = this.total == null?metric.getTotal():this.total + metric.getTotal();
                this.sumOfSquares = this.sumOfSquares == null?metric.getSumOfSquares():this.sumOfSquares + metric.getSumOfSquares();
                this.exclusive = this.exclusive == null?metric.getExclusive():this.exclusive + metric.getExclusive();
                this.setMin(metric.getMin());
                this.setMax(metric.getMax());
            }
        }
    }

    public void increment(long value) {
        this.count += value;
    }

    public void increment() {
        this.increment(1L);
    }

    public double getSumOfSquares() {
        return this.sumOfSquares == null?0.0D:this.sumOfSquares;
    }

    public long getCount() {
        return this.count;
    }

    public double getExclusive() {
        return this.exclusive == null?0.0D:this.exclusive;
    }

    public void addExclusive(double value) {
        if(this.exclusive == null) {
            this.exclusive = value;
        } else {
            this.exclusive = this.exclusive + value;
        }

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScope() {
        return this.scope;
    }

    public String getStringScope() {
        return this.scope == null?"":this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public double getMin() {
        return this.min == null?0.0D:this.min.doubleValue();
    }

    public double getMax() {
        return this.max == null?0.0D:this.max.doubleValue();
    }

    public double getTotal() {
        return this.total == null?0.0D:this.total.doubleValue();
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void setSumOfSquares(Double sumOfSquares) {
        this.sumOfSquares = sumOfSquares;
    }

    public void setExclusive(Double exclusive) {
        this.exclusive = exclusive;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void clear() {
        this.min = null;
        this.max = null;
        this.total = null;
        this.sumOfSquares = null;
        this.exclusive = null;
        this.count = 0L;
    }

    public boolean isCountOnly() {
        return this.total == null;
    }

    public boolean isScoped() {
        return this.scope != null;
    }

    public boolean isUnscoped() {
        return this.scope == null;
    }

    public JsonElement asJson() {
        return (JsonElement)(this.isCountOnly()?new JsonPrimitive(Long.valueOf(this.count)):this.asJsonObject());
    }

    public JsonObject asJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("count", new JsonPrimitive(Long.valueOf(this.count)));
        if(this.total != null) {
            jsonObject.add("total", new JsonPrimitive(this.total));
        }

        if(this.min != null) {
            jsonObject.add("min", new JsonPrimitive(this.min));
        }

        if(this.max != null) {
            jsonObject.add("max", new JsonPrimitive(this.max));
        }

        if(this.sumOfSquares != null) {
            jsonObject.add("sum_of_squares", new JsonPrimitive(this.sumOfSquares));
        }

        if(this.exclusive != null) {
            jsonObject.add("exclusive", new JsonPrimitive(this.exclusive));
        }

        return jsonObject;
    }

    public String toString() {
        return "Metric{count=" + this.count + ", total=" + this.total + ", max=" + this.max + ", min=" + this.min + ", scope='" + this.scope + '\'' + ", name='" + this.name + '\'' + ", exclusive='" + this.exclusive + '\'' + ", sumofsquares='" + this.sumOfSquares + '\'' + '}';
    }
}
