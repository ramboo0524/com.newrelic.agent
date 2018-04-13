//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp3;

import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.util.Map;
import java.util.TreeMap;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3TransactionStateUtil extends TransactionStateUtil {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private static final long CONTENTLENGTH_UNKNOWN = -1L;

    public OkHttp3TransactionStateUtil() {
    }

    public static void inspectAndInstrument(TransactionState transactionState, Request request) {
        if(request == null) {
            log.warning("Missing request");
        } else {
            inspectAndInstrument(transactionState, request.url().toString(), request.method());
        }

    }

    public static Response inspectAndInstrumentResponse(TransactionState transactionState, Response response) {
        String appData = "";
//        int statusCode = true;
        long contentLength = 0L;
        int statusCode;
        if(response == null) {
            statusCode = 500;
            log.warning("Missing response");
        } else {
            appData = response.header("X-NewRelic-App-Data");
            statusCode = response.code();

            try {
                contentLength = exhaustiveContentLength(response);
            } catch (Exception var7) {
                ;
            }

            if(contentLength < 0L) {
                log.warning("Missing body or content length");
            }
        }

        inspectAndInstrumentResponse(transactionState, appData, (int)contentLength, statusCode);
        return addTransactionAndErrorData(transactionState, response);
    }

    private static long exhaustiveContentLength(Response response) {
        long contentLength = -1L;
        if(response != null) {
            if(response.body() != null) {
                contentLength = response.body().contentLength();
            }

            if(contentLength < 0L) {
                String responseBodyString = response.header("Content-Length");
                if(responseBodyString != null && responseBodyString.length() > 0) {
                    try {
                        contentLength = Long.parseLong(responseBodyString);
                    } catch (NumberFormatException var5) {
                        log.warning("Failed to parse content length: " + var5.toString());
                    }
                }
            }
        }

        return contentLength;
    }

    private static Response addTransactionAndErrorData(TransactionState transactionState, Response response) {
        TransactionData transactionData = transactionState.end();
        if(transactionData != null) {
            TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            if((long)transactionState.getStatusCode() >= 400L && response != null) {
                String contentTypeHeader = response.header("Content-Type");
                String contentType = null;
                Map<String, String> params = new TreeMap<>();
                if(contentTypeHeader != null && contentTypeHeader.length() > 0 && !"".equals(contentTypeHeader)) {
                    params.put("content_type", contentType);
                }

                params.put("content_length", transactionState.getBytesReceived() + "");
                String responseBodyString = "";

                try {
                    long contentLength = exhaustiveContentLength(response);
                    if(contentLength > 0L) {
                        responseBodyString = response.peekBody(contentLength).string();
                    }
                } catch (Exception var10) {
                    if(response.message() != null) {
                        log.warning("Missing response body, using response message");
                        responseBodyString = response.message();
                    }
                }

                Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), responseBodyString, params);
            }
        }

        return response;
    }
}
