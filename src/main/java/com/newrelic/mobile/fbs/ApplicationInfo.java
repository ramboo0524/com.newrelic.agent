//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.mobile.fbs;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ApplicationInfo extends Table {
    public ApplicationInfo() {
    }

    public static ApplicationInfo getRootAsApplicationInfo(ByteBuffer _bb) {
        return getRootAsApplicationInfo(_bb, new ApplicationInfo());
    }

    public static ApplicationInfo getRootAsApplicationInfo(ByteBuffer _bb, ApplicationInfo obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb);
    }

    public void __init(int _i, ByteBuffer _bb) {
        this.bb_pos = _i;
        this.bb = _bb;
    }

    public ApplicationInfo __assign(int _i, ByteBuffer _bb) {
        this.__init(_i, _bb);
        return this;
    }

    public ApplicationLicense applicationLicense() {
        return this.applicationLicense(new ApplicationLicense());
    }

    public ApplicationLicense applicationLicense(ApplicationLicense obj) {
        int o = this.__offset(4);
        return o != 0?obj.__assign(this.__indirect(o + this.bb_pos), this.bb):null;
    }

    public int platform() {
        int o = this.__offset(6);
        return o != 0?this.bb.get(o + this.bb_pos) & 255:0;
    }

    public boolean mutatePlatform(int platform) {
        int o = this.__offset(6);
        if(o != 0) {
            this.bb.put(o + this.bb_pos, (byte)platform);
            return true;
        } else {
            return false;
        }
    }

    public String appVersion() {
        int o = this.__offset(8);
        return o != 0?this.__string(o + this.bb_pos):null;
    }

    public ByteBuffer appVersionAsByteBuffer() {
        return this.__vector_as_bytebuffer(8, 1);
    }

    public long appVersionId() {
        int o = this.__offset(10);
        return o != 0?this.bb.getLong(o + this.bb_pos):0L;
    }

    public boolean mutateAppVersionId(long appVersionId) {
        int o = this.__offset(10);
        if(o != 0) {
            this.bb.putLong(o + this.bb_pos, appVersionId);
            return true;
        } else {
            return false;
        }
    }

    public static int createApplicationInfo(FlatBufferBuilder builder, int applicationLicenseOffset, int platform, int appVersionOffset, long appVersionId) {
        builder.startObject(4);
        addAppVersionId(builder, appVersionId);
        addAppVersion(builder, appVersionOffset);
        addApplicationLicense(builder, applicationLicenseOffset);
        addPlatform(builder, platform);
        return endApplicationInfo(builder);
    }

    public static void startApplicationInfo(FlatBufferBuilder builder) {
        builder.startObject(4);
    }

    public static void addApplicationLicense(FlatBufferBuilder builder, int applicationLicenseOffset) {
        builder.addOffset(0, applicationLicenseOffset, 0);
    }

    public static void addPlatform(FlatBufferBuilder builder, int platform) {
        builder.addByte(1, (byte)platform, 0);
    }

    public static void addAppVersion(FlatBufferBuilder builder, int appVersionOffset) {
        builder.addOffset(2, appVersionOffset, 0);
    }

    public static void addAppVersionId(FlatBufferBuilder builder, long appVersionId) {
        builder.addLong(3, appVersionId, 0L);
    }

    public static int endApplicationInfo(FlatBufferBuilder builder) {
        int o = builder.endObject();
        return o;
    }
}
