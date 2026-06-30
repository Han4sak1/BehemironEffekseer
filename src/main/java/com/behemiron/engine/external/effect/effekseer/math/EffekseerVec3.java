package com.behemiron.engine.external.effect.effekseer.math;

import java.util.Objects;

/**
 * 表示一个三维向量。
 */
public final class EffekseerVec3 {

    private final float x;
    private final float y;
    private final float z;

    public EffekseerVec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

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

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerVec3 that)) {
            return false;
        }
        return Float.compare(that.x, x) == 0
                && Float.compare(that.y, y) == 0
                && Float.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "EffekseerVec3[x=" + x + ", y=" + y + ", z=" + z + ']';
    }
}
