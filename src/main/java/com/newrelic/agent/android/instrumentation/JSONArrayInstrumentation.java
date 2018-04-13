//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.tracing.TraceMachine;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayInstrumentation {
    private static final ArrayList<String> categoryParams = new ArrayList<>(Arrays.asList("category", MetricCategory.class.getName(), "JSON"));

    @TraceConstructor
    public static JSONArray init(String json) throws JSONException {
        if(json == null) {
            throw new JSONException("Failed to initialize JSONArray: json string is null.");
        } else {
            try {
                TraceMachine.enterMethod("JSONArray#<init>", categoryParams);
                JSONArray jsonArray = new JSONArray(json);
                TraceMachine.exitMethod();
                return jsonArray;
            } catch (JSONException var3) {
                TraceMachine.exitMethod();
                throw var3;
            }
        }
    }

    @ReplaceCallSite(
            scope = "org.json.JSONArray"
    )
    public static String toString(JSONArray jsonArray) {
        TraceMachine.enterMethod("JSONArray#toString", categoryParams);
        String jsonString = jsonArray.toString();
        TraceMachine.exitMethod();
        return jsonString;
    }

    @ReplaceCallSite(
            scope = "org.json.JSONArray"
    )
    public static String toString(JSONArray jsonArray, int indentFactor) throws JSONException {
        try {
            TraceMachine.enterMethod("JSONArray#toString", categoryParams);
            String jsonString = jsonArray.toString(indentFactor);
            TraceMachine.exitMethod();
            return jsonString;
        } catch (JSONException var4) {
            TraceMachine.exitMethod();
            throw var4;
        }
    }
}
