package com.behemiron.engine.external.effect.effekseer.internal.raw.vk;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * Vulkan C ABI 的 JavaCPP 绑定声明。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public final class BehemironEffekseerVKNative {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    /**
     * 工具类不允许实例化。
     */
    private BehemironEffekseerVKNative() {
    }

    /**
     * 初始化 Vulkan backend。
     *
     * @param physicalDevice       Vulkan 物理设备
     * @param device               Vulkan 逻辑设备
     * @param queue                Vulkan 图形队列
     * @param commandPool          Vulkan 命令池
     * @param swapBufferCount      交换链缓冲数量
     * @param doesPresentToScreen  当前 render pass 是否直接输出到屏幕
     * @param renderTextureFormats 颜色附件格式数组
     * @param renderTextureCount   颜色附件数量
     * @param depthFormat          深度附件格式
     * @return 是否初始化成功
     */
    @Name("be_effekseer_backend_initialize_vk")
    public static native @Cast("int32_t") int backendInitializeVK(
            @Cast("uint64_t") long physicalDevice,
            @Cast("uint64_t") long device,
            @Cast("uint64_t") long queue,
            @Cast("uint64_t") long commandPool,
            @Cast("int32_t") int swapBufferCount,
            @Cast("int32_t") int doesPresentToScreen,
            @Cast("uint32_t*") IntPointer renderTextureFormats,
            @Cast("int32_t") int renderTextureCount,
            @Cast("uint32_t") int depthFormat
    );

    /**
     * 在当前 Vulkan 命令缓冲上提交完整 Effekseer 帧。
     *
     * @param handle              Native manager 指针
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
    @Name("be_effekseer_manager_render_vk")
    public static native void managerRenderVK(
            Pointer handle,
            @Cast("uint64_t") long nativeCommandBuffer,
            @Cast("uint64_t") long backgroundImage,
            @Cast("uint32_t") int backgroundAspect,
            @Cast("uint32_t") int backgroundFormat,
            @Cast("uint64_t") long depthImage,
            @Cast("uint32_t") int depthAspect,
            @Cast("uint32_t") int depthFormat,
            @Cast("int32_t") int layer,
            @Cast("int32_t") int drawBack,
            @Cast("int32_t") int drawFront
    );
}
