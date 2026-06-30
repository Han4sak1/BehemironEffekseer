package com.behemiron.engine.external.effect.effekseer.runtime;

import com.behemiron.engine.external.effect.effekseer.math.EffekseerTransform;

import java.util.Objects;

/**
 * 表示一次特效播放时的 typed 参数。
 */
public final class EffekseerPlayOptions {

    private final EffekseerTransform transform;
    private final int startFrame;

    public EffekseerPlayOptions(EffekseerTransform transform, int startFrame) {
        this.transform = Objects.requireNonNull(transform, "transform");
        this.startFrame = startFrame;
    }

    /**
     * 返回默认播放参数。
     *
     * @return 默认播放参数
     */
    public static EffekseerPlayOptions defaults() {
        return new EffekseerPlayOptions(EffekseerTransform.identity(), 0);
    }

    public EffekseerTransform transform() {
        return transform;
    }

    public int startFrame() {
        return startFrame;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerPlayOptions that)) {
            return false;
        }
        return startFrame == that.startFrame && transform.equals(that.transform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transform, startFrame);
    }

    @Override
    public String toString() {
        return "EffekseerPlayOptions[transform=" + transform + ", startFrame=" + startFrame + ']';
    }
}
