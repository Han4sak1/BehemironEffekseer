package com.behemiron.engine.external.effect.effekseer.math;

/**
 * 表示一个三维向量。
 *
 * @param x X 分量
 * @param y Y 分量
 * @param z Z 分量
 */
public record EffekseerVec3(
        float x,
        float y,
        float z
) {
    /**
     * 返回零向量。
     *
     * @return 零向量
     */
    public static EffekseerVec3 zero() {
        return new EffekseerVec3(0.0f, 0.0f, 0.0f);
    }

    /**
     * 返回单位缩放向量。
     *
     * @return 单位缩放向量
     */
    public static EffekseerVec3 one() {
        return new EffekseerVec3(1.0f, 1.0f, 1.0f);
    }
}
