package com.behemiron.engine.external.effect.effekseer.math;

import java.util.Objects;

/**
 * 表示一组位置、旋转和缩放参数。
 *
 * @param position 位置
 * @param rotation 旋转
 * @param scale    缩放
 */
public record EffekseerTransform(
        EffekseerVec3 position,
        EffekseerVec3 rotation,
        EffekseerVec3 scale
) {
    /**
     * 创建一个默认变换。
     *
     * @return 默认变换
     */
    public static EffekseerTransform identity() {
        return new EffekseerTransform(EffekseerVec3.zero(), EffekseerVec3.zero(), EffekseerVec3.one());
    }

    /**
     * 构造时校验字段非空。
     */
    public EffekseerTransform {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(rotation, "rotation");
        Objects.requireNonNull(scale, "scale");
    }
}
