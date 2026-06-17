package com.behemiron.engine.external.effect.effekseer;

/**
 * 表示当前 Native 构建或 backend 可提供的公共功能位。
 */
public enum EffekseerFeature {
    COLLISION_CALLBACK(1),
    VULKAN(1 << 1),
    OPENGL(1 << 2),
    EFFECT_DEPENDENCY_VIEW(1 << 3),
    TYPED_PLAY_OPTIONS(1 << 4),
    BATCH_API(1 << 5),
    DIAGNOSTICS(1 << 6),
    OPENGL_FRAME_API(1 << 7),
    VULKAN_FRAME_API(1 << 8);

    private final int nativeMask;

    EffekseerFeature(int nativeMask) {
        this.nativeMask = nativeMask;
    }

    /**
     * 返回与 Native 层功能位对应的掩码值。
     *
     * @return Native 功能位
     */
    public int nativeMask() {
        return nativeMask;
    }

    /**
     * 判断某个位掩码中是否包含当前功能。
     *
     * @param flags Native 功能位掩码
     * @return 是否包含当前功能
     */
    public boolean isEnabledIn(int flags) {
        return (flags & nativeMask) != 0;
    }
}
