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

public final class DoubleSessionAttribute extends Table {
    public DoubleSessionAttribute() {
    }

    public static DoubleSessionAttribute getRootAsDoubleSessionAttribute(ByteBuffer _bb) {
        return getRootAsDoubleSessionAttribute(_bb, new DoubleSessionAttribute());
    }

    public static DoubleSessionAttribute getRootAsDoubleSessionAttribute(ByteBuffer _bb, DoubleSessionAttribute obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public DoubleSessionAttribute __assign(int _i, ByteBuffer _bb) {
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

    public double value() {
        int o = this.__offset(6);
        return o != 0?this.bb.getDouble(o + this.bb_pos):0.0D;
    }

    public boolean mutateValue(double value) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putDouble(o + this.bb_pos, value);
            return true;
        } else {
            return false;
        }
    }

    public static int createDoubleSessionAttribute(FlatBufferBuilder builder, int nameOffset, double value) {
        builder.startObject(2);
        addValue(builder, value);
        addName(builder, nameOffset);
        return endDoubleSessionAttribute(builder);
    }

    public static void startDoubleSessionAttribute(FlatBufferBuilder builder) {
        builder.startObject(2);
    }

    public static void addName(FlatBufferBuilder builder, int nameOffset) {
        builder.addOffset(0, nameOffset, 0);
    }

    public static void addValue(FlatBufferBuilder builder, double value) {
        builder.addDouble(1, value, 0.0D);
    }

    public static int endDoubleSessionAttribute(FlatBufferBuilder builder) {
        int o = builder.endObject();
        builder.required(o, 4);
        return o;
    }

    protected int keysCompare(Integer o1, Integer o2, ByteBuffer _bb) {
        return compareStrings(__offset(4, o1, _bb), __offset(4, o2, _bb), _bb);
    }

    public static DoubleSessionAttribute __lookup_by_key(int vectorLocation, String key, ByteBuffer bb) {
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
                    return (new DoubleSessionAttribute()).__assign(tableOffset, bb);
                }

                ++middle;
                start += middle;
                span -= middle;
            }
        }

        return null;
    }
}
