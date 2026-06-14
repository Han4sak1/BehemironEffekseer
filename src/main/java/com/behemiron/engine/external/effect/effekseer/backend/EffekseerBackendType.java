package com.behemiron.engine.external.effect.effekseer.backend;

/**
 * 表示 Effekseer 当前使用的渲染后端。
 */
public enum EffekseerBackendType {
    UNKNOWN(0),
    OPENGL(1),
    VULKAN(4);

    private final int nativeValue;

    EffekseerBackendType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * 返回与 Native 层 C ABI 对应的枚举值。
     *
     * @return Native 层后端枚举值
     */
    public int nativeValue() {
        return nativeValue;
    }

    /**
     * 根据 Native 层枚举值解析后端类型。
     *
     * @param value Native 层后端枚举值
     * @return 对应的后端类型，未知值会回退到 {@link #UNKNOWN}
     */
    public static EffekseerBackendType fromNative(int value) {
        for (EffekseerBackendType type : values()) {
            if (type.nativeValue == value) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
