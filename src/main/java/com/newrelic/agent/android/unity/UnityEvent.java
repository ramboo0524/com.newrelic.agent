//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.unity;

import java.util.HashMap;
import java.util.Map;

public class UnityEvent {
    private String name;
    private Map<String, Object> attributes;

    public UnityEvent(String name) {
        this.name = name;
        this.attributes = new HashMap();
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void addAttribute(String name, Double value) {
        this.attributes.put(name, value);
    }
}
