//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
public @interface Trace {
    String NULL = "";

    String metricName() default "";

    boolean skipTransactionTrace() default false;

    MetricCategory category() default MetricCategory.NONE;
}
