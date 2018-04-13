//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.okhttp2;

import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class CallbackExtension implements Callback {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private TransactionState transactionState;
    private Callback impl;

    public CallbackExtension(Callback impl, TransactionState transactionState) {
        this.impl = impl;
        this.transactionState = transactionState;
    }

    public void onFailure(Request request, IOException e) {
        this.error(e);
        this.impl.onFailure(request, e);
    }

    public void onResponse(Response response) throws IOException {
        response = this.checkResponse(response);
        this.impl.onResponse(response);
    }

    private Response checkResponse(Response response) {
        if(!this.getTransactionState().isComplete()) {
            log.verbose("CallbackExtension.checkResponse() - transaction is not complete.  Inspecting and instrumenting response.");
            response = OkHttp2TransactionStateUtil.inspectAndInstrumentResponse(this.getTransactionState(), response);
        }

        return response;
    }

    private TransactionState getTransactionState() {
        return this.transactionState;
    }

    private void error(Exception e) {
        TransactionState transactionState = this.getTransactionState();
        TransactionStateUtil.setErrorCodeFromException(transactionState, e);
        if(!transactionState.isComplete()) {
            TransactionData transactionData = transactionState.end();
            if(transactionData != null) {
                TaskQueue.queue(new HttpTransactionMeasurement(transactionData.getUrl(), transactionData.getHttpMethod(), transactionData.getStatusCode(), transactionData.getErrorCode(), transactionData.getTimestamp(), (double)transactionData.getTime(), transactionData.getBytesSent(), transactionData.getBytesReceived(), transactionData.getAppData()));
            }
        }

    }
}
