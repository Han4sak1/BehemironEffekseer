package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的碰撞查询结构。
 */
@Name("BehemironEffekseerCollisionQuery")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerCollisionQueryNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerCollisionQueryNative() {
        allocate();
    }

    public BehemironEffekseerCollisionQueryNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    public native float start_x();

    public native BehemironEffekseerCollisionQueryNative start_x(float value);

    public native float start_y();

    public native BehemironEffekseerCollisionQueryNative start_y(float value);

    public native float start_z();

    public native BehemironEffekseerCollisionQueryNative start_z(float value);

    public native float end_x();

    public native BehemironEffekseerCollisionQueryNative end_x(float value);

    public native float end_y();

    public native BehemironEffekseerCollisionQueryNative end_y(float value);

    public native float end_z();

    public native BehemironEffekseerCollisionQueryNative end_z(float value);
}
