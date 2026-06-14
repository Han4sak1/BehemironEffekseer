package com.behemiron.engine.external.effect.effekseer;

import com.behemiron.engine.external.effect.effekseer.backend.EffekseerBackendType;
import com.behemiron.engine.external.effect.effekseer.backend.EffekseerBackend;
import com.behemiron.engine.external.effect.effekseer.data.EffekseerEffect;
import com.behemiron.engine.external.effect.effekseer.logging.EffekseerLogCallback;
import com.behemiron.engine.external.effect.effekseer.logging.EffekseerLogLevel;
import com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerCommonNative;
import com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerLogCallbackNative;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerManager;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;

import java.util.EnumSet;

/**
 * 提供 Effekseer 的通用门面入口，用于查询 backend 状态以及创建常用对象。
 */
public final class Effekseer {

    private static EffekseerLogCallback logCallback;

    /**
     * 工具类不允许实例化。
     */
    private Effekseer() {
    }

    /**
     * 获取当前已经初始化的后端类型。
     *
     * @return 当前后端类型
     */
    public static EffekseerBackendType getBackendType() {
        return EffekseerBackend.getBackendType();
    }

    /**
     * 获取当前 Native ABI 版本。
     *
     * @return ABI 版本号
     */
    public static int getAbiVersion() {
        return EffekseerBackend.getAbiVersion();
    }

    /**
     * 获取当前构建支持的功能集合。
     *
     * @return 构建功能集合
     */
    public static EnumSet<EffekseerFeature> getAvailableFeatures() {
        return EffekseerBackend.getAvailableFeatures();
    }

    /**
     * 获取当前 backend 的能力集合。
     *
     * @return backend 能力集合
     */
    public static EnumSet<EffekseerFeature> getBackendCapabilities() {
        return EffekseerBackend.getBackendCapabilities();
    }

    /**
     * 终止当前 backend，并释放 Native 层缓存。
     */
    public static void terminate() {
        EffekseerBackend.terminate();
    }

    /**
     * 设置全局 Native 日志回调。
     *
     * @param callback 日志回调；传入 {@code null} 表示清除
     */
    public static void setLogCallback(EffekseerLogCallback callback) {
        logCallback = callback;
        BehemironEffekseerLogCallbackNative nativeLogCallback;
        if (callback == null) {
            BehemironEffekseerCommonNative.setLogCallback(null, null);
            return;
        }

        nativeLogCallback = new BehemironEffekseerLogCallbackNative() {
            @Override
            public void call(int logType, BytePointer message, Pointer userData) {
                logCallback.onLog(
                        EffekseerLogLevel.fromNative(logType),
                        message == null || message.isNull() ? "" : message.getString()
                );
            }
        };
        BehemironEffekseerCommonNative.setLogCallback(nativeLogCallback, null);
    }

    /**
     * 获取最近一次错误消息。
     *
     * @return 最近一次错误消息；若当前没有错误则返回 {@code null}
     */
    public static String getLastError() {
        BytePointer pointer = BehemironEffekseerCommonNative.getLastError();
        return pointer == null || pointer.isNull() ? null : pointer.getString();
    }

    /**
     * 清空最近一次错误消息。
     */
    public static void clearLastError() {
        BehemironEffekseerCommonNative.clearLastError();
    }

    /**
     * 创建一个空的特效对象。
     *
     * @return 新创建的特效对象
     */
    public static EffekseerEffect createEffect() {
        return new EffekseerEffect();
    }

    /**
     * 创建并立即加载一个特效对象。
     *
     * @param data          特效二进制内容
     * @param magnification 播放倍率
     * @return 已加载的特效对象
     */
    public static EffekseerEffect loadEffect(byte[] data, float magnification) {
        EffekseerEffect effect = createEffect();
        if (!effect.load(data, magnification)) {
            effect.close();
            throw new IllegalStateException("加载 Effekseer effect 失败");
        }
        return effect;
    }

    /**
     * 创建一个通用 manager。
     *
     * @return 新创建的 manager
     */
    public static EffekseerManager createManager() {
        return new EffekseerManager();
    }

    /**
     * 创建并初始化一个通用 manager。
     *
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 已初始化的 manager
     */
    public static EffekseerManager createInitializedManager(int spriteMaxCount, boolean srgbMode) {
        EffekseerManager manager = createManager();
        if (!manager.initialize(spriteMaxCount, srgbMode)) {
            manager.close();
            throw new IllegalStateException("初始化 Effekseer manager 失败");
        }
        return manager;
    }
}
