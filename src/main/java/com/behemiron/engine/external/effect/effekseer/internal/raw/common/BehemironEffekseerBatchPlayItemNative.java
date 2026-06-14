package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的批量播放项结构。
 */
@Name("BehemironEffekseerBatchPlayItem")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerBatchPlayItemNative extends Pointer {

    static {
        Loader.load();
    }

    public BehemironEffekseerBatchPlayItemNative() {
        allocate();
    }

    public BehemironEffekseerBatchPlayItemNative(long size) {
        allocateArray(size);
    }

    public BehemironEffekseerBatchPlayItemNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    private native void allocateArray(long size);

    @Override
    public BehemironEffekseerBatchPlayItemNative position(long position) {
        return (BehemironEffekseerBatchPlayItemNative) super.position(position);
    }

    public native Pointer effect_handle();

    public native BehemironEffekseerBatchPlayItemNative effect_handle(Pointer value);

    public native @Name("options") BehemironEffekseerPlayOptionsNative options();
}
