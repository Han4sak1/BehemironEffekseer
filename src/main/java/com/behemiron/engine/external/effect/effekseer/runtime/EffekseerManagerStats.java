package com.behemiron.engine.external.effect.effekseer.runtime;

import java.util.Objects;

/**
 * 表示 manager 当前统计信息。
 */
public final class EffekseerManagerStats {

    private final int totalInstanceCount;
    private final int restInstanceCount;
    private final int updateTime;
    private final int drawTime;
    private final int gpuTime;
    private final int drawCallCount;
    private final int drawVertexCount;

    public EffekseerManagerStats(
            int totalInstanceCount,
            int restInstanceCount,
            int updateTime,
            int drawTime,
            int gpuTime,
            int drawCallCount,
            int drawVertexCount
    ) {
        this.totalInstanceCount = totalInstanceCount;
        this.restInstanceCount = restInstanceCount;
        this.updateTime = updateTime;
        this.drawTime = drawTime;
        this.gpuTime = gpuTime;
        this.drawCallCount = drawCallCount;
        this.drawVertexCount = drawVertexCount;
    }

    public int totalInstanceCount() {
        return totalInstanceCount;
    }

    public int restInstanceCount() {
        return restInstanceCount;
    }

    public int updateTime() {
        return updateTime;
    }

    public int drawTime() {
        return drawTime;
    }

    public int gpuTime() {
        return gpuTime;
    }

    public int drawCallCount() {
        return drawCallCount;
    }

    public int drawVertexCount() {
        return drawVertexCount;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerManagerStats that)) {
            return false;
        }
        return totalInstanceCount == that.totalInstanceCount
                && restInstanceCount == that.restInstanceCount
                && updateTime == that.updateTime
                && drawTime == that.drawTime
                && gpuTime == that.gpuTime
                && drawCallCount == that.drawCallCount
                && drawVertexCount == that.drawVertexCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                totalInstanceCount,
                restInstanceCount,
                updateTime,
                drawTime,
                gpuTime,
                drawCallCount,
                drawVertexCount
        );
    }

    @Override
    public String toString() {
        return "EffekseerManagerStats[totalInstanceCount=" + totalInstanceCount
                + ", restInstanceCount=" + restInstanceCount
                + ", updateTime=" + updateTime
                + ", drawTime=" + drawTime
                + ", gpuTime=" + gpuTime
                + ", drawCallCount=" + drawCallCount
                + ", drawVertexCount=" + drawVertexCount
                + ']';
    }
}
