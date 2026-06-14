package com.behemiron.engine.external.effect.effekseer.backend;

import com.behemiron.engine.external.effect.effekseer.EffekseerFeature;
import com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerCommonNative;

import java.util.EnumSet;

/**
 * 提供与当前 Effekseer backend 状态相关的通用操作。
 */
public final class EffekseerBackend {

    /**
     * 工具类不允许实例化。
     */
    private EffekseerBackend() {
    }

    /**
     * 查询当前已经初始化的后端类型。
     *
     * @return 当前后端类型
     */
    public static EffekseerBackendType getBackendType() {
        return EffekseerBackendType.fromNative(BehemironEffekseerCommonNative.backendGetType());
    }

    /**
     * 获取当前 Native ABI 版本。
     *
     * @return ABI 版本号
     */
    public static int getAbiVersion() {
        return BehemironEffekseerCommonNative.getAbiVersion();
    }

    /**
     * 获取当前构建支持的功能集合。
     *
     * @return 构建功能集合
     */
    public static EnumSet<EffekseerFeature> getAvailableFeatures() {
        return toFeatureSet(BehemironEffekseerCommonNative.getFeatureFlags());
    }

    /**
     * 获取当前已经初始化 backend 的能力集合。
     *
     * @return backend 能力集合
     */
    public static EnumSet<EffekseerFeature> getBackendCapabilities() {
        return toFeatureSet(BehemironEffekseerCommonNative.backendGetCapabilities());
    }

    /**
     * 终止当前 backend，并释放 Native 层缓存。
     */
    public static void terminate() {
        BehemironEffekseerCommonNative.backendTerminate();
    }

    private static EnumSet<EffekseerFeature> toFeatureSet(int flags) {
        EnumSet<EffekseerFeature> result = EnumSet.noneOf(EffekseerFeature.class);
        for (EffekseerFeature feature : EffekseerFeature.values()) {
            if (feature.isEnabledIn(flags)) {
                result.add(feature);
            }
        }
        return result;
    }
}
