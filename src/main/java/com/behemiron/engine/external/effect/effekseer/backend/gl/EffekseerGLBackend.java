package com.behemiron.engine.external.effect.effekseer.backend.gl;

import com.behemiron.engine.external.effect.effekseer.internal.raw.gl.BehemironEffekseerGLNative;

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
}
