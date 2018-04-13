//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.payload;

import com.newrelic.agent.android.payload.PayloadSender.CompletionHandler;
import java.util.concurrent.Callable;

class PayloadReaper implements Callable<PayloadSender> {
    final PayloadSender sender;
    final CompletionHandler handler;

    public PayloadReaper(PayloadSender sender, CompletionHandler handler) {
        if(sender == null) {
            throw new NullPointerException("Must provide payload sender!");
        } else {
            this.sender = sender;
            this.handler = handler;
        }
    }

    public PayloadSender call() throws Exception {
        PayloadSender payloadSender = null;

        try {
            payloadSender = this.sender.call();
            if(this.handler != null) {
                this.handler.onResponse(payloadSender);
            }

            return payloadSender;
        } catch (Exception var3) {
            if(this.handler != null) {
                this.handler.onException(this.sender, var3);
            }

            return null;
        }
    }

    public boolean equals(Object o) {
        if(this == o) {
            return true;
        } else if(o instanceof PayloadReaper) {
            PayloadReaper payloadReaper = (PayloadReaper)o;
            return this.sender.payload.equals(payloadReaper.sender.payload);
        } else {
            return false;
        }
    }

    public String getUuid() {
        return this.sender.getPayload().getUuid();
    }
}
