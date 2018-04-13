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

public final class ApplicationLicense extends Table {
    public ApplicationLicense() {
    }

    public static ApplicationLicense getRootAsApplicationLicense(ByteBuffer _bb) {
        return getRootAsApplicationLicense(_bb, new ApplicationLicense());
    }

    public static ApplicationLicense getRootAsApplicationLicense(ByteBuffer _bb, ApplicationLicense obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public ApplicationLicense __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public String licenseKey() {
        int o = this.__offset(4);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer licenseKeyAsByteBuffer() {
        return this.__vector_as_bytebuffer(4, 1);
    }

    public long accountId() {
        int o = this.__offset(6);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAccountId(long accountId) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, accountId);
            return true;
        } else {
            return false;
        }
    }

    public long clusterAgentId() {
        int o = this.__offset(8);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateClusterAgentId(long clusterAgentId) {
        int o = this.__offset(8);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, clusterAgentId);
            return true;
        } else {
            return false;
        }
    }

    public static int createApplicationLicense(FlatBufferBuilder builder, int licenseKeyOffset, long accountId, long clusterAgentId) {
        builder.startObject(3);
        addClusterAgentId(builder, clusterAgentId);
        addAccountId(builder, accountId);
        addLicenseKey(builder, licenseKeyOffset);
        return endApplicationLicense(builder);
    }

    public static void startApplicationLicense(FlatBufferBuilder builder) {
        builder.startObject(3);
    }

    public static void addLicenseKey(FlatBufferBuilder builder, int licenseKeyOffset) {
        builder.addOffset(0, licenseKeyOffset, 0);
    }

    public static void addAccountId(FlatBufferBuilder builder, long accountId) {
        builder.addLong(1, accountId, 0L);
    }

    public static void addClusterAgentId(FlatBufferBuilder builder, long clusterAgentId) {
        builder.addLong(2, clusterAgentId, 0L);
    }

    public static int endApplicationLicense(FlatBufferBuilder builder) {
        int o = builder.endObject();
        builder.required(o, 4);
        return o;
    }

    protected int keysCompare(Integer o1, Integer o2, ByteBuffer _bb) {
        return compareStrings(__offset(4, o1, _bb), __offset(4, o2, _bb), _bb);
    }

    public static ApplicationLicense __lookup_by_key(int vectorLocation, String key, ByteBuffer bb) {
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
                    return (new ApplicationLicense()).__assign(tableOffset, bb);
                }

                ++middle;
                start += middle;
                span -= middle;
            }
        }

        return null;
    }
}
