//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Util {
    private static final Random random = new Random();

    public Util() {
    }

    public static String slurp(InputStream stream) throws IOException {
        char[] buf = new char[8192];
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        while(true) {
            int n = reader.read(buf);
            if(n < 0) {
                return sb.toString();
            }

            sb.append(buf, 0, n);
        }
    }

    public static String sanitizeUrl(String urlString) {
        if(urlString == null) {
            return null;
        } else {
            URL url;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException var3) {
                return null;
            }

            StringBuffer sanitizedUrl = new StringBuffer();
            sanitizedUrl.append(url.getProtocol());
            sanitizedUrl.append("://");
            sanitizedUrl.append(url.getHost());
            if(url.getPort() != -1) {
                sanitizedUrl.append(":");
                sanitizedUrl.append(url.getPort());
            }

            sanitizedUrl.append(url.getPath());
            return sanitizedUrl.toString();
        }
    }

    public static Random getRandom() {
        return random;
    }
}
