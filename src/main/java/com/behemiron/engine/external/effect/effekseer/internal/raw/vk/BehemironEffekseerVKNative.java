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
     * 开始把外部 Vulkan 命令缓冲绑定给 Effekseer。
     *
     * @param handle              Native manager 指针
     * @param nativeCommandBuffer Vulkan 命令缓冲句柄
     */
    @Name("be_effekseer_manager_begin_command_list_vk")
    public static native void managerBeginCommandListVK(Pointer handle, @Cast("uint64_t") long nativeCommandBuffer);

    /**
     * 结束当前 Vulkan 命令缓冲绑定。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_end_command_list_vk")
    public static native void managerEndCommandListVK(Pointer handle);

    /**
     * 绑定 Vulkan 背景图像。
     *
     * @param handle Native manager 指针
     * @param image  Vulkan 图像句柄
     * @param aspect 图像 aspect mask
     * @param format 图像格式
     */
    @Name("be_effekseer_manager_set_background_vk")
    public static native void managerSetBackgroundVK(
            Pointer handle,
            @Cast("uint64_t") long image,
            @Cast("uint32_t") int aspect,
            @Cast("uint32_t") int format
    );

    /**
     * 绑定 Vulkan 深度图像。
     *
     * @param handle Native manager 指针
     * @param image  Vulkan 图像句柄
     * @param aspect 图像 aspect mask
     * @param format 图像格式
     */
    @Name("be_effekseer_manager_set_depth_vk")
    public static native void managerSetDepthVK(
            Pointer handle,
            @Cast("uint64_t") long image,
            @Cast("uint32_t") int aspect,
            @Cast("uint32_t") int format
    );
}
