//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

public class Location {
    private final String countryCode;
    private final String region;

    public Location(String countryCode, String region) {
        if(countryCode != null && region != null) {
            this.countryCode = countryCode;
            this.region = region;
        } else {
            throw new IllegalArgumentException("Country code and region must not be null.");
        }
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getRegion() {
        return this.region;
    }
}
