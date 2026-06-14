package com.behemiron.engine.external.effect.effekseer.backend.vk;

import com.behemiron.engine.external.effect.effekseer.internal.raw.vk.BehemironEffekseerVKNative;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerManager;

/**
 * 在通用 manager 基础上补充 Vulkan 专属命令缓冲和图像绑定能力。
 */
public final class EffekseerVKManager extends EffekseerManager {

    /**
     * 开始把外部 Vulkan 命令缓冲绑定给 Effekseer。
     *
     * @param nativeCommandBuffer Vulkan 命令缓冲句柄
     */
    public void beginCommandListVK(long nativeCommandBuffer) {
        BehemironEffekseerVKNative.managerBeginCommandListVK(handle(), nativeCommandBuffer);
    }

    /**
     * 结束当前 Vulkan 命令缓冲绑定。
     */
    public void endCommandListVK() {
        BehemironEffekseerVKNative.managerEndCommandListVK(handle());
    }

    /**
     * 绑定 Vulkan 背景图像。
     *
     * @param image  Vulkan 图像句柄
     * @param aspect 图像 aspect mask
     * @param format 图像格式
     */
    public void setBackgroundVK(long image, int aspect, int format) {
        BehemironEffekseerVKNative.managerSetBackgroundVK(handle(), image, aspect, format);
    }

    /**
     * 绑定 Vulkan 深度图像。
     *
     * @param image  Vulkan 图像句柄
     * @param aspect 图像 aspect mask
     * @param format 图像格式
     */
    public void setDepthVK(long image, int aspect, int format) {
        BehemironEffekseerVKNative.managerSetDepthVK(handle(), image, aspect, format);
    }
}
