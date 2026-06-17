package com.behemiron.engine.external.effect.effekseer.backend.vk;

import com.behemiron.engine.external.effect.effekseer.internal.raw.vk.BehemironEffekseerVKNative;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerManager;

/**
 * 在通用 manager 基础上补充 Vulkan 专属帧提交能力。
 */
public final class EffekseerVKManager extends EffekseerManager {

    /**
     * 在当前 Vulkan 命令缓冲上提交完整 Effekseer 帧。
     *
     * @param nativeCommandBuffer Vulkan 命令缓冲句柄
     * @param backgroundImage     背景图像句柄，0 表示无
     * @param backgroundAspect    背景图像 aspect mask
     * @param backgroundFormat    背景图像格式
     * @param depthImage          深度图像句柄，0 表示无
     * @param depthAspect         深度图像 aspect mask
     * @param depthFormat         深度图像格式
     * @param layer               layer 索引
     * @param drawBack            是否绘制后景
     * @param drawFront           是否绘制前景
     */
    public void renderVK(
            long nativeCommandBuffer,
            long backgroundImage,
            int backgroundAspect,
            int backgroundFormat,
            long depthImage,
            int depthAspect,
            int depthFormat,
            int layer,
            boolean drawBack,
            boolean drawFront
    ) {
        BehemironEffekseerVKNative.managerRenderVK(
                handle(),
                nativeCommandBuffer,
                backgroundImage,
                backgroundAspect,
                backgroundFormat,
                depthImage,
                depthAspect,
                depthFormat,
                layer,
                drawBack ? 1 : 0,
                drawFront ? 1 : 0
        );
    }
}
