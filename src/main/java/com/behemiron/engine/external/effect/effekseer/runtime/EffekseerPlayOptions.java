package com.behemiron.engine.external.effect.effekseer.runtime;

import com.behemiron.engine.external.effect.effekseer.math.EffekseerTransform;

import java.util.Objects;

/**
 * 表示一次特效播放时的 typed 参数。
 *
 * @param transform  初始变换
 * @param startFrame 起播帧
 */
public record EffekseerPlayOptions(
        EffekseerTransform transform,
        int startFrame
) {
    /**
     * 返回默认播放参数。
     *
     * @return 默认播放参数
     */
    public static EffekseerPlayOptions defaults() {
        return new EffekseerPlayOptions(EffekseerTransform.identity(), 0);
    }

    /**
     * 构造时校验字段非空。
     */
    public EffekseerPlayOptions {
        Objects.requireNonNull(transform, "transform");
    }
}
