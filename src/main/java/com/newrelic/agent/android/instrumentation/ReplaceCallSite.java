//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

public @interface ReplaceCallSite {
    boolean isStatic() default false;

    String scope() default "";
}
