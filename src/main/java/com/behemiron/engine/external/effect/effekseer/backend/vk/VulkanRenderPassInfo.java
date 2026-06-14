package com.behemiron.engine.external.effect.effekseer.backend.vk;

import java.util.Arrays;
import java.util.Objects;

/**
 * 描述初始化 Vulkan backend 时所需的渲染通道信息。
 */
public record VulkanRenderPassInfo(boolean doesPresentToScreen, int[] renderTextureFormats, int depthFormat) {

    /**
     * 创建 Vulkan 渲染通道信息。
     *
     * @param doesPresentToScreen  当前 render pass 是否直接输出到屏幕
     * @param renderTextureFormats 颜色附件格式数组，长度必须在 1 到 8 之间
     * @param depthFormat          深度附件格式
     */
    public VulkanRenderPassInfo(boolean doesPresentToScreen, int[] renderTextureFormats, int depthFormat) {
        Objects.requireNonNull(renderTextureFormats, "renderTextureFormats");
        if (renderTextureFormats.length == 0 || renderTextureFormats.length > 8) {
            throw new IllegalArgumentException("renderTextureFormats 数量必须在 1 到 8 之间");
        }
        this.doesPresentToScreen = doesPresentToScreen;
        this.renderTextureFormats = Arrays.copyOf(renderTextureFormats, renderTextureFormats.length);
        this.depthFormat = depthFormat;
    }

    /**
     * 返回当前 render pass 是否直接输出到屏幕。
     *
     * @return 是否直接输出到屏幕
     */
    @Override
    public boolean doesPresentToScreen() {
        return doesPresentToScreen;
    }

    /**
     * 返回颜色附件格式数组的副本。
     *
     * @return 颜色附件格式数组副本
     */
    @Override
    public int[] renderTextureFormats() {
        return Arrays.copyOf(renderTextureFormats, renderTextureFormats.length);
    }

    /**
     * 返回颜色附件数量。
     *
     * @return 颜色附件数量
     */
    public int renderTextureCount() {
        return renderTextureFormats.length;
    }

    /**
     * 返回深度附件格式。
     *
     * @return 深度附件格式
     */
    @Override
    public int depthFormat() {
        return depthFormat;
    }
}
