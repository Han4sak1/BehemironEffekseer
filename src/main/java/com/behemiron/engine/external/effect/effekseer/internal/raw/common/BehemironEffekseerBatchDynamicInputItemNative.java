package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的批量 dynamic input 结构。
 */
@Name("BehemironEffekseerBatchDynamicInputItem")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerBatchDynamicInputItemNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerBatchDynamicInputItemNative() {
        allocate();
    }

    public BehemironEffekseerBatchDynamicInputItemNative(long size) {
        allocateArray(size);
    }

    public BehemironEffekseerBatchDynamicInputItemNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    private native void allocateArray(long size);

    @Override
    @SuppressWarnings("unchecked")
    public BehemironEffekseerBatchDynamicInputItemNative position(long position) {
        return (BehemironEffekseerBatchDynamicInputItemNative) super.position(position);
    }

    public native int effect_handle();

    public native BehemironEffekseerBatchDynamicInputItemNative effect_handle(int value);

    public native int input_index();

    public native BehemironEffekseerBatchDynamicInputItemNative input_index(int value);

    public native float value();

    public native BehemironEffekseerBatchDynamicInputItemNative value(float value);
}
