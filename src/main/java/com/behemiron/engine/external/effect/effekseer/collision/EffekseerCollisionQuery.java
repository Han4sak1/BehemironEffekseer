package com.behemiron.engine.external.effect.effekseer.collision;

/**
 * 表示一次世界碰撞查询的射线参数。
 *
 * @param startX 起点 X
 * @param startY 起点 Y
 * @param startZ 起点 Z
 * @param endX   终点 X
 * @param endY   终点 Y
 * @param endZ   终点 Z
 */
public record EffekseerCollisionQuery(
        float startX,
        float startY,
        float startZ,
        float endX,
        float endY,
        float endZ
) {
}
