package com.behemiron.engine.external.effect.effekseer;

import com.behemiron.engine.external.effect.effekseer.backend.vk.EffekseerVKBackend;
import com.behemiron.engine.external.effect.effekseer.backend.vk.EffekseerVKManager;
import com.behemiron.engine.external.effect.effekseer.backend.vk.VulkanRenderPassInfo;

/**
 * 提供 Vulkan backend 的便捷门面入口。
 */
public final class EffekseerVK {

    /**
     * 工具类不允许实例化。
     */
    private EffekseerVK() {
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
    public static boolean initialize(
            long physicalDevice,
            long device,
            long queue,
            long commandPool,
            int swapBufferCount,
            VulkanRenderPassInfo renderPassInfo
    ) {
        return EffekseerVKBackend.initializeVK(
                physicalDevice,
                device,
                queue,
                commandPool,
                swapBufferCount,
                renderPassInfo
        );
    }

    /**
     * 创建一个 Vulkan manager。
     *
     * @return 新创建的 Vulkan manager
     */
    public static EffekseerVKManager createManager() {
        return new EffekseerVKManager();
    }

    /**
     * 创建并初始化一个 Vulkan manager。
     *
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 已初始化的 Vulkan manager
     */
    public static EffekseerVKManager createInitializedManager(int spriteMaxCount, boolean srgbMode) {
        EffekseerVKManager manager = createManager();
        if (!manager.initialize(spriteMaxCount, srgbMode)) {
            manager.close();
            throw new IllegalStateException("初始化 Vulkan Effekseer manager 失败");
        }
        return manager;
    }

    /**
     * 先初始化 Vulkan backend，再创建并初始化一个 Vulkan manager。
     *
     * @param physicalDevice  Vulkan 物理设备
     * @param device          Vulkan 逻辑设备
     * @param queue           Vulkan 图形队列
     * @param commandPool     Vulkan 命令池
     * @param swapBufferCount 交换链缓冲数量
     * @param renderPassInfo  渲染通道信息
     * @param spriteMaxCount  粒子最大数量
     * @param srgbMode        是否启用 sRGB 模式
     * @return 已初始化的 Vulkan manager
     */
    public static EffekseerVKManager initializeAndCreateManager(
            long physicalDevice,
            long device,
            long queue,
            long commandPool,
            int swapBufferCount,
            VulkanRenderPassInfo renderPassInfo,
            int spriteMaxCount,
            boolean srgbMode
    ) {
        if (!initialize(physicalDevice, device, queue, commandPool, swapBufferCount, renderPassInfo)) {
            throw new IllegalStateException("初始化 Vulkan Effekseer backend 失败");
        }
        return createInitializedManager(spriteMaxCount, srgbMode);
    }
}
