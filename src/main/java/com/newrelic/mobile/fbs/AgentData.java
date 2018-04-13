//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import com.newrelic.mobile.fbs.hex.HandledException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class AgentData extends Table {
    public AgentData() {
    }

    public static AgentData getRootAsAgentData(ByteBuffer _bb) {
        return getRootAsAgentData(_bb, new AgentData());
    }

    public static AgentData getRootAsAgentData(ByteBuffer _bb, AgentData obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public AgentData __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public StringSessionAttribute stringAttributes(int j) {
        return this.stringAttributes(new StringSessionAttribute(), j);
    }

    public StringSessionAttribute stringAttributes(StringSessionAttribute obj, int j) {
        int o = this.__offset(4);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int stringAttributesLength() {
        int o = this.__offset(4);
        return o != 0?this.__vector_len(o):0;
    }

    public StringSessionAttribute stringAttributesByKey(String key) {
        int o = this.__offset(4);
        return o != 0?StringSessionAttribute.__lookup_by_key(this.__vector(o), key, this.bb):null;
    }

    public LongSessionAttribute longAttributes(int j) {
        return this.longAttributes(new LongSessionAttribute(), j);
    }

    public LongSessionAttribute longAttributes(LongSessionAttribute obj, int j) {
        int o = this.__offset(6);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int longAttributesLength() {
        int o = this.__offset(6);
        return o != 0?this.__vector_len(o):0;
    }

    public LongSessionAttribute longAttributesByKey(String key) {
        int o = this.__offset(6);
        return o != 0?LongSessionAttribute.__lookup_by_key(this.__vector(o), key, this.bb):null;
    }

    public DoubleSessionAttribute doubleAttributes(int j) {
        return this.doubleAttributes(new DoubleSessionAttribute(), j);
    }

    public DoubleSessionAttribute doubleAttributes(DoubleSessionAttribute obj, int j) {
        int o = this.__offset(8);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int doubleAttributesLength() {
        int o = this.__offset(8);
        return o != 0?this.__vector_len(o):0;
    }

    public DoubleSessionAttribute doubleAttributesByKey(String key) {
        int o = this.__offset(8);
        return o != 0?DoubleSessionAttribute.__lookup_by_key(this.__vector(o), key, this.bb):null;
    }

    public BoolSessionAttribute boolAttributes(int j) {
        return this.boolAttributes(new BoolSessionAttribute(), j);
    }

    public BoolSessionAttribute boolAttributes(BoolSessionAttribute obj, int j) {
        int o = this.__offset(10);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int boolAttributesLength() {
        int o = this.__offset(10);
        return o != 0?this.__vector_len(o):0;
    }

    public BoolSessionAttribute boolAttributesByKey(String key) {
        int o = this.__offset(10);
        return o != 0?BoolSessionAttribute.__lookup_by_key(this.__vector(o), key, this.bb):null;
    }

    public HandledException handledExceptions(int j) {
        return this.handledExceptions(new HandledException(), j);
    }

    public HandledException handledExceptions(HandledException obj, int j) {
        int o = this.__offset(12);
        return o != 0?obj.__assign(this.__indirect(this.__vector(o) + j * 4), this.bb):null;
    }

    public int handledExceptionsLength() {
        int o = this.__offset(12);
        return o != 0?this.__vector_len(o):0;
    }

    public ApplicationInfo applicationInfo() {
        return this.applicationInfo(new ApplicationInfo());
    }

    public ApplicationInfo applicationInfo(ApplicationInfo obj) {
        int o = this.__offset(14);
        return o != 0?obj.__assign(this.__indirect(o + this.bb_pos), this.bb):null;
    }

    public RequestInformation requestInfo() {
        return this.requestInfo(new RequestInformation());
    }

    public RequestInformation requestInfo(RequestInformation obj) {
        int o = this.__offset(16);
        return o != 0?obj.__assign(this.__indirect(o + this.bb_pos), this.bb):null;
    }

    public static int createAgentData(FlatBufferBuilder builder, int stringAttributesOffset, int longAttributesOffset, int doubleAttributesOffset, int boolAttributesOffset, int handledExceptionsOffset, int applicationInfoOffset, int requestInfoOffset) {
        builder.startObject(7);
        addRequestInfo(builder, requestInfoOffset);
        addApplicationInfo(builder, applicationInfoOffset);
        addHandledExceptions(builder, handledExceptionsOffset);
        addBoolAttributes(builder, boolAttributesOffset);
        addDoubleAttributes(builder, doubleAttributesOffset);
        addLongAttributes(builder, longAttributesOffset);
        addStringAttributes(builder, stringAttributesOffset);
        return endAgentData(builder);
    }

    public static void startAgentData(FlatBufferBuilder builder) {
        builder.startObject(7);
    }

    public static void addStringAttributes(FlatBufferBuilder builder, int stringAttributesOffset) {
        builder.addOffset(0, stringAttributesOffset, 0);
    }

    public static int createStringAttributesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startStringAttributesVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addLongAttributes(FlatBufferBuilder builder, int longAttributesOffset) {
        builder.addOffset(1, longAttributesOffset, 0);
    }

    public static int createLongAttributesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startLongAttributesVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addDoubleAttributes(FlatBufferBuilder builder, int doubleAttributesOffset) {
        builder.addOffset(2, doubleAttributesOffset, 0);
    }

    public static int createDoubleAttributesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startDoubleAttributesVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addBoolAttributes(FlatBufferBuilder builder, int boolAttributesOffset) {
        builder.addOffset(3, boolAttributesOffset, 0);
    }

    public static int createBoolAttributesVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startBoolAttributesVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addHandledExceptions(FlatBufferBuilder builder, int handledExceptionsOffset) {
        builder.addOffset(4, handledExceptionsOffset, 0);
    }

    public static int createHandledExceptionsVector(FlatBufferBuilder builder, int[] data) {
        builder.startVector(4, data.length, 4);

        for(int i = data.length - 1; i >= 0; --i) {
            builder.addOffset(data[i]);
        }

        return builder.endVector();
    }

    public static void startHandledExceptionsVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static void addApplicationInfo(FlatBufferBuilder builder, int applicationInfoOffset) {
        builder.addOffset(5, applicationInfoOffset, 0);
    }

    public static void addRequestInfo(FlatBufferBuilder builder, int requestInfoOffset) {
        builder.addOffset(6, requestInfoOffset, 0);
    }

    public static int endAgentData(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }
}
