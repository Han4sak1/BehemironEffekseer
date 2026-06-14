package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 通用 C ABI 的 JavaCPP 绑定声明。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public final class BehemironEffekseerCommonNative {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    /**
     * 工具类不允许实例化。
     */
    private BehemironEffekseerCommonNative() {
    }

    /**
     * 查询当前已经初始化的 backend 类型。
     *
     * @return Native 层 backend 枚举值
     */
    @Name("be_effekseer_backend_get_type")
    public static native @Cast("int32_t") int backendGetType();

    /**
     * 获取当前 Native ABI 版本。
     *
     * @return ABI 版本号
     */
    @Name("be_effekseer_get_abi_version")
    public static native @Cast("uint32_t") int getAbiVersion();

    /**
     * 获取当前构建支持的功能位。
     *
     * @return 功能位掩码
     */
    @Name("be_effekseer_get_feature_flags")
    public static native @Cast("uint32_t") int getFeatureFlags();

    /**
     * 获取当前 backend 的能力位。
     *
     * @return backend 能力位掩码
     */
    @Name("be_effekseer_backend_get_capabilities")
    public static native @Cast("uint32_t") int backendGetCapabilities();

    /**
     * 设置全局日志回调。
     *
     * @param callback 日志回调
     * @param userData 透传用户数据
     */
    @Name("be_effekseer_set_log_callback")
    public static native void setLogCallback(BehemironEffekseerLogCallbackNative callback, Pointer userData);

    /**
     * 获取最近一次错误消息。
     *
     * @return 最近一次错误消息
     */
    @Name("be_effekseer_get_last_error")
    public static native BytePointer getLastError();

    /**
     * 清空最近一次错误消息。
     */
    @Name("be_effekseer_clear_last_error")
    public static native void clearLastError();

    /**
     * 终止当前 backend，并释放 Native 层缓存。
     */
    @Name("be_effekseer_backend_terminate")
    public static native void backendTerminate();

    /**
     * 创建一个 Native manager。
     *
     * @return Native manager 指针
     */
    @Name("be_effekseer_manager_create")
    public static native Pointer managerCreate();

    /**
     * 销毁一个 Native manager。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_destroy")
    public static native void managerDestroy(Pointer handle);

    /**
     * 初始化 Native manager。
     *
     * @param handle         Native manager 指针
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 是否初始化成功
     */
    @Name("be_effekseer_manager_initialize")
    public static native @Cast("int32_t") int managerInitialize(
            Pointer handle,
            @Cast("int32_t") int spriteMaxCount,
            @Cast("int32_t") int srgbMode
    );

    /**
     * 开始一个 update 批次。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_begin_update")
    public static native void managerBeginUpdate(Pointer handle);

    /**
     * 结束一个 update 批次。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_end_update")
    public static native void managerEndUpdate(Pointer handle);

    /**
     * 推进 Native manager 时间。
     *
     * @param handle      Native manager 指针
     * @param deltaFrames 推进帧数
     */
    @Name("be_effekseer_manager_update")
    public static native void managerUpdate(Pointer handle, float deltaFrames);

    /**
     * 把指定实例推进到目标帧。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param frame        目标帧
     */
    @Name("be_effekseer_manager_update_handle_to_move_to_frame")
    public static native void managerUpdateHandleToMoveToFrame(Pointer handle, @Cast("int32_t") int effectHandle, float frame);

    /**
     * 播放一个 Native effect。
     *
     * @param handle Native manager 指针
     * @param effect Native effect 指针
     * @return Native 实例 handle
     */
    @Name("be_effekseer_manager_play")
    public static native @Cast("int32_t") int managerPlay(Pointer handle, Pointer effect);

    /**
     * 使用 typed options 播放一个 Native effect。
     *
     * @param handle  Native manager 指针
     * @param effect  Native effect 指针
     * @param options typed 播放参数
     * @return Native 实例 handle
     */
    @Name("be_effekseer_manager_play_with_options")
    public static native @Cast("int32_t") int managerPlayWithOptions(
            Pointer handle,
            Pointer effect,
            BehemironEffekseerPlayOptionsNative options
    );

    /**
     * 停止当前 manager 上的全部实例。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_stop_all")
    public static native void managerStopAll(Pointer handle);

    /**
     * 停止指定实例。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     */
    @Name("be_effekseer_manager_stop")
    public static native void managerStop(Pointer handle, @Cast("int32_t") int effectHandle);

    /**
     * 设置实例暂停状态。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param paused       是否暂停
     */
    @Name("be_effekseer_manager_set_paused")
    public static native void managerSetPaused(Pointer handle, @Cast("int32_t") int effectHandle, @Cast("int32_t") int paused);

    /**
     * 设置实例显示状态。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param shown        是否显示
     */
    @Name("be_effekseer_manager_set_shown")
    public static native void managerSetShown(Pointer handle, @Cast("int32_t") int effectHandle, @Cast("int32_t") int shown);

    /**
     * 向实例发送 trigger。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param index        trigger 索引
     */
    @Name("be_effekseer_manager_send_trigger")
    public static native void managerSendTrigger(Pointer handle, @Cast("int32_t") int effectHandle, @Cast("int32_t") int index);

    /**
     * 设置实例位置。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param x            X 坐标
     * @param y            Y 坐标
     * @param z            Z 坐标
     */
    @Name("be_effekseer_manager_set_effect_position")
    public static native void managerSetEffectPosition(Pointer handle, @Cast("int32_t") int effectHandle, float x, float y, float z);

    /**
     * 设置实例旋转。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param x            X 轴旋转
     * @param y            Y 轴旋转
     * @param z            Z 轴旋转
     */
    @Name("be_effekseer_manager_set_effect_rotation")
    public static native void managerSetEffectRotation(Pointer handle, @Cast("int32_t") int effectHandle, float x, float y, float z);

    /**
     * 设置实例缩放。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param x            X 轴缩放
     * @param y            Y 轴缩放
     * @param z            Z 轴缩放
     */
    @Name("be_effekseer_manager_set_effect_scale")
    public static native void managerSetEffectScale(Pointer handle, @Cast("int32_t") int effectHandle, float x, float y, float z);

    /**
     * 设置指定 layer 的观察参数。
     *
     * @param handle       Native manager 指针
     * @param layer        layer 索引
     * @param viewerPosX   观察者 X 坐标
     * @param viewerPosY   观察者 Y 坐标
     * @param viewerPosZ   观察者 Z 坐标
     * @param distanceBias 距离偏移
     */
    @Name("be_effekseer_manager_set_layer_parameter")
    public static native void managerSetLayerParameter(
            Pointer handle,
            @Cast("int32_t") int layer,
            float viewerPosX,
            float viewerPosY,
            float viewerPosZ,
            float distanceBias
    );

    /**
     * 设置实例 3x4 变换矩阵。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     */
    @Name("be_effekseer_manager_set_effect_transform_matrix")
    public static native void managerSetEffectTransformMatrix(
            Pointer handle,
            @Cast("int32_t") int effectHandle,
            float v0, float v1, float v2, float v3,
            float v4, float v5, float v6, float v7,
            float v8, float v9, float v10, float v11
    );

    /**
     * 设置实例基础 3x4 变换矩阵。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     */
    @Name("be_effekseer_manager_set_effect_transform_base_matrix")
    public static native void managerSetEffectTransformBaseMatrix(
            Pointer handle,
            @Cast("int32_t") int effectHandle,
            float v0, float v1, float v2, float v3,
            float v4, float v5, float v6, float v7,
            float v8, float v9, float v10, float v11
    );

    /**
     * 绘制后景层。
     *
     * @param handle Native manager 指针
     * @param layer  layer 索引
     */
    @Name("be_effekseer_manager_draw_back")
    public static native void managerDrawBack(Pointer handle, @Cast("int32_t") int layer);

    /**
     * 绘制前景层。
     *
     * @param handle Native manager 指针
     * @param layer  layer 索引
     */
    @Name("be_effekseer_manager_draw_front")
    public static native void managerDrawFront(Pointer handle, @Cast("int32_t") int layer);

    /**
     * 设置实例所属 layer。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param layer        layer 索引
     */
    @Name("be_effekseer_manager_set_layer")
    public static native void managerSetLayer(Pointer handle, @Cast("int32_t") int effectHandle, @Cast("int32_t") int layer);

    /**
     * 设置相机朝向和位置。
     *
     * @param handle Native manager 指针
     * @param frontX 前向向量 X
     * @param frontY 前向向量 Y
     * @param frontZ 前向向量 Z
     * @param posX   相机位置 X
     * @param posY   相机位置 Y
     * @param posZ   相机位置 Z
     */
    @Name("be_effekseer_manager_set_camera_parameter")
    public static native void managerSetCameraParameter(
            Pointer handle,
            float frontX,
            float frontY,
            float frontZ,
            float posX,
            float posY,
            float posZ
    );

    /**
     * 设置 4x4 投影矩阵。
     *
     * @param handle   Native manager 指针
     * @param values16 16 个矩阵元素
     */
    @Name("be_effekseer_manager_set_projection_matrix")
    public static native void managerSetProjectionMatrix(Pointer handle, FloatPointer values16);

    /**
     * 设置 4x4 相机矩阵。
     *
     * @param handle   Native manager 指针
     * @param values16 16 个矩阵元素
     */
    @Name("be_effekseer_manager_set_camera_matrix")
    public static native void managerSetCameraMatrix(Pointer handle, FloatPointer values16);

    /**
     * 查询实例是否仍然存在。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @return 是否存在
     */
    @Name("be_effekseer_manager_exists")
    public static native @Cast("int32_t") int managerExists(Pointer handle, @Cast("int32_t") int effectHandle);

    /**
     * 根据窗口尺寸设置简化的 view-projection 矩阵。
     *
     * @param handle       Native manager 指针
     * @param windowWidth  窗口宽度
     * @param windowHeight 窗口高度
     */
    @Name("be_effekseer_manager_set_view_projection_matrix_with_simple_window")
    public static native void managerSetViewProjectionMatrixWithSimpleWindow(
            Pointer handle,
            @Cast("int32_t") int windowWidth,
            @Cast("int32_t") int windowHeight
    );

    /**
     * 设置实例动态输入值。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param index        动态输入索引
     * @param value        动态输入值
     */
    @Name("be_effekseer_manager_set_dynamic_input")
    public static native void managerSetDynamicInput(
            Pointer handle,
            @Cast("int32_t") int effectHandle,
            @Cast("int32_t") int index,
            float value
    );

    /**
     * 获取实例动态输入值。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @param index        动态输入索引
     * @return 当前动态输入值
     */
    @Name("be_effekseer_manager_get_dynamic_input")
    public static native float managerGetDynamicInput(
            Pointer handle,
            @Cast("int32_t") int effectHandle,
            @Cast("int32_t") int index
    );

    /**
     * 批量播放特效。
     *
     * @param handle     Native manager 指针
     * @param items      批量播放项
     * @param itemCount  项数量
     * @param outHandles 输出实例 handle
     * @return 成功播放数量
     */
    @Name("be_effekseer_manager_batch_play")
    public static native @Cast("int32_t") int managerBatchPlay(
            Pointer handle,
            BehemironEffekseerBatchPlayItemNative items,
            @Cast("int32_t") int itemCount,
            IntPointer outHandles
    );

    /**
     * 批量设置实例 transform。
     *
     * @param handle    Native manager 指针
     * @param items     批量 transform 项
     * @param itemCount 项数量
     */
    @Name("be_effekseer_manager_batch_set_transform")
    public static native void managerBatchSetTransform(
            Pointer handle,
            BehemironEffekseerBatchTransformItemNative items,
            @Cast("int32_t") int itemCount
    );

    /**
     * 批量设置实例 dynamic input。
     *
     * @param handle    Native manager 指针
     * @param items     批量 dynamic input 项
     * @param itemCount 项数量
     */
    @Name("be_effekseer_manager_batch_set_dynamic_input")
    public static native void managerBatchSetDynamicInput(
            Pointer handle,
            BehemironEffekseerBatchDynamicInputItemNative items,
            @Cast("int32_t") int itemCount
    );

    /**
     * 设置世界碰撞回调。
     *
     * @param handle   Native manager 指针
     * @param callback 世界碰撞回调
     * @param userData 透传给回调的用户数据
     */
    @Name("be_effekseer_manager_set_collision_callback")
    public static native void managerSetCollisionCallback(
            Pointer handle,
            BehemironEffekseerCollisionCallbackNative callback,
            Pointer userData
    );

    /**
     * 启动 worker 线程。
     *
     * @param handle      Native manager 指针
     * @param workerCount 线程数量
     */
    @Name("be_effekseer_manager_launch_worker_threads")
    public static native void managerLaunchWorkerThreads(Pointer handle, @Cast("int32_t") int workerCount);

    /**
     * 清除当前背景资源绑定。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_unset_background")
    public static native void managerUnsetBackground(Pointer handle);

    /**
     * 清除当前深度资源绑定。
     *
     * @param handle Native manager 指针
     */
    @Name("be_effekseer_manager_unset_depth")
    public static native void managerUnsetDepth(Pointer handle);

    /**
     * 获取指定实例的粒子数量。
     *
     * @param handle       Native manager 指针
     * @param effectHandle Native 实例 handle
     * @return 当前粒子数量
     */
    @Name("be_effekseer_manager_get_instance_count")
    public static native @Cast("int32_t") int managerGetInstanceCount(Pointer handle, @Cast("int32_t") int effectHandle);

    /**
     * 获取当前 manager 中全部实例的总粒子数量。
     *
     * @param handle Native manager 指针
     * @return 总粒子数量
     */
    @Name("be_effekseer_manager_get_total_instance_count")
    public static native @Cast("int32_t") int managerGetTotalInstanceCount(Pointer handle);

    /**
     * 获取 manager 当前统计。
     *
     * @param handle   Native manager 指针
     * @param outStats 输出统计结构
     * @return 是否成功写出
     */
    @Name("be_effekseer_manager_get_stats")
    public static native @Cast("int32_t") int managerGetStats(
            Pointer handle,
            BehemironEffekseerManagerStatsNative outStats
    );
}
