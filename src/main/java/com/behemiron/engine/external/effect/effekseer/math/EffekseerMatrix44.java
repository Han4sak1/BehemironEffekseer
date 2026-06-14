package com.behemiron.engine.external.effect.effekseer.math;

import java.util.Arrays;

/**
 * 表示按 Native 顺序排布的 4x4 矩阵。
 */
public final class EffekseerMatrix44 {

    private final float[] values;

    /**
     * 使用 16 个 float 创建矩阵。
     *
     * @param values16 16 个矩阵元素
     */
    public EffekseerMatrix44(float... values16) {
        if (values16 == null) {
            throw new IllegalArgumentException("values16 不能为空");
        }
        if (values16.length != 16) {
            throw new IllegalArgumentException("Matrix44 需要 16 个 float");
        }
        this.values = values16.clone();
    }

    /**
     * 返回矩阵元素的副本。
     *
     * @return 16 个矩阵元素
     */
    public float[] toArray() {
        return values.clone();
    }

    @Override
    public String toString() {
        return "EffekseerMatrix44" + Arrays.toString(values);
    }
}
