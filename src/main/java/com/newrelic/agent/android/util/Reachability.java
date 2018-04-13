//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.net.InetAddress;

public class Reachability {
    private static final int REACHABILITY_TIMEOUT = 500;

    public Reachability() {
    }

    public static boolean hasReachableNetworkConnection(Context context, String reachableHost) {
        boolean isReachable = false;

        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
            if(cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isMobile = activeNetwork.getType() == 0;
                boolean isWiFi = activeNetwork.getType() == 1;
                isReachable = (isMobile || isWiFi) && activeNetwork.isConnectedOrConnecting();
            }
        } catch (Exception var8) {
            try {
                InetAddress addr = InetAddress.getByName(reachableHost);
                isReachable = addr.isReachable(REACHABILITY_TIMEOUT);
            } catch (Exception var7) {
                isReachable = false;
            }
        }

        return isReachable;
    }

    public static boolean hasReachableWifiConnection(Context context) {
        boolean isReachable = false;

        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
            if(cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                if(activeNetwork != null) {
                    boolean isWiFi = activeNetwork.getType() == 1;
                    isReachable = isWiFi && activeNetwork.isConnectedOrConnecting();
                }
            }
        } catch (Exception var5) {
            isReachable = false;
        }

        return isReachable;
    }

    public static boolean hasReachableMobileConnection(Context context) {
        boolean isReachable = false;

        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService("connectivity");
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if(activeNetwork != null) {
                boolean isMobile = activeNetwork.getType() == 0;
                isReachable = isMobile && activeNetwork.isConnectedOrConnecting();
            }
        } catch (Exception var5) {
            isReachable = false;
        }

        return isReachable;
    }
}
