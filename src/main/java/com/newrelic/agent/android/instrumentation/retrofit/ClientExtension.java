//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.instrumentation.retrofit;

import com.newrelic.agent.android.Agent;
import com.newrelic.agent.android.TaskQueue;
import com.newrelic.agent.android.api.common.TransactionData;
import com.newrelic.agent.android.instrumentation.TransactionState;
import com.newrelic.agent.android.instrumentation.TransactionStateUtil;
import com.newrelic.agent.android.logging.AgentLog;
import com.newrelic.agent.android.logging.AgentLogManager;
import com.newrelic.agent.android.measurement.http.HttpTransactionMeasurement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;

public class ClientExtension implements Client {
    private static final AgentLog log = AgentLogManager.getAgentLog();
    private Client impl;
    private TransactionState transactionState;
    private Request request;

    public ClientExtension(Client impl) {
        this.impl = impl;
    }

    public Response execute(Request request) throws IOException {
        this.request = request;
        this.getTransactionState();
        request = this.setCrossProcessHeader(request);
        Response response = null;

        try {
            response = this.impl.execute(request);
            response = new Response(response.getUrl(), response.getStatus(), response.getReason(), response.getHeaders(), new ContentBufferingTypedInput(response.getBody()));
        } catch (IOException var4) {
            this.error(var4);
            throw var4;
        }

        this.checkResponse(response);
        return response;
    }

    private Request setCrossProcessHeader(Request request) {
        String crossProcessId = Agent.getCrossProcessId();
        if(crossProcessId != null) {
            List<Header> headers = new ArrayList<>(request.getHeaders());
            headers.add(new Header("X-NewRelic-ID", crossProcessId));
            request = new Request(request.getMethod(), request.getUrl(), headers, request.getBody());
        }

        return request;
    }

    private void checkResponse(Response response) {
        if(!this.getTransactionState().isComplete()) {
            RetrofitTransactionStateUtil.inspectAndInstrumentResponse(this.getTransactionState(), response);
        }

    }

    private TransactionState getTransactionState() {
        if(this.transactionState == null) {
            this.transactionState = new TransactionState();
            RetrofitTransactionStateUtil.inspectAndInstrument(this.transactionState, this.request);
        }

        return this.transactionState;
    }

    private void error(Exception e) {
        log.debug("handling exception: " + e.toString());
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
