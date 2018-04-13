//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.newrelic.agent.android.api.common.WanType;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import java.text.MessageFormat;

public final class Connectivity {
    private static final String ANDROID = "Android";
    private static AgentLog log = AgentLogManager.getAgentLog();

    public Connectivity() {
    }

    public static String carrierNameFromContext(Context context) {
        NetworkInfo networkInfo;
        try {
            networkInfo = getNetworkInfo(context);
        } catch (SecurityException var3) {
            return "unknown";
        }

        if(!isConnected(networkInfo)) {
            return "none";
        } else if(isWan(networkInfo)) {
            return carrierNameFromTelephonyManager(context);
        } else if(isWifi(networkInfo)) {
            return "wifi";
        } else {
            log.warning(MessageFormat.format("Unknown network type: {0} [{1}]", networkInfo.getTypeName(), networkInfo.getType()));
            return "unknown";
        }
    }

    public static String wanType(Context context) {
        NetworkInfo networkInfo;
        try {
            networkInfo = getNetworkInfo(context);
        } catch (SecurityException var3) {
            return "unknown";
        }

        return !isConnected(networkInfo)?"none":(isWifi(networkInfo)?"wifi":(isWan(networkInfo)?connectionNameFromNetworkSubtype(networkInfo.getSubtype()):"unknown"));
    }

    private static boolean isConnected(NetworkInfo networkInfo) {
        return networkInfo != null && networkInfo.isConnected();
    }

    private static boolean isWan(NetworkInfo networkInfo) {
        switch(networkInfo.getType()) {
            case 0:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            case 1:
            default:
                return false;
        }
    }

    private static boolean isWifi(NetworkInfo networkInfo) {
        switch(networkInfo.getType()) {
            case 1:
            case 6:
            case 7:
            case 9:
                return true;
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            default:
                return false;
        }
    }

    private static NetworkInfo getNetworkInfo(Context context) throws SecurityException {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService("connectivity");

        try {
            return connectivityManager.getActiveNetworkInfo();
        } catch (SecurityException var3) {
            log.warning("Cannot determine network state. Enable android.permission.ACCESS_NETWORK_STATE in your manifest.");
            throw var3;
        }
    }

    private static String carrierNameFromTelephonyManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService("phone");
        String networkOperator = telephonyManager.getNetworkOperatorName();
        if(networkOperator != null && !networkOperator.isEmpty()) {
            boolean smellsLikeAnEmulator = Build.PRODUCT.equals("google_sdk") || Build.PRODUCT.equals("sdk") || Build.PRODUCT.equals("sdk_x86") || Build.FINGERPRINT.startsWith("generic");
            return networkOperator.equals("Android") && smellsLikeAnEmulator?"wifi":networkOperator;
        } else {
            return "unknown";
        }
    }

    private static String connectionNameFromNetworkSubtype(int subType) {
        switch(subType) {
            case 0:
            default:
                return WanType.UNKNOWN;
            case 1:
                return WanType.GPRS;
            case 2:
                return WanType.EDGE;
            case 3:
                return WanType.UMTS;
            case 4:
                return WanType.CDMA;
            case 5:
                return WanType.EVDO_REV_0;
            case 6:
                return WanType.EVDO_REV_A;
            case 7:
                return WanType.RTT;
            case 8:
                return WanType.HSDPA;
            case 9:
                return WanType.HSUPA;
            case 10:
                return WanType.HSPA;
            case 11:
                return WanType.IDEN;
            case 12:
                return WanType.EVDO_REV_B;
            case 13:
                return WanType.LTE;
            case 14:
                return WanType.HRPD;
            case 15:
                return WanType.HSPAP;
        }
    }
}
