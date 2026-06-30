package com.behemiron.engine.external.effect.effekseer.collision;

import java.util.Objects;

/**
 * 表示一次世界碰撞查询的中立命中结果。
 */
public final class EffekseerCollisionHit {

    private final float positionX;
    private final float positionY;
    private final float positionZ;
    private final float normalX;
    private final float normalY;
    private final float normalZ;
    private final float distance;
    private final int userFlags;

    public EffekseerCollisionHit(
            float positionX,
            float positionY,
            float positionZ,
            float normalX,
            float normalY,
            float normalZ,
            float distance,
            int userFlags
    ) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
        this.distance = distance;
        this.userFlags = userFlags;
    }

    public float positionX() {
        return positionX;
    }

    public float positionY() {
        return positionY;
    }

    public float positionZ() {
        return positionZ;
    }

    public float normalX() {
        return normalX;
    }

    public float normalY() {
        return normalY;
    }

    public float normalZ() {
        return normalZ;
    }

    public float distance() {
        return distance;
    }

    public int userFlags() {
        return userFlags;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerCollisionHit that)) {
            return false;
        }
        return Float.compare(that.positionX, positionX) == 0
                && Float.compare(that.positionY, positionY) == 0
                && Float.compare(that.positionZ, positionZ) == 0
                && Float.compare(that.normalX, normalX) == 0
                && Float.compare(that.normalY, normalY) == 0
                && Float.compare(that.normalZ, normalZ) == 0
                && Float.compare(that.distance, distance) == 0
                && userFlags == that.userFlags;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionX, positionY, positionZ, normalX, normalY, normalZ, distance, userFlags);
    }

    @Override
    public String toString() {
        return "EffekseerCollisionHit[positionX=" + positionX
                + ", positionY=" + positionY
                + ", positionZ=" + positionZ
                + ", normalX=" + normalX
                + ", normalY=" + normalY
                + ", normalZ=" + normalZ
                + ", distance=" + distance
                + ", userFlags=" + userFlags
                + ']';
    }
}
