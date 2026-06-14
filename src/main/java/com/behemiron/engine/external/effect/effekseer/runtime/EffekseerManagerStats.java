package com.behemiron.engine.external.effect.effekseer.runtime;

/**
 * 表示 manager 当前统计信息。
 *
 * @param totalInstanceCount 当前总粒子数量
 * @param restInstanceCount 当前剩余实例容量
 * @param updateTime Update CPU 时间
 * @param drawTime Draw CPU 时间
 * @param gpuTime GPU 时间
 * @param drawCallCount Draw Call 数量
 * @param drawVertexCount 绘制顶点数量
 */
public record EffekseerManagerStats(
    int totalInstanceCount,
    int restInstanceCount,
    int updateTime,
    int drawTime,
    int gpuTime,
    int drawCallCount,
    int drawVertexCount
) {
}
