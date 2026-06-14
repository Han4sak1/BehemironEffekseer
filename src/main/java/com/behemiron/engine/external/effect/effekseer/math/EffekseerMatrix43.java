package com.behemiron.engine.external.effect.effekseer.math;

import java.util.Arrays;

/**
 * 表示按 Native 顺序排布的 3x4 矩阵。
 */
public final class EffekseerMatrix43 {

    private final float[] values;

    /**
     * 使用 12 个 float 创建矩阵。
     *
     * @param values12 12 个矩阵元素
     */
    public EffekseerMatrix43(float... values12) {
        if (values12 == null) {
            throw new IllegalArgumentException("values12 不能为空");
        }
        if (values12.length != 12) {
            throw new IllegalArgumentException("Matrix43 需要 12 个 float");
        }
        this.values = values12.clone();
    }

    /**
     * 返回矩阵元素的副本。
     *
     * @return 12 个矩阵元素
     */
    public float[] toArray() {
        return values.clone();
    }

    @Override
    public String toString() {
        return "EffekseerMatrix43" + Arrays.toString(values);
    }
}
