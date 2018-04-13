//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.stores;

import android.content.Context;
import com.newrelic.agent.android.payload.Payload;
import com.newrelic.agent.android.payload.PayloadStore;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class SharedPrefsPayloadStore extends SharedPrefsStore implements PayloadStore<Payload> {
    public static final String STORE_FILE = "NRPayloadStore";

    public SharedPrefsPayloadStore(Context context) {
        this(context, "NRPayloadStore");
    }

    public SharedPrefsPayloadStore(Context context, String storeFilename) {
        super(context, storeFilename);
    }

    public boolean store(Payload payload) {
        LinkedHashSet<String> stringSet = this.toStringSet(payload);
        return super.store(payload.getUuid(), stringSet);
    }

    public List<Payload> fetchAll() {
        List<Payload> payloads = new ArrayList<>();
        Iterator var2 = super.fetchAll().iterator();

        while(var2.hasNext()) {
            Object object = var2.next();
            if(object instanceof HashSet) {
                try {
                    HashSet<String> stringSet = (HashSet<String>)object;
                    Iterator<String> iter = stringSet.iterator();
                    Payload payload = (new Gson()).fromJson(iter.next(), Payload.class);
                    payload.putBytes(this.decodePayload(iter.next()));
                    payloads.add(payload);
                } catch (JsonSyntaxException var7) {
                    ;
                }
            }
        }

        return payloads;
    }

    public void delete(Payload payload) {
        super.delete(payload.getUuid());
    }

    protected String encodePayload(Payload payload) {
        return this.encodeBytes(payload.getBytes());
    }

    protected byte[] decodePayload(String encodedString) {
        return this.decodeStringToBytes(encodedString);
    }

    protected String decodePayloadToString(byte[] decodedString) {
        return this.decodeBytesToString(decodedString);
    }

    private LinkedHashSet<String> toStringSet(Payload payload) {
        LinkedHashSet<String> stringSet = new LinkedHashSet<>(2);
        stringSet.add(payload.asJsonMeta());
        stringSet.add(this.encodePayload(payload));
        return stringSet;
    }
}
