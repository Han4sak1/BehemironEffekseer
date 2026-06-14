package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的碰撞命中结果结构。
 */
@Name("BehemironEffekseerCollisionHit")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerCollisionHitNative extends Pointer {

    static {
        Loader.load();
    }

    public BehemironEffekseerCollisionHitNative() {
        allocate();
    }

    public BehemironEffekseerCollisionHitNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    public native float position_x();

    public native BehemironEffekseerCollisionHitNative position_x(float value);

    public native float position_y();

    public native BehemironEffekseerCollisionHitNative position_y(float value);

    public native float position_z();

    public native BehemironEffekseerCollisionHitNative position_z(float value);

    public native float normal_x();

    public native BehemironEffekseerCollisionHitNative normal_x(float value);

    public native float normal_y();

    public native BehemironEffekseerCollisionHitNative normal_y(float value);

    public native float normal_z();

    public native BehemironEffekseerCollisionHitNative normal_z(float value);

    public native float distance();

    public native BehemironEffekseerCollisionHitNative distance(float value);

    public native @Cast("uint32_t") int user_flags();

    public native BehemironEffekseerCollisionHitNative user_flags(@Cast("uint32_t") int value);
}
