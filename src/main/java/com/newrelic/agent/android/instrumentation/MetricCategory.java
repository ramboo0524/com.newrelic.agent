//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import java.util.HashMap;
import java.util.Map;

public enum MetricCategory {
    NONE("None"),
    VIEW_LOADING("View Loading"),
    VIEW_LAYOUT("Layout"),
    DATABASE("Database"),
    IMAGE("Images"),
    JSON("JSON"),
    NETWORK("Network");

    private String categoryName;
    private static final Map<String, MetricCategory> methodMap = new HashMap<String, MetricCategory>() {
        {
            this.put("onCreate", MetricCategory.VIEW_LOADING);
        }
    };

    private MetricCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public static MetricCategory categoryForMethod(String fullMethodName) {
        if(fullMethodName == null) {
            return NONE;
        } else {
            String methodName = null;
            int hashIndex = fullMethodName.indexOf("#");
            if(hashIndex >= 0) {
                methodName = fullMethodName.substring(hashIndex + 1);
            }

            MetricCategory category = methodMap.get(methodName);
            if(category == null) {
                category = NONE;
            }

            return category;
        }
    }
}
