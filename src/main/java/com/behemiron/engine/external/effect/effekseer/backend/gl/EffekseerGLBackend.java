package com.behemiron.engine.external.effect.effekseer.backend.gl;

import com.behemiron.engine.external.effect.effekseer.internal.raw.gl.BehemironEffekseerGLNative;
import org.bytedeco.javacpp.Pointer;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 提供 OpenGL backend 的底层初始化入口。
 */
public final class EffekseerGLBackend {

    /**
     * 工具类不允许实例化。
     */
    private EffekseerGLBackend() {
    }

    /**
     * 初始化 OpenGL backend。
     *
     * @return 是否初始化成功
     */
    public static boolean initializeGL() {
        return BehemironEffekseerGLNative.backendInitializeGL() != 0;
    }

    /**
     * 在 OpenGL 加载态隔离中执行一段操作。
     *
     * @param body 要执行的逻辑
     * @param <T>  返回值类型
     * @return body 返回值
     */
    public static <T> T runWithLoadStateGuard(Supplier<T> body) {
        Objects.requireNonNull(body, "body");
        Pointer state = BehemironEffekseerGLNative.glBeginLoadState();
        try {
            return body.get();
        } finally {
            BehemironEffekseerGLNative.glEndLoadState(state);
        }
    }
}
