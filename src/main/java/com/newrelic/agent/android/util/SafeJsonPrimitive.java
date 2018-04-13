//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import com.google.gson.JsonPrimitive;

public class SafeJsonPrimitive {
    public static final String NULL_STRING = "null";
    public static final Number NULL_NUMBER = 0.0f;
    public static final Boolean NULL_BOOL;
    public static final char NULL_CHAR = ' ';

    public SafeJsonPrimitive() {
    }

    public static String checkNull(String string) {
        return string == null?NULL_STRING:string;
    }

    public static Boolean checkNull(Boolean bool) {
        return bool == null?NULL_BOOL:bool;
    }

    public static Number checkNull(Number number) {
        return number == null?NULL_NUMBER:number;
    }

    public static Character checkNull(Character c) {
        return c == null?NULL_CHAR:c;
    }

    public static JsonPrimitive factory(Boolean bool) {
        return new JsonPrimitive(checkNull(bool));
    }

    public static JsonPrimitive factory(Number number) {
        return new JsonPrimitive(checkNull(number));
    }

    public static JsonPrimitive factory(String string) {
        return new JsonPrimitive(checkNull(string));
    }

    public static JsonPrimitive factory(Character character) {
        return new JsonPrimitive(checkNull(character));
    }

    static {
        NULL_BOOL = Boolean.FALSE;
    }
}
