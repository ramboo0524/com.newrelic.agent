//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class RequestInformation extends Table {
    public RequestInformation() {
    }

    public static RequestInformation getRootAsRequestInformation(ByteBuffer _bb) {
        return getRootAsRequestInformation(_bb, new RequestInformation());
    }

    public static RequestInformation getRootAsRequestInformation(ByteBuffer _bb, RequestInformation obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public RequestInformation __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public long requestTimestampMs() {
        int o = this.__offset(4);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateRequestTimestampMs(long requestTimestampMs) {
        int o = this.__offset(4);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, requestTimestampMs);
            return true;
        } else {
            return false;
        }
    }

    public long requestAddress() {
        int o = this.__offset(6);
        return o != 0?(long)this.bb.getInt(o + this.bb_pos) & 4294967295L:0L;
    }

    public boolean mutateRequestAddress(long requestAddress) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putInt(o + this.bb_pos, (int)requestAddress);
            return true;
        } else {
            return false;
        }
    }

    public static int createRequestInformation(FlatBufferBuilder builder, long requestTimestampMs, long requestAddress) {
        builder.startObject(2);
        addRequestTimestampMs(builder, requestTimestampMs);
        addRequestAddress(builder, requestAddress);
        return endRequestInformation(builder);
    }

    public static void startRequestInformation(FlatBufferBuilder builder) {
        builder.startObject(2);
    }

    public static void addRequestTimestampMs(FlatBufferBuilder builder, long requestTimestampMs) {
        builder.addLong(0, requestTimestampMs, 0L);
    }

    public static void addRequestAddress(FlatBufferBuilder builder, long requestAddress) {
        builder.addInt(1, (int)requestAddress, 0);
    }

    public static int endRequestInformation(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }
}
