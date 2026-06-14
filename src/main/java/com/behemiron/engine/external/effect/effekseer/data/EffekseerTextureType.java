package com.behemiron.engine.external.effect.effekseer.data;

/**
 * 表示 Effekseer 资源中使用的纹理槽类型。
 */
public enum EffekseerTextureType {
    COLOR(0),
    NORMAL(1),
    DISTORTION(2);

    private final int nativeValue;

    EffekseerTextureType(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * 返回与 Native 层 C ABI 对应的纹理类型值。
     *
     * @return Native 层纹理类型值
     */
    public int nativeValue() {
        return nativeValue;
    }
}
