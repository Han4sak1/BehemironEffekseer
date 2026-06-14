package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的 effect dependency 结构。
 */
@Name("BehemironEffekseerEffectDependency")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerEffectDependencyNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerEffectDependencyNative() {
        allocate();
    }

    public BehemironEffekseerEffectDependencyNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    public native int type();

    public native BehemironEffekseerEffectDependencyNative type(int value);

    public native int slot();

    public native BehemironEffekseerEffectDependencyNative slot(int value);

    public native ShortPointer path_utf16();

    public native BehemironEffekseerEffectDependencyNative path_utf16(ShortPointer value);
}
