//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.harvest.type.HarvestErrorCodes;
import com.newrelic.agent.android.instrumentation.httpclient.ContentBufferingResponseEntityImpl;
import com.newrelic.agent.android.instrumentation.httpclient.HttpRequestEntityImpl;
import com.newrelic.agent.android.instrumentation.httpclient.HttpResponseEntityImpl;
import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.newrelic.agent.android.tracing.TraceMachine;
import com.newrelic.agent.android.util.ExceptionHelper;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.HttpUriRequest;

public class TransactionStateUtil implements HarvestErrorCodes {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String APP_DATA_HEADER = "X-NewRelic-App-Data";
    public static final String CROSS_PROCESS_ID_HEADER = "X-NewRelic-ID";

    public TransactionStateUtil() {
    }

    public static void inspectAndInstrument(TransactionState transactionState, String url, String httpMethod) {
        transactionState.setUrl(url);
        transactionState.setHttpMethod(httpMethod);
        transactionState.setCarrier(Agent.getActiveNetworkCarrier());
        transactionState.setWanType(Agent.getActiveNetworkWanType());
    }

    public static void inspectAndInstrument(TransactionState transactionState, HttpURLConnection conn) {
        inspectAndInstrument(transactionState, conn.getURL().toString(), conn.getRequestMethod());
    }

    public static void setCrossProcessHeader(HttpURLConnection conn) {
        String crossProcessId = Agent.getCrossProcessId();
        if(crossProcessId != null) {
            conn.setRequestProperty(CROSS_PROCESS_ID_HEADER, crossProcessId);
        }

    }

    public static void inspectAndInstrumentResponse(TransactionState transactionState, String appData, int contentLength, int statusCode) {
        if(appData != null && !appData.equals("")) {
            transactionState.setAppData(appData);
        }

        if(contentLength >= 0) {
            transactionState.setBytesReceived((long)contentLength);
        }

        transactionState.setStatusCode(statusCode);
    }

    public static void inspectAndInstrumentResponse(TransactionState transactionState, HttpURLConnection conn) {
        String appData = conn.getHeaderField(APP_DATA_HEADER);
        int contentLength = conn.getContentLength();
        int statusCode = 0;

        try {
            statusCode = conn.getResponseCode();
        } catch (IOException var6) {
            log.debug("Failed to retrieve response code due to an I/O exception: " + var6.getMessage());
        } catch (NullPointerException var7) {
            log.error("Failed to retrieve response code due to underlying (Harmony?) NPE", var7);
        }

        inspectAndInstrumentResponse(transactionState, appData, contentLength, statusCode);
    }

    public static HttpRequest inspectAndInstrument(TransactionState transactionState, HttpHost host, HttpRequest request) {
        addCrossProcessIdHeader(request);
        String url = null;
        RequestLine requestLine = request.getRequestLine();
        if(requestLine != null) {
            String uri = requestLine.getUri();
            boolean isAbsoluteUri = uri != null && uri.length() >= 10 && uri.substring(0, 10).contains("://");
            if(!isAbsoluteUri && uri != null && host != null) {
                String prefix = host.toURI();
                url = prefix + (!prefix.endsWith("/") && !uri.startsWith("/")?"/":"") + uri;
            } else if(isAbsoluteUri) {
                url = uri;
            }

            inspectAndInstrument(transactionState, url, requestLine.getMethod());
        }

        if(transactionState.getUrl() != null && transactionState.getHttpMethod() != null) {
            wrapRequestEntity(transactionState, request);
            return request;
        } else {
            try {
                throw new Exception("TransactionData constructor was not provided with a valid URL, host or HTTP method");
            } catch (Exception var8) {
                AgentLogManager.getAgentLog().error(MessageFormat.format("TransactionStateUtil.inspectAndInstrument(...) for {0} could not determine request URL or HTTP method [host={1}, requestLine={2}]", new Object[]{request.getClass().getCanonicalName(), host, requestLine}), var8);
                return request;
            }
        }
    }

    public static HttpUriRequest inspectAndInstrument(TransactionState transactionState, HttpUriRequest request) {
        addCrossProcessIdHeader(request);
        inspectAndInstrument(transactionState, request.getURI().toString(), request.getMethod());
        wrapRequestEntity(transactionState, request);
        return request;
    }

    private static void addCrossProcessIdHeader(HttpRequest request) {
        String crossProcessId = Agent.getCrossProcessId();
        if(crossProcessId != null) {
            TraceMachine.setCurrentTraceParam("cross_process_data", crossProcessId);
            request.setHeader(CROSS_PROCESS_ID_HEADER, crossProcessId);
        }

    }

    private static void wrapRequestEntity(TransactionState transactionState, HttpRequest request) {
        if(request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)request;
            if(entityEnclosingRequest.getEntity() != null) {
                entityEnclosingRequest.setEntity(new HttpRequestEntityImpl(entityEnclosingRequest.getEntity(), transactionState));
            }
        }

    }

    public static HttpResponse inspectAndInstrument(TransactionState transactionState, HttpResponse response) {
        transactionState.setStatusCode(response.getStatusLine().getStatusCode());
        Header[] appDataHeader = response.getHeaders(APP_DATA_HEADER);
        if(appDataHeader != null && appDataHeader.length > 0 && !"".equals(appDataHeader[0].getValue())) {
            transactionState.setAppData(appDataHeader[0].getValue());
        }

        Header[] contentLengthHeader = response.getHeaders(CONTENT_LENGTH_HEADER);
        long contentLengthFromHeader = -1L;
        if(contentLengthHeader != null && contentLengthHeader.length > 0) {
            try {
                contentLengthFromHeader = Long.parseLong(contentLengthHeader[0].getValue());
                transactionState.setBytesReceived(contentLengthFromHeader);
                addTransactionAndErrorData(transactionState, response);
            } catch (NumberFormatException var7) {
                log.warning("Failed to parse content length: " + var7.toString());
            }
        } else if(response.getEntity() != null) {
            response.setEntity(new HttpResponseEntityImpl(response.getEntity(), transactionState, contentLengthFromHeader));
        } else {
            transactionState.setBytesReceived(0L);
            addTransactionAndErrorData(transactionState, null);
        }

        return response;
    }

    public static void setErrorCodeFromException(TransactionState transactionState, Exception e) {
        int exceptionAsErrorCode = ExceptionHelper.exceptionToErrorCode(e);
        log.error("TransactionStateUtil: Attempting to convert network exception " + e.getClass().getName() + " to error code.");
        transactionState.setErrorCode(exceptionAsErrorCode);
    }

    private static void addTransactionAndErrorData(TransactionState transactionState, HttpResponse response) {
        TransactionData transactionData = transactionState.end();
        if(transactionData != null) {
            TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            if((long)transactionState.getStatusCode() >= 400L) {
                StringBuilder responseBody = new StringBuilder();
                Map<String, String> params = new TreeMap();
                if(response != null) {
                    try {
                        if(response.getEntity() != null) {
                            if(!(response.getEntity() instanceof HttpRequestEntityImpl)) {
                                response.setEntity(new ContentBufferingResponseEntityImpl(response.getEntity()));
                            }

                            InputStream content = response.getEntity().getContent();
                            if(content instanceof CountingInputStream) {
                                responseBody.append(((CountingInputStream)content).getBufferAsString());
                            } else {
                                log.error("Unable to wrap content stream for entity");
                            }
                        } else {
                            log.debug("null response entity. response-body will be reported empty");
                        }
                    } catch (IllegalStateException var7) {
                        log.error(var7.toString());
                    } catch (IOException var8) {
                        log.error(var8.toString());
                    }

                    Header[] contentTypeHeader = response.getHeaders(CONTENT_TYPE_HEADER);
                    String contentType = null;
                    if(contentTypeHeader != null && contentTypeHeader.length > 0 && !"".equals(contentTypeHeader[0].getValue())) {
                        contentType = contentTypeHeader[0].getValue();
                    }

                    if(contentType != null && contentType.length() > 0) {
                        params.put("content_type", contentType);
                    }
                }

                params.put("content_length", transactionState.getBytesReceived() + "");
                Measurements.addHttpError(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), responseBody.toString(), params);
            }

        }
    }
}
