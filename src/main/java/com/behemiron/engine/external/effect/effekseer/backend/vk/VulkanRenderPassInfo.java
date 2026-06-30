package com.behemiron.engine.external.effect.effekseer.backend.vk;

import java.util.Arrays;
import java.util.Objects;

/**
 * 描述初始化 Vulkan backend 时所需的渲染通道信息。
 */
public final class VulkanRenderPassInfo {

    private final boolean doesPresentToScreen;
    private final int[] renderTextureFormats;
    private final int depthFormat;

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
    public boolean doesPresentToScreen() {
        return doesPresentToScreen;
    }

    /**
     * 返回颜色附件格式数组的副本。
     *
     * @return 颜色附件格式数组副本
     */
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
    public int depthFormat() {
        return depthFormat;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof VulkanRenderPassInfo that)) {
            return false;
        }
        return doesPresentToScreen == that.doesPresentToScreen
                && depthFormat == that.depthFormat
                && Arrays.equals(renderTextureFormats, that.renderTextureFormats);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(doesPresentToScreen, depthFormat);
        result = 31 * result + Arrays.hashCode(renderTextureFormats);
        return result;
    }

    @Override
    public String toString() {
        return "VulkanRenderPassInfo[doesPresentToScreen=" + doesPresentToScreen
                + ", renderTextureFormats=" + Arrays.toString(renderTextureFormats)
                + ", depthFormat=" + depthFormat
                + ']';
    }
}
