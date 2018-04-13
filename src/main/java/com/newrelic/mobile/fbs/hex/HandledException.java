//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs.hex;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import com.newrelic.mobile.fbs.ios.Library;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class HandledException extends Table {
    public HandledException() {
    }

    public static HandledException getRootAsHandledException(ByteBuffer _bb) {
        return getRootAsHandledException(_bb, new HandledException());
    }

    public static HandledException getRootAsHandledException(ByteBuffer _bb, HandledException obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public HandledException __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public long appUuidLow() {
        int o = this.__offset(4);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAppUuidLow(long appUuidLow) {
        int o = this.__offset(4);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, appUuidLow);
            return true;
        } else {
            return false;
        }
    }

    public long appUuidHigh() {
        int o = this.__offset(6);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAppUuidHigh(long appUuidHigh) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, appUuidHigh);
            return true;
        } else {
            return false;
        }
    }

    public String sessionId() {
        int o = this.__offset(8);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer sessionIdAsByteBuffer() {
        return this.__vector_as_bytebuffer(8, 1);
    }

    public long timestampMs() {
        int o = this.__offset(10);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateTimestampMs(long timestampMs) {
        int o = this.__offset(10);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, timestampMs);
            return true;
        } else {
            return false;
        }
    }

    public String name() {
        int o = this.__offset(12);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer nameAsByteBuffer() {
        return this.__vector_as_bytebuffer(12, 1);
    }

    public String message() {
        int o = this.__offset(14);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer messageAsByteBuffer() {
        return this.__vector_as_bytebuffer(14, 1);
    }

    public String cause() {
        int o = this.__offset(16);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer causeAsByteBuffer() {
        return this.__vector_as_bytebuffer(16, 1);
    }

    public Thread threads(int j) {
        return this.threads(new Thread(), j);
    }

    public Thread threads(Thread obj, int j) {
        int o = this.__offset(18);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int threadsLength() {
        int o = this.__offset(18);
        return o != 0?this.__vector_len(o):0;
    }

    public Library libraries(int j) {
        return this.libraries(new Library(), j);
    }

    public Library libraries(Library obj, int j) {
        int o = this.__offset(20);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int librariesLength() {
        int o = this.__offset(20);
        return o != 0?this.__vector_len(o):0;
    }

    public static int createHandledException(FlatBufferBuilder builder, long appUuidLow, long appUuidHigh, int sessionIdOffset, long timestampMs, int nameOffset, int messageOffset, int causeOffset, int threadsOffset, int librariesOffset) {
        builder.startObject(9);
        addTimestampMs(builder, timestampMs);
        addAppUuidHigh(builder, appUuidHigh);
        addAppUuidLow(builder, appUuidLow);
        addLibraries(builder, librariesOffset);
        addThreads(builder, threadsOffset);
        addCause(builder, causeOffset);
        addMessage(builder, messageOffset);
        addName(builder, nameOffset);
        addSessionId(builder, sessionIdOffset);
        return endHandledException(builder);
    }

    public static void startHandledException(FlatBufferBuilder builder) {
        builder.startObject(9);
    }

    public static void addAppUuidLow(FlatBufferBuilder builder, long appUuidLow) {
        builder.addLong(0, appUuidLow, 0L);
    }

    public static void addAppUuidHigh(FlatBufferBuilder builder, long appUuidHigh) {
        builder.addLong(1, appUuidHigh, 0L);
    }

    public static void addSessionId(FlatBufferBuilder builder, int sessionIdOffset) {
        builder.addOffset(2, sessionIdOffset, 0);
    }

    public static void addTimestampMs(FlatBufferBuilder builder, long timestampMs) {
        builder.addLong(3, timestampMs, 0L);
    }

    public static void addName(FlatBufferBuilder builder, int nameOffset) {
        builder.addOffset(4, nameOffset, 0);
    }

    public static void addMessage(FlatBufferBuilder builder, int messageOffset) {
        builder.addOffset(5, messageOffset, 0);
    }

    public static void addCause(FlatBufferBuilder builder, int causeOffset) {
        builder.addOffset(6, causeOffset, 0);
    }

    public static void addThreads(FlatBufferBuilder builder, int threadsOffset) {
        builder.addOffset(7, threadsOffset, 0);
    }

    public static int createThreadsVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startThreadsVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addLibraries(FlatBufferBuilder builder, int librariesOffset) {
        builder.addOffset(8, librariesOffset, 0);
    }

    public static int createLibrariesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startLibrariesVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static int endHandledException(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public static void finishHandledExceptionBuffer(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
    }
}
