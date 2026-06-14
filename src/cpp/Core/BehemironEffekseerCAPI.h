#pragma once

#include "BehemironEffekseerBatchTypes.h"
#include "BehemironEffekseerCollisionTypes.h"
#include <stdint.h>

#if defined(_WIN32) || defined(__CYGWIN__)
#  if defined(BEHEMIRON_EFFEKSEER_BUILD_SHARED)
#    define BEHEMIRON_EFFEKSEER_API __declspec(dllexport)
#  elif !defined(BEHEMIRON_EFFEKSEER_STATIC)
#    define BEHEMIRON_EFFEKSEER_API __declspec(dllimport)
#  else
#    define BEHEMIRON_EFFEKSEER_API
#  endif
#elif defined(__GNUC__) && __GNUC__ >= 4
#  define BEHEMIRON_EFFEKSEER_API __attribute__((visibility("default")))
#else
#  define BEHEMIRON_EFFEKSEER_API
#endif

#ifdef __cplusplus
extern "C" {
#endif

// region 句柄定义

typedef void* BehemironEffekseerManagerHandle;
typedef void* BehemironEffekseerEffectHandle;

// endregion

// region 枚举定义

typedef enum BehemironEffekseerBackendType {
    BEHEMIRON_EFFEKSEER_BACKEND_UNKNOWN = 0,
    BEHEMIRON_EFFEKSEER_BACKEND_OPENGL = 1,
    BEHEMIRON_EFFEKSEER_BACKEND_VULKAN = 4,
} BehemironEffekseerBackendType;

typedef enum BehemironEffekseerFeatureFlags {
    BEHEMIRON_EFFEKSEER_FEATURE_COLLISION_CALLBACK = 1,
    BEHEMIRON_EFFEKSEER_FEATURE_VULKAN = 1 << 1,
    BEHEMIRON_EFFEKSEER_FEATURE_OPENGL = 1 << 2,
    BEHEMIRON_EFFEKSEER_FEATURE_EFFECT_DEPENDENCY_VIEW = 1 << 3,
    BEHEMIRON_EFFEKSEER_FEATURE_TYPED_PLAY_OPTIONS = 1 << 4,
    BEHEMIRON_EFFEKSEER_FEATURE_BATCH_API = 1 << 5,
    BEHEMIRON_EFFEKSEER_FEATURE_DIAGNOSTICS = 1 << 6,
} BehemironEffekseerFeatureFlags;

typedef enum BehemironEffekseerTextureType {
    BEHEMIRON_EFFEKSEER_TEXTURE_COLOR = 0,
    BEHEMIRON_EFFEKSEER_TEXTURE_NORMAL = 1,
    BEHEMIRON_EFFEKSEER_TEXTURE_DISTORTION = 2,
} BehemironEffekseerTextureType;

// endregion

// region Backend 接口

// 初始化 OpenGL 后端。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_backend_initialize_gl(void);
// 初始化 Vulkan 后端，并写入当前渲染通道配置。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_backend_initialize_vk(
    uint64_t physical_device,
    uint64_t device,
    uint64_t queue,
    uint64_t command_pool,
    int32_t swap_buffer_count,
    int32_t does_present_to_screen,
    const uint32_t* render_texture_formats,
    int32_t render_texture_count,
    uint32_t depth_format
);
// 获取当前 ABI 版本号。
BEHEMIRON_EFFEKSEER_API uint32_t be_effekseer_get_abi_version(void);
// 获取当前构建支持的功能位。
BEHEMIRON_EFFEKSEER_API uint32_t be_effekseer_get_feature_flags(void);
// 获取当前已经初始化的后端类型。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_backend_get_type(void);
// 获取当前 backend 的可用能力位。
BEHEMIRON_EFFEKSEER_API uint32_t be_effekseer_backend_get_capabilities(void);
// 设置全局日志回调。
BEHEMIRON_EFFEKSEER_API void be_effekseer_set_log_callback(
    BehemironEffekseerLogCallback callback,
    void* user_data
);
// 获取最近一次错误消息。
BEHEMIRON_EFFEKSEER_API const char* be_effekseer_get_last_error(void);
// 清空最近一次错误消息。
BEHEMIRON_EFFEKSEER_API void be_effekseer_clear_last_error(void);
// 终止当前后端，并释放 backend 级别缓存。
BEHEMIRON_EFFEKSEER_API void be_effekseer_backend_terminate(void);

// endregion

// region Manager 通用接口

// 创建 manager 核心对象。
BEHEMIRON_EFFEKSEER_API BehemironEffekseerManagerHandle be_effekseer_manager_create(void);
// 销毁 manager 核心对象。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_destroy(BehemironEffekseerManagerHandle handle);
// 依据当前 backend 初始化 renderer 与 manager。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_initialize(
    BehemironEffekseerManagerHandle handle,
    int32_t sprite_max_count,
    int32_t srgb_mode
);
// 开始一次 update 批次。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_begin_update(BehemironEffekseerManagerHandle handle);
// 结束一次 update 批次。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_end_update(BehemironEffekseerManagerHandle handle);
// 推进 manager 时间。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_update(BehemironEffekseerManagerHandle handle, float delta_frames);
// 把指定实例推进到目标帧。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_update_handle_to_move_to_frame(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float frame
);
// 播放一个 effect，并返回实例句柄。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_play(
    BehemironEffekseerManagerHandle handle,
    BehemironEffekseerEffectHandle effect
);
// 使用 typed options 播放 effect。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_play_with_options(
    BehemironEffekseerManagerHandle handle,
    BehemironEffekseerEffectHandle effect,
    const BehemironEffekseerPlayOptions* options
);
// 停止全部实例。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_stop_all(BehemironEffekseerManagerHandle handle);
// 停止指定实例。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_stop(BehemironEffekseerManagerHandle handle, int32_t effect_handle);
// 设置实例暂停状态。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_paused(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t paused
);
// 设置实例显示状态。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_shown(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t shown
);
// 向实例发送 trigger。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_send_trigger(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t index
);
// 设置实例位置。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_effect_position(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float x,
    float y,
    float z
);
// 设置实例旋转。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_effect_rotation(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float x,
    float y,
    float z
);
// 设置实例缩放。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_effect_scale(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float x,
    float y,
    float z
);
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_layer_parameter(
    BehemironEffekseerManagerHandle handle,
    int32_t layer,
    float viewer_pos_x,
    float viewer_pos_y,
    float viewer_pos_z,
    float distance_bias
);
// 设置实例 3x4 变换矩阵。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_effect_transform_matrix(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float v0,
    float v1,
    float v2,
    float v3,
    float v4,
    float v5,
    float v6,
    float v7,
    float v8,
    float v9,
    float v10,
    float v11
);
// 设置实例基础 3x4 变换矩阵。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_effect_transform_base_matrix(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    float v0,
    float v1,
    float v2,
    float v3,
    float v4,
    float v5,
    float v6,
    float v7,
    float v8,
    float v9,
    float v10,
    float v11
);
// 绘制后景层。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_draw_back(BehemironEffekseerManagerHandle handle, int32_t layer);
// 绘制前景层。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_draw_front(BehemironEffekseerManagerHandle handle, int32_t layer);
// 设置实例 layer。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_layer(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t layer
);
// 设置相机朝向和位置。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_camera_parameter(
    BehemironEffekseerManagerHandle handle,
    float front_x,
    float front_y,
    float front_z,
    float pos_x,
    float pos_y,
    float pos_z
);
// 设置 4x4 投影矩阵。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_projection_matrix(
    BehemironEffekseerManagerHandle handle,
    const float* values16
);
// 设置 4x4 相机矩阵。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_camera_matrix(
    BehemironEffekseerManagerHandle handle,
    const float* values16
);
// 查询实例是否仍然存在。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_exists(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle
);
// 按窗口大小构建简化 view-projection。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_view_projection_matrix_with_simple_window(
    BehemironEffekseerManagerHandle handle,
    int32_t window_width,
    int32_t window_height
);
// 设置实例动态输入。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_dynamic_input(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t index,
    float value
);
// 获取实例动态输入。
BEHEMIRON_EFFEKSEER_API float be_effekseer_manager_get_dynamic_input(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle,
    int32_t index
);
// 批量播放 effect。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_batch_play(
    BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchPlayItem* items,
    int32_t item_count,
    int32_t* out_handles
);
// 批量设置实例 transform。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_batch_set_transform(
    BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchTransformItem* items,
    int32_t item_count
);
// 批量设置实例 dynamic input。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_batch_set_dynamic_input(
    BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchDynamicInputItem* items,
    int32_t item_count
);
// 设置世界碰撞回调。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_collision_callback(
    BehemironEffekseerManagerHandle handle,
    BehemironEffekseerCollisionCallback callback,
    void* user_data
);
// 启动 worker 线程。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_launch_worker_threads(
    BehemironEffekseerManagerHandle handle,
    int32_t worker_count
);

// endregion

// region Manager GL 接口

// 绑定 OpenGL 背景纹理。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_background_gl(
    BehemironEffekseerManagerHandle handle,
    uint32_t glid,
    int32_t has_mipmap
);
// 绑定 OpenGL 深度纹理。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_depth_gl(
    BehemironEffekseerManagerHandle handle,
    uint32_t glid,
    int32_t has_mipmap
);

// endregion

// region Manager VK 接口

// 为 Vulkan backend 绑定外部命令缓冲。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_begin_command_list_vk(
    BehemironEffekseerManagerHandle handle,
    uint64_t native_command_buffer
);
// 结束当前 Vulkan 命令列表。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_end_command_list_vk(
    BehemironEffekseerManagerHandle handle
);
// 绑定 Vulkan 背景图像。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_background_vk(
    BehemironEffekseerManagerHandle handle,
    uint64_t image,
    uint32_t aspect,
    uint32_t format
);
// 绑定 Vulkan 深度图像。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_set_depth_vk(
    BehemironEffekseerManagerHandle handle,
    uint64_t image,
    uint32_t aspect,
    uint32_t format
);

// endregion

// region Manager 共享渲染资源接口

// 清除当前背景资源绑定。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_unset_background(BehemironEffekseerManagerHandle handle);
// 清除当前深度资源绑定。
BEHEMIRON_EFFEKSEER_API void be_effekseer_manager_unset_depth(BehemironEffekseerManagerHandle handle);
// 获取实例存活粒子数。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_get_instance_count(
    BehemironEffekseerManagerHandle handle,
    int32_t effect_handle
);
// 获取 manager 当前总粒子数。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_get_total_instance_count(
    BehemironEffekseerManagerHandle handle
);
// 获取 manager 当前统计。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_manager_get_stats(
    BehemironEffekseerManagerHandle handle,
    BehemironEffekseerManagerStats* out_stats
);

// endregion

// region Effect 接口

// 创建 effect 核心对象。
BEHEMIRON_EFFEKSEER_API BehemironEffekseerEffectHandle be_effekseer_effect_create(void);
// 销毁 effect 核心对象。
BEHEMIRON_EFFEKSEER_API void be_effekseer_effect_destroy(BehemironEffekseerEffectHandle handle);
// 加载 efk 二进制本体。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_load(
    BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    int32_t len,
    float magnification
);
// 获取指定纹理路径。
BEHEMIRON_EFFEKSEER_API const uint16_t* be_effekseer_effect_get_texture_path_utf16(
    BehemironEffekseerEffectHandle handle,
    int32_t index,
    int32_t texture_type
);
// 获取指定纹理类型数量。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_texture_count(
    BehemironEffekseerEffectHandle handle,
    int32_t texture_type
);
// 注入指定纹理资源。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_load_texture(
    BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    int32_t len,
    int32_t index,
    int32_t texture_type
);
// 查询指定纹理是否已加载。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_has_texture_loaded(
    BehemironEffekseerEffectHandle handle,
    int32_t index,
    int32_t texture_type
);
// 获取指定模型路径。
BEHEMIRON_EFFEKSEER_API const uint16_t* be_effekseer_effect_get_model_path_utf16(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取模型数量。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_model_count(BehemironEffekseerEffectHandle handle);
// 注入指定模型资源。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_load_model(
    BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    int32_t len,
    int32_t index
);
// 查询指定模型是否已加载。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_has_model_loaded(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取指定材质路径。
BEHEMIRON_EFFEKSEER_API const uint16_t* be_effekseer_effect_get_material_path_utf16(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取材质数量。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_material_count(BehemironEffekseerEffectHandle handle);
// 注入指定材质资源。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_load_material(
    BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    int32_t len,
    int32_t index
);
// 查询指定材质是否已加载。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_has_material_loaded(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取指定曲线路径。
BEHEMIRON_EFFEKSEER_API const uint16_t* be_effekseer_effect_get_curve_path_utf16(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取曲线数量。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_curve_count(BehemironEffekseerEffectHandle handle);
// 注入指定曲线资源。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_load_curve(
    BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    int32_t len,
    int32_t index
);
// 查询指定曲线是否已加载。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_has_curve_loaded(
    BehemironEffekseerEffectHandle handle,
    int32_t index
);
// 获取实例生命周期最大帧。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_term_max(BehemironEffekseerEffectHandle handle);
// 获取实例生命周期最小帧。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_term_min(BehemironEffekseerEffectHandle handle);
// 获取 effect 统一依赖数量。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_dependency_count(BehemironEffekseerEffectHandle handle);
// 获取 effect 统一依赖项。
BEHEMIRON_EFFEKSEER_API int32_t be_effekseer_effect_get_dependency(
    BehemironEffekseerEffectHandle handle,
    int32_t index,
    BehemironEffekseerEffectDependency* out_dependency
);

// endregion

#ifdef __cplusplus
}
#endif
