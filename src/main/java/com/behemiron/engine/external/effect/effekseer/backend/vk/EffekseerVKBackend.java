package com.behemiron.engine.external.effect.effekseer.backend.vk;

import com.behemiron.engine.external.effect.effekseer.internal.raw.vk.BehemironEffekseerVKNative;
import org.bytedeco.javacpp.IntPointer;

/**
 * 提供 Vulkan backend 的底层初始化入口。
 */
public final class EffekseerVKBackend {

    /**
     * 工具类不允许实例化。
     */
    private EffekseerVKBackend() {
    }

    /**
     * 初始化 Vulkan backend。
     *
     * @param physicalDevice  Vulkan 物理设备
     * @param device          Vulkan 逻辑设备
     * @param queue           Vulkan 图形队列
     * @param commandPool     Vulkan 命令池
     * @param swapBufferCount 交换链缓冲数量
     * @param renderPassInfo  渲染通道信息
     * @return 是否初始化成功
     */
    public static boolean initializeVK(
            long physicalDevice,
            long device,
            long queue,
            long commandPool,
            int swapBufferCount,
            VulkanRenderPassInfo renderPassInfo
    ) {
        if (renderPassInfo == null) {
            throw new IllegalArgumentException("renderPassInfo 不能为空");
        }
        int[] formats = renderPassInfo.renderTextureFormats();
        try (IntPointer renderTextureFormats = new IntPointer(8)) {
            for (int i = 0; i < formats.length; i++) {
                renderTextureFormats.put(i, formats[i]);
            }
            for (int i = formats.length; i < 8; i++) {
                renderTextureFormats.put(i, 0);
            }
            return BehemironEffekseerVKNative.backendInitializeVK(
                    physicalDevice,
                    device,
                    queue,
                    commandPool,
                    swapBufferCount,
                    renderPassInfo.doesPresentToScreen() ? 1 : 0,
                    renderTextureFormats,
                    renderPassInfo.renderTextureCount(),
                    renderPassInfo.depthFormat()
            ) != 0;
        }
    }
}
