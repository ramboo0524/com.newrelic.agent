//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs.ios;

public final class Arch {
    public static final byte armv7 = 0;
    public static final byte arm64 = 1;
    public static final String[] names = new String[]{"armv7", "arm64"};

    private Arch() {
    }

    public static String name(int e) {
        return names[e];
    }
}
