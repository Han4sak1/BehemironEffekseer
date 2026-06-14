package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FunctionPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的日志回调函数指针。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public abstract class BehemironEffekseerLogCallbackNative extends FunctionPointer {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    public BehemironEffekseerLogCallbackNative(Pointer p) {
        super(p);
    }

    protected BehemironEffekseerLogCallbackNative() {
        allocate();
    }

    private native void allocate();

    public abstract void call(
            @Cast("int32_t") int logType,
            @Cast("const char*") BytePointer message,
            Pointer userData
    );
}
