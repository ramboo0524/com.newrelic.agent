//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs;

public final class Platform {
    public static final byte Android = 0;
    public static final byte iOS = 1;
    public static final byte tvOS = 2;
    public static final String[] names = new String[]{"Android", "iOS", "tvOS"};

    public static String name(int e) {
        return names[e];
    }
}
