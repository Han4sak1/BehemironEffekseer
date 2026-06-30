package com.behemiron.engine.external.effect.effekseer.math;

import java.util.Objects;

/**
 * 表示一组位置、旋转和缩放参数。
 */
public final class EffekseerTransform {

    private final EffekseerVec3 position;
    private final EffekseerVec3 rotation;
    private final EffekseerVec3 scale;

    public EffekseerTransform(EffekseerVec3 position, EffekseerVec3 rotation, EffekseerVec3 scale) {
        this.position = Objects.requireNonNull(position, "position");
        this.rotation = Objects.requireNonNull(rotation, "rotation");
        this.scale = Objects.requireNonNull(scale, "scale");
    }

    /**
     * 创建一个默认变换。
     *
     * @return 默认变换
     */
    public static EffekseerTransform identity() {
        return new EffekseerTransform(EffekseerVec3.zero(), EffekseerVec3.zero(), EffekseerVec3.one());
    }

    public EffekseerVec3 position() {
        return position;
    }

    public EffekseerVec3 rotation() {
        return rotation;
    }

    public EffekseerVec3 scale() {
        return scale;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerTransform that)) {
            return false;
        }
        return position.equals(that.position)
                && rotation.equals(that.rotation)
                && scale.equals(that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, scale);
    }

    @Override
    public String toString() {
        return "EffekseerTransform[position=" + position
                + ", rotation=" + rotation
                + ", scale=" + scale
                + ']';
    }
}
