package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的 typed 播放参数结构。
 */
@Name("BehemironEffekseerPlayOptions")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerPlayOptionsNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerPlayOptionsNative() {
        allocate();
    }

    public BehemironEffekseerPlayOptionsNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    public native float position_x();

    public native BehemironEffekseerPlayOptionsNative position_x(float value);

    public native float position_y();

    public native BehemironEffekseerPlayOptionsNative position_y(float value);

    public native float position_z();

    public native BehemironEffekseerPlayOptionsNative position_z(float value);

    public native float rotation_x();

    public native BehemironEffekseerPlayOptionsNative rotation_x(float value);

    public native float rotation_y();

    public native BehemironEffekseerPlayOptionsNative rotation_y(float value);

    public native float rotation_z();

    public native BehemironEffekseerPlayOptionsNative rotation_z(float value);

    public native float scale_x();

    public native BehemironEffekseerPlayOptionsNative scale_x(float value);

    public native float scale_y();

    public native BehemironEffekseerPlayOptionsNative scale_y(float value);

    public native float scale_z();

    public native BehemironEffekseerPlayOptionsNative scale_z(float value);

    public native int start_frame();

    public native BehemironEffekseerPlayOptionsNative start_frame(int value);
}
