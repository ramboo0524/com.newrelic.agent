//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.httpclient;

import com.newrelic.agent.android.Measurements;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerSource;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.AbstractHttpMessage;

public final class HttpResponseEntityImpl implements HttpEntity, StreamCompleteListener {
    private static final String TRANSFER_ENCODING_HEADER = "Transfer-Encoding";
    private static final String ENCODING_CHUNKED = "chunked";
    private final HttpEntity impl;
    private final TransactionState transactionState;
    private final long contentLengthFromHeader;
    private CountingInputStream contentStream;
    private static final AgentLog log = AgentLogManager.getAgentLog();

    public HttpResponseEntityImpl(HttpEntity impl, TransactionState transactionState, long contentLengthFromHeader) {
        this.impl = impl;
        this.transactionState = transactionState;
        this.contentLengthFromHeader = contentLengthFromHeader;
    }

    public void consumeContent() throws IOException {
        try {
            this.impl.consumeContent();
        } catch (IOException var2) {
            this.handleException(var2);
            throw var2;
        }
    }

    public InputStream getContent() throws IOException, IllegalStateException {
        if(this.contentStream != null) {
            return this.contentStream;
        } else {
            try {
                boolean shouldBuffer = true;
                if(this.impl instanceof AbstractHttpMessage) {
                    AbstractHttpMessage message = (AbstractHttpMessage)this.impl;
                    Header transferEncodingHeader = message.getLastHeader(TRANSFER_ENCODING_HEADER);
                    if(transferEncodingHeader != null && ENCODING_CHUNKED.equalsIgnoreCase(transferEncodingHeader.getValue())) {
                        shouldBuffer = false;
                    }
                } else if(this.impl instanceof HttpEntityWrapper) {
                    HttpEntityWrapper entityWrapper = (HttpEntityWrapper)this.impl;
                    shouldBuffer = !entityWrapper.isChunked();
                }

                try {
                    this.contentStream = new CountingInputStream(this.impl.getContent(), shouldBuffer);
                    this.contentStream.addStreamCompleteListener(this);
                } catch (IllegalArgumentException var4) {
                    log.error("HttpResponseEntityImpl: " + var4.toString());
                }

                return this.contentStream;
            } catch (IOException var5) {
                this.handleException(var5);
                throw var5;
            }
        }
    }

    public Header getContentEncoding() {
        return this.impl.getContentEncoding();
    }

    public long getContentLength() {
        return this.impl.getContentLength();
    }

    public Header getContentType() {
        return this.impl.getContentType();
    }

    public boolean isChunked() {
        return this.impl.isChunked();
    }

    public boolean isRepeatable() {
        return this.impl.isRepeatable();
    }

    public boolean isStreaming() {
        return this.impl.isStreaming();
    }

    public void writeTo(OutputStream outstream) throws IOException {
        if(!this.transactionState.isComplete()) {
            CountingOutputStream outputStream = null;

            try {
                outputStream = new CountingOutputStream(outstream);
                this.impl.writeTo(outputStream);
            } catch (IOException var4) {
                if(outputStream != null) {
                    this.handleException(var4, outputStream.getCount());
                }

                var4.printStackTrace();
                throw var4;
            }

            if(!this.transactionState.isComplete()) {
                if(this.contentLengthFromHeader >= 0L) {
                    this.transactionState.setBytesReceived(this.contentLengthFromHeader);
                } else {
                    this.transactionState.setBytesReceived(outputStream.getCount());
                }

                this.addTransactionAndErrorData(this.transactionState);
            }
        } else {
            this.impl.writeTo(outstream);
        }

    }

    public void streamComplete(StreamCompleteEvent e) {
        StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
        source.removeStreamCompleteListener(this);
        if(!this.transactionState.isComplete()) {
            if(this.contentLengthFromHeader >= 0L) {
                this.transactionState.setBytesReceived(this.contentLengthFromHeader);
            } else {
                this.transactionState.setBytesReceived(e.getBytes());
            }

            this.addTransactionAndErrorData(this.transactionState);
        }

    }

    public void streamError(StreamCompleteEvent e) {
        StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
        source.removeStreamCompleteListener(this);
        TransactionStateUtil.setErrorCodeFromException(this.transactionState, e.getException());
        if(!this.transactionState.isComplete()) {
            this.transactionState.setBytesReceived(e.getBytes());
        }

    }

    private void addTransactionAndErrorData(TransactionState transactionState) {
        TransactionData transactionData = transactionState.end();
        if(transactionData != null) {
            TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            if((long)transactionState.getStatusCode() >= 400L) {
                StringBuilder responseBody = new StringBuilder();

                try {
                    InputStream errorStream = this.getContent();
                    if(errorStream instanceof CountingInputStream) {
                        responseBody.append(((CountingInputStream)errorStream).getBufferAsString());
                    }
                } catch (Exception var6) {
                    log.error(var6.toString());
                }

                Header contentType = this.impl.getContentType();
                Map<String, String> params = new TreeMap();
                if(contentType != null && contentType.getValue() != null && !"".equals(contentType.getValue())) {
                    params.put("content_type", contentType.getValue());
                }

                params.put("content_length", transactionState.getBytesReceived() + "");
                Measurements.addHttpError(transactionData, responseBody.toString(), params);
            }

        }
    }

    private void handleException(Exception e) {
        this.handleException(e, (Long)null);
    }

    private void handleException(Exception e, Long streamBytes) {
        TransactionStateUtil.setErrorCodeFromException(this.transactionState, e);
        if(!this.transactionState.isComplete()) {
            if(streamBytes != null) {
                this.transactionState.setBytesReceived(streamBytes);
            }

            TransactionData transactionData = this.transactionState.end();
            if(transactionData != null) {
                TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            }
        }

    }
}
