package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的 manager stats 结构。
 */
@Name("BehemironEffekseerManagerStats")
@Properties(inherit = BehemironEffekseerPreset.class)
public class BehemironEffekseerManagerStatsNative extends Pointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerManagerStatsNative() {
        allocate();
    }

    public BehemironEffekseerManagerStatsNative(Pointer pointer) {
        super(pointer);
    }

    private native void allocate();

    public native int total_instance_count();

    public native BehemironEffekseerManagerStatsNative total_instance_count(int value);

    public native int rest_instance_count();

    public native BehemironEffekseerManagerStatsNative rest_instance_count(int value);

    public native int update_time();

    public native BehemironEffekseerManagerStatsNative update_time(int value);

    public native int draw_time();

    public native BehemironEffekseerManagerStatsNative draw_time(int value);

    public native int gpu_time();

    public native BehemironEffekseerManagerStatsNative gpu_time(int value);

    public native int draw_call_count();

    public native BehemironEffekseerManagerStatsNative draw_call_count(int value);

    public native int draw_vertex_count();

    public native BehemironEffekseerManagerStatsNative draw_vertex_count(int value);
}
