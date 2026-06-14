package com.behemiron.engine.external.effect.effekseer.collision;

/**
 * 表示一次世界碰撞查询的中立命中结果。
 *
 * @param positionX 命中点 X
 * @param positionY 命中点 Y
 * @param positionZ 命中点 Z
 * @param normalX   命中法线 X；当前仅供宿主侧记录或复用
 * @param normalY   命中法线 Y；当前仅供宿主侧记录或复用
 * @param normalZ   命中法线 Z；当前仅供宿主侧记录或复用
 * @param distance  命中距离；当前仅供宿主侧记录或复用
 * @param userFlags 由宿主自定义解释的命中标志；当前不会直接参与 Native 内部判定
 */
public record EffekseerCollisionHit(
        float positionX,
        float positionY,
        float positionZ,
        float normalX,
        float normalY,
        float normalZ,
        float distance,
        int userFlags
) {
}
