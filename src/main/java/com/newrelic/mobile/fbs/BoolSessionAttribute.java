//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public final class BoolSessionAttribute extends Table {
    public BoolSessionAttribute() {
    }

    public static BoolSessionAttribute getRootAsBoolSessionAttribute(ByteBuffer _bb) {
        return getRootAsBoolSessionAttribute(_bb, new BoolSessionAttribute());
    }

    public static BoolSessionAttribute getRootAsBoolSessionAttribute(ByteBuffer _bb, BoolSessionAttribute obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public BoolSessionAttribute __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public String name() {
        int o = this.__offset(4);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer nameAsByteBuffer() {
        return this.__vector_as_bytebuffer(4, 1);
    }

    public boolean value() {
        int o = this.__offset(6);
        return o != 0 && 0 != this.bb.get(o + this.bb_pos);
    }

    public boolean mutateValue(boolean value) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.put(o + this.bb_pos, (byte)(value?1:0));
            return true;
        } else {
            return false;
        }
    }

    public static int createBoolSessionAttribute(FlatBufferBuilder builder, int nameOffset, boolean value) {
        builder.startObject(2);
        addName(builder, nameOffset);
        addValue(builder, value);
        return endBoolSessionAttribute(builder);
    }

    public static void startBoolSessionAttribute(FlatBufferBuilder builder) {
        builder.startObject(2);
    }

    public static void addName(FlatBufferBuilder builder, int nameOffset) {
        builder.addOffset(0, nameOffset, 0);
    }

    public static void addValue(FlatBufferBuilder builder, boolean value) {
        builder.addBoolean(1, value, false);
    }

    public static int endBoolSessionAttribute(FlatBufferBuilder builder) {
        int o = builder.endObject();
        builder.required(o, 4);
        return o;
    }

    protected int keysCompare(Integer o1, Integer o2, ByteBuffer _bb) {
        return compareStrings(__offset(4, o1, _bb), __offset(4, o2, _bb), _bb);
    }

    public static BoolSessionAttribute __lookup_by_key(int vectorLocation, String key, ByteBuffer bb) {
        byte[] byteKey = key.getBytes((Charset)Table.UTF8_CHARSET.get());
        int span = bb.getInt(vectorLocation - 4);
        int start = 0;

        while(span != 0) {
            int middle = span / 2;
            int tableOffset = __indirect(vectorLocation + 4 * (start + middle), bb);
            int comp = compareStrings(__offset(4, bb.array().length - tableOffset, bb), byteKey, bb);
            if(comp > 0) {
                span = middle;
            } else {
                if(comp >= 0) {
                    return (new BoolSessionAttribute()).__assign(tableOffset, bb);
                }

                ++middle;
                start += middle;
                span -= middle;
            }
        }

        return null;
    }
}
