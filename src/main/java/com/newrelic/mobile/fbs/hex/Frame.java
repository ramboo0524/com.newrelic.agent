//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs.hex;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Frame extends Table {
    public Frame() {
    }

    public static Frame getRootAsFrame(ByteBuffer _bb) {
        return getRootAsFrame(_bb, new Frame());
    }

    public static Frame getRootAsFrame(ByteBuffer _bb, Frame obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public Frame __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public String value() {
        int o = this.__offset(4);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer valueAsByteBuffer() {
        return this.__vector_as_bytebuffer(4, 1);
    }

    public String className() {
        int o = this.__offset(6);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer classNameAsByteBuffer() {
        return this.__vector_as_bytebuffer(6, 1);
    }

    public String methodName() {
        int o = this.__offset(8);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer methodNameAsByteBuffer() {
        return this.__vector_as_bytebuffer(8, 1);
    }

    public String fileName() {
        int o = this.__offset(10);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer fileNameAsByteBuffer() {
        return this.__vector_as_bytebuffer(10, 1);
    }

    public long lineNumber() {
        int o = this.__offset(12);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateLineNumber(long lineNumber) {
        int o = this.__offset(12);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, lineNumber);
            return true;
        } else {
            return false;
        }
    }

    public long address() {
        int o = this.__offset(14);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAddress(long address) {
        int o = this.__offset(14);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, address);
            return true;
        } else {
            return false;
        }
    }

    public static int createFrame(FlatBufferBuilder builder, int valueOffset, int classNameOffset, int methodNameOffset, int fileNameOffset, long lineNumber, long address) {
        builder.startObject(6);
        addAddress(builder, address);
        addLineNumber(builder, lineNumber);
        addFileName(builder, fileNameOffset);
        addMethodName(builder, methodNameOffset);
        addClassName(builder, classNameOffset);
        addValue(builder, valueOffset);
        return endFrame(builder);
    }

    public static void startFrame(FlatBufferBuilder builder) {
        builder.startObject(6);
    }

    public static void addValue(FlatBufferBuilder builder, int valueOffset) {
        builder.addOffset(0, valueOffset, 0);
    }

    public static void addClassName(FlatBufferBuilder builder, int classNameOffset) {
        builder.addOffset(1, classNameOffset, 0);
    }

    public static void addMethodName(FlatBufferBuilder builder, int methodNameOffset) {
        builder.addOffset(2, methodNameOffset, 0);
    }

    public static void addFileName(FlatBufferBuilder builder, int fileNameOffset) {
        builder.addOffset(3, fileNameOffset, 0);
    }

    public static void addLineNumber(FlatBufferBuilder builder, long lineNumber) {
        builder.addLong(4, lineNumber, 0L);
    }

    public static void addAddress(FlatBufferBuilder builder, long address) {
        builder.addLong(5, address, 0L);
    }

    public static int endFrame(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }
}
