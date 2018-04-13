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

public final class LongSessionAttribute extends Table {
    public LongSessionAttribute() {
    }

    public static LongSessionAttribute getRootAsLongSessionAttribute(ByteBuffer _bb) {
        return getRootAsLongSessionAttribute(_bb, new LongSessionAttribute());
    }

    public static LongSessionAttribute getRootAsLongSessionAttribute(ByteBuffer _bb, LongSessionAttribute obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public LongSessionAttribute __assign(int _i, ByteBuffer _bb) {
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

    public long value() {
        int o = this.__offset(6);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateValue(long value) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, value);
            return true;
        } else {
            return false;
        }
    }

    public static int createLongSessionAttribute(FlatBufferBuilder builder, int nameOffset, long value) {
        builder.startObject(2);
        addValue(builder, value);
        addName(builder, nameOffset);
        return endLongSessionAttribute(builder);
    }

    public static void startLongSessionAttribute(FlatBufferBuilder builder) {
        builder.startObject(2);
    }

    public static void addName(FlatBufferBuilder builder, int nameOffset) {
        builder.addOffset(0, nameOffset, 0);
    }

    public static void addValue(FlatBufferBuilder builder, long value) {
        builder.addLong(1, value, 0L);
    }

    public static int endLongSessionAttribute(FlatBufferBuilder builder) {
        int o = builder.endObject();
        builder.required(o, 4);
        return o;
    }

    protected int keysCompare(Integer o1, Integer o2, ByteBuffer _bb) {
        return compareStrings(__offset(4, o1, _bb), __offset(4, o2, _bb), _bb);
    }

    public static LongSessionAttribute __lookup_by_key(int vectorLocation, String key, ByteBuffer bb) {
        byte[] byteKey = key.getBytes(Table.UTF8_CHARSET.get());
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
                    return (new LongSessionAttribute()).__assign(tableOffset, bb);
                }

                ++middle;
                start += middle;
                span -= middle;
            }
        }

        return null;
    }
}
