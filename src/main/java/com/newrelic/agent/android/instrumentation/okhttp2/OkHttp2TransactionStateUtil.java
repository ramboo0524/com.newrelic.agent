//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp2;

import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import java.util.Map;
import java.util.TreeMap;
import okio.Buffer;
import okio.BufferedSource;

public class OkHttp2TransactionStateUtil extends TransactionStateUtil {
    private static final AgentLog log = AgentLogManager.getAgentLog();

    public OkHttp2TransactionStateUtil() {
    }

    public static void inspectAndInstrument(TransactionState transactionState, Request request) {
        if(request == null) {
            log.warning("Missing request");
        } else {
            inspectAndInstrument(transactionState, request.urlString(), request.method());
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
                contentLength = response.body().contentLength();
            } catch (Exception var7) {
                log.warning("Missing body or content length");
            }
        }

        inspectAndInstrumentResponse(transactionState, appData, (int)contentLength, statusCode);
        return addTransactionAndErrorData(transactionState, response);
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
                    final ResponseBody body = response.body();
                    responseBodyString = body.string();
                    final Buffer contents = (new Buffer()).write(responseBodyString.getBytes());
                    ResponseBody responseBody = new ResponseBody() {
                        public MediaType contentType() {
                            return body.contentType();
                        }

                        public long contentLength() {
                            return contents.size();
                        }

                        public BufferedSource source() {
                            return contents;
                        }
                    };
                    response = response.newBuilder().body(responseBody).build();
                } catch (Exception var10) {
                    if(response.message() != null) {
                        log.warning("Missing response body, using response message");
                        responseBodyString = response.message();
                    }
                }

                if(transactionData.getErrorCode() != 0) {
                    Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), responseBodyString, params);
                } else {
                    Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), responseBodyString, params);
                }
            }
        }

        return response;
    }
}
