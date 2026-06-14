package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的批量 transform 结构。
 */
@Name("BehemironEffekseerBatchTransformItem")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerBatchTransformItemNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerBatchTransformItemNative() {
        allocate();
    }

    public BehemironEffekseerBatchTransformItemNative(long size) {
        allocateArray(size);
    }

    public BehemironEffekseerBatchTransformItemNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    private native void allocateArray(long size);

    @Override
    @SuppressWarnings("unchecked")
    public BehemironEffekseerBatchTransformItemNative position(long position) {
        return (BehemironEffekseerBatchTransformItemNative) super.position(position);
    }

    public native int effect_handle();

    public native BehemironEffekseerBatchTransformItemNative effect_handle(int value);

    public native float position_x();

    public native BehemironEffekseerBatchTransformItemNative position_x(float value);

    public native float position_y();

    public native BehemironEffekseerBatchTransformItemNative position_y(float value);

    public native float position_z();

    public native BehemironEffekseerBatchTransformItemNative position_z(float value);

    public native float rotation_x();

    public native BehemironEffekseerBatchTransformItemNative rotation_x(float value);

    public native float rotation_y();

    public native BehemironEffekseerBatchTransformItemNative rotation_y(float value);

    public native float rotation_z();

    public native BehemironEffekseerBatchTransformItemNative rotation_z(float value);

    public native float scale_x();

    public native BehemironEffekseerBatchTransformItemNative scale_x(float value);

    public native float scale_y();

    public native BehemironEffekseerBatchTransformItemNative scale_y(float value);

    public native float scale_z();

    public native BehemironEffekseerBatchTransformItemNative scale_z(float value);
}
