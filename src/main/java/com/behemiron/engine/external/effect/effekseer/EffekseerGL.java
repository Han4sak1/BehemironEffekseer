package com.behemiron.engine.external.effect.effekseer;

import com.behemiron.engine.external.effect.effekseer.backend.gl.EffekseerGLBackend;
import com.behemiron.engine.external.effect.effekseer.backend.gl.EffekseerGLManager;

/**
 * 提供 OpenGL backend 的便捷门面入口。
 */
public final class EffekseerGL {

    /**
     * 工具类不允许实例化。
     */
    private EffekseerGL() {
    }

    /**
     * 初始化 OpenGL backend。
     *
     * @return 是否初始化成功
     */
    public static boolean initialize() {
        return EffekseerGLBackend.initializeGL();
    }

    /**
     * 创建一个 OpenGL manager。
     *
     * @return 新创建的 OpenGL manager
     */
    public static EffekseerGLManager createManager() {
        return new EffekseerGLManager();
    }

    /**
     * 创建并初始化一个 OpenGL manager。
     *
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 已初始化的 OpenGL manager
     */
    public static EffekseerGLManager createInitializedManager(int spriteMaxCount, boolean srgbMode) {
        EffekseerGLManager manager = createManager();
        if (!manager.initialize(spriteMaxCount, srgbMode)) {
            manager.close();
            throw new IllegalStateException("初始化 OpenGL Effekseer manager 失败");
        }
        return manager;
    }

    /**
     * 先初始化 OpenGL backend，再创建并初始化一个 OpenGL manager。
     *
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 已初始化的 OpenGL manager
     */
    public static EffekseerGLManager initializeAndCreateManager(int spriteMaxCount, boolean srgbMode) {
        if (!initialize()) {
            throw new IllegalStateException("初始化 OpenGL Effekseer backend 失败");
        }
        return createInitializedManager(spriteMaxCount, srgbMode);
    }
}
