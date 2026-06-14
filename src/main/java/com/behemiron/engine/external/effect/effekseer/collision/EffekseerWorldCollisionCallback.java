package com.behemiron.engine.external.effect.effekseer.collision;

import com.behemiron.engine.external.effect.effekseer.collision.EffekseerCollisionHit;
import com.behemiron.engine.external.effect.effekseer.collision.EffekseerCollisionQuery;

/**
 * 为 Effekseer 提供世界碰撞查询能力。
 */
@FunctionalInterface
public interface EffekseerWorldCollisionCallback {

    /**
     * 处理一次从特效系统发起的碰撞查询。
     *
     * @param query 中立碰撞查询参数
     * @return 命中结果；返回 {@code null} 表示未命中。当前 Native 侧实际会消费命中点坐标，
     * 其余字段保留给宿主侧使用。
     */
    EffekseerCollisionHit collide(EffekseerCollisionQuery query);
}
