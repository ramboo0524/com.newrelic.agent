//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.httpclient;

import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.instrumentation.io.CountingInputStream;
import com.newrelic.agent.android.instrumentation.io.CountingOutputStream;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteEvent;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteListener;
import com.newrelic.agent.android.instrumentation.io.StreamCompleteListenerSource;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

public final class HttpRequestEntityImpl implements HttpEntity, StreamCompleteListener {
    private final HttpEntity impl;
    private final TransactionState transactionState;

    public HttpRequestEntityImpl(HttpEntity impl, TransactionState transactionState) {
        this.impl = impl;
        this.transactionState = transactionState;
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
        try {
            if(!this.transactionState.isSent()) {
                CountingInputStream stream = new CountingInputStream(this.impl.getContent());
                stream.addStreamCompleteListener(this);
                return stream;
            } else {
                return this.impl.getContent();
            }
        } catch (IOException var2) {
            this.handleException(var2);
            throw var2;
        } catch (IllegalStateException var3) {
            this.handleException(var3);
            throw var3;
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
        try {
            if(!this.transactionState.isSent()) {
                CountingOutputStream stream = new CountingOutputStream(outstream);
                this.impl.writeTo(stream);
                this.transactionState.setBytesSent(stream.getCount());
            } else {
                this.impl.writeTo(outstream);
            }

        } catch (IOException var3) {
            this.handleException(var3);
            throw var3;
        }
    }

    public void streamComplete(StreamCompleteEvent e) {
        StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
        source.removeStreamCompleteListener(this);
        this.transactionState.setBytesSent(e.getBytes());
    }

    public void streamError(StreamCompleteEvent e) {
        StreamCompleteListenerSource source = (StreamCompleteListenerSource)e.getSource();
        source.removeStreamCompleteListener(this);
        this.handleException(e.getException(), e.getBytes());
    }

    private void handleException(Exception e) {
        this.handleException(e, (Long)null);
    }

    private void handleException(Exception e, Long streamBytes) {
        TransactionStateUtil.setErrorCodeFromException(this.transactionState, e);
        if(!this.transactionState.isComplete()) {
            if(streamBytes != null) {
                this.transactionState.setBytesSent(streamBytes);
            }

            TransactionData transactionData = this.transactionState.end();
            if(transactionData != null) {
                TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            }
        }

    }
}
