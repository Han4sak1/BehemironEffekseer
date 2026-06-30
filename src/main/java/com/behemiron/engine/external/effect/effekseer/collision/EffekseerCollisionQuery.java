package com.behemiron.engine.external.effect.effekseer.collision;

import java.util.Objects;

/**
 * 表示一次世界碰撞查询的射线参数。
 */
public final class EffekseerCollisionQuery {

    private final float startX;
    private final float startY;
    private final float startZ;
    private final float endX;
    private final float endY;
    private final float endZ;

    public EffekseerCollisionQuery(float startX, float startY, float startZ, float endX, float endY, float endZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
        this.endX = endX;
        this.endY = endY;
        this.endZ = endZ;
    }

    public float startX() {
        return startX;
    }

    public float startY() {
        return startY;
    }

    public float startZ() {
        return startZ;
    }

    public float endX() {
        return endX;
    }

    public float endY() {
        return endY;
    }

    public float endZ() {
        return endZ;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerCollisionQuery that)) {
            return false;
        }
        return Float.compare(that.startX, startX) == 0
                && Float.compare(that.startY, startY) == 0
                && Float.compare(that.startZ, startZ) == 0
                && Float.compare(that.endX, endX) == 0
                && Float.compare(that.endY, endY) == 0
                && Float.compare(that.endZ, endZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, startZ, endX, endY, endZ);
    }

    @Override
    public String toString() {
        return "EffekseerCollisionQuery[startX=" + startX
                + ", startY=" + startY
                + ", startZ=" + startZ
                + ", endX=" + endX
                + ", endY=" + endY
                + ", endZ=" + endZ
                + ']';
    }
}
