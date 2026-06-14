package com.behemiron.engine.external.effect.effekseer.data;

/**
 * 表示特效依赖清单中的资源类型。
 */
public enum EffekseerDependencyType {
    TEXTURE_COLOR(0),
    TEXTURE_NORMAL(1),
    TEXTURE_DISTORTION(2),
    MODEL(3),
    MATERIAL(4),
    CURVE(5);

    private final int nativeValue;

    EffekseerDependencyType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * 返回 Native 层依赖类型值。
     *
     * @return Native 层依赖类型值
     */
    public int nativeValue() {
        return nativeValue;
    }

    /**
     * 根据 Native 层值解析依赖类型。
     *
     * @param value Native 层依赖类型值
     * @return 对应依赖类型
     */
    public static EffekseerDependencyType fromNative(int value) {
        for (EffekseerDependencyType type : values()) {
            if (type.nativeValue == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的依赖类型: " + value);
    }
}
