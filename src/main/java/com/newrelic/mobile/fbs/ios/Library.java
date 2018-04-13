//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs.ios;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Library extends Table {
    public Library() {
    }

    public static Library getRootAsLibrary(ByteBuffer _bb) {
        return getRootAsLibrary(_bb, new Library());
    }

    public static Library getRootAsLibrary(ByteBuffer _bb, Library obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public Library __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public long uuidLow() {
        int o = this.__offset(4);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateUuidLow(long uuidLow) {
        int o = this.__offset(4);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, uuidLow);
            return true;
        } else {
            return false;
        }
    }

    public long uuidHigh() {
        int o = this.__offset(6);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateUuidHigh(long uuidHigh) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, uuidHigh);
            return true;
        } else {
            return false;
        }
    }

    public long address() {
        int o = this.__offset(8);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAddress(long address) {
        int o = this.__offset(8);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, address);
            return true;
        } else {
            return false;
        }
    }

    public boolean userLibrary() {
        int o = this.__offset(10);
        return o != 0?0 != this.bb.get(o + this.bb_pos):false;
    }

    public boolean mutateUserLibrary(boolean userLibrary) {
        int o = this.__offset(10);
        if(o != 0) {
            this.bb.put(o + this.bb_pos, (byte)(userLibrary?1:0));
            return true;
        } else {
            return false;
        }
    }

    public int arch() {
        int o = this.__offset(12);
        return o != 0?this.bb.get(o + this.bb_pos) & 255:0;
    }

    public boolean mutateArch(int arch) {
        int o = this.__offset(12);
        if(o != 0) {
            this.bb.put(o + this.bb_pos, (byte)arch);
            return true;
        } else {
            return false;
        }
    }

    public static int createLibrary(FlatBufferBuilder builder, long uuidLow, long uuidHigh, long address, boolean userLibrary, int arch) {
        builder.startObject(5);
        addAddress(builder, address);
        addUuidHigh(builder, uuidHigh);
        addUuidLow(builder, uuidLow);
        addArch(builder, arch);
        addUserLibrary(builder, userLibrary);
        return endLibrary(builder);
    }

    public static void startLibrary(FlatBufferBuilder builder) {
        builder.startObject(5);
    }

    public static void addUuidLow(FlatBufferBuilder builder, long uuidLow) {
        builder.addLong(0, uuidLow, 0L);
    }

    public static void addUuidHigh(FlatBufferBuilder builder, long uuidHigh) {
        builder.addLong(1, uuidHigh, 0L);
    }

    public static void addAddress(FlatBufferBuilder builder, long address) {
        builder.addLong(2, address, 0L);
    }

    public static void addUserLibrary(FlatBufferBuilder builder, boolean userLibrary) {
        builder.addBoolean(3, userLibrary, false);
    }

    public static void addArch(FlatBufferBuilder builder, int arch) {
        builder.addByte(4, (byte)arch, 0);
    }

    public static int endLibrary(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }

    public static void finishLibraryBuffer(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
    }
}
