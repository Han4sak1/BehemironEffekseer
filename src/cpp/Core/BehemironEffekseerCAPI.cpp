#include "BehemironEffekseerCAPI.h"

#include "EffekseerBackendCore.h"
#include "EffekseerEffectCore.h"
#include "EffekseerManagerCore.h"

// region C API 辅助转换

namespace {

constexpr uint32_t kBehemironEffekseerAbiVersion = 2;

EffekseerTextureType ToTextureType(const int32_t type)
{
    switch (type)
    {
    case BEHEMIRON_EFFEKSEER_TEXTURE_NORMAL:
        return EffekseerTextureType::Normal;
    case BEHEMIRON_EFFEKSEER_TEXTURE_DISTORTION:
        return EffekseerTextureType::Distortion;
    case BEHEMIRON_EFFEKSEER_TEXTURE_COLOR:
    default:
        return EffekseerTextureType::Color;
    }
}

EffekseerManagerCore* ToManager(const BehemironEffekseerManagerHandle handle)
{
    return static_cast<EffekseerManagerCore*>(handle);
}

EffekseerEffectCore* ToEffect(const BehemironEffekseerEffectHandle handle)
{
    return static_cast<EffekseerEffectCore*>(handle);
}

int32_t ToInt(const bool value)
{
    return value ? 1 : 0;
}

constexpr BehemironEffekseerPlayOptions kDefaultPlayOptions = {
    0.0f, 0.0f, 0.0f,
    0.0f, 0.0f, 0.0f,
    1.0f, 1.0f, 1.0f,
    0
};

EffekseerCoreVulkanRenderPassInfo ToRenderPassInfo(
    const int32_t doesPresentToScreen,
    const uint32_t* renderTextureFormats,
    const int32_t renderTextureCount,
    const uint32_t depthFormat)
{
    EffekseerCoreVulkanRenderPassInfo result;
    result.doesPresentToScreen = doesPresentToScreen != 0;
    result.renderTextureCount = renderTextureCount < 0 ? 0 : (renderTextureCount > 8 ? 8 : renderTextureCount);
    result.depthFormat = depthFormat;
    for (int i = 0; i < 8; i++)
    {
        result.renderTextureFormats[i] =
            renderTextureFormats == nullptr || i >= result.renderTextureCount ? 0 : renderTextureFormats[i];
    }
    return result;
}

} // namespace

// endregion

// region Backend 接口

// 初始化 OpenGL backend。
int32_t be_effekseer_backend_initialize_gl()
{
    return ToInt(EffekseerBackendCore::InitializeWithOpenGL());
}

// 初始化 Vulkan backend。
int32_t be_effekseer_backend_initialize_vk(
    const uint64_t physical_device,
    const uint64_t device,
    const uint64_t queue,
    const uint64_t command_pool,
    const int32_t swap_buffer_count,
    const int32_t does_present_to_screen,
    const uint32_t* render_texture_formats,
    const int32_t render_texture_count,
    const uint32_t depth_format)
{
    return ToInt(EffekseerBackendCore::InitializeWithVulkan(
        physical_device,
        device,
        queue,
        command_pool,
        swap_buffer_count,
        ToRenderPassInfo(
            does_present_to_screen,
            render_texture_formats,
            render_texture_count,
            depth_format)));
}

uint32_t be_effekseer_get_abi_version()
{
    return kBehemironEffekseerAbiVersion;
}

uint32_t be_effekseer_get_feature_flags()
{
    uint32_t flags = 0;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_COLLISION_CALLBACK;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_OPENGL;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_EFFECT_DEPENDENCY_VIEW;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_TYPED_PLAY_OPTIONS;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_BATCH_API;
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_DIAGNOSTICS;
#ifdef __EFFEKSEER_BUILD_VULKAN__
    flags |= BEHEMIRON_EFFEKSEER_FEATURE_VULKAN;
#endif
    return flags;
}

int32_t be_effekseer_backend_get_type()
{
    return static_cast<int32_t>(EffekseerBackendCore::GetDevice());
}

uint32_t be_effekseer_backend_get_capabilities()
{
    uint32_t flags = BEHEMIRON_EFFEKSEER_FEATURE_COLLISION_CALLBACK
        | BEHEMIRON_EFFEKSEER_FEATURE_EFFECT_DEPENDENCY_VIEW
        | BEHEMIRON_EFFEKSEER_FEATURE_TYPED_PLAY_OPTIONS
        | BEHEMIRON_EFFEKSEER_FEATURE_BATCH_API
        | BEHEMIRON_EFFEKSEER_FEATURE_DIAGNOSTICS;
    switch (EffekseerBackendCore::GetDevice())
    {
    case EffekseerCoreDeviceType::OpenGL:
        flags |= BEHEMIRON_EFFEKSEER_FEATURE_OPENGL;
        break;
    case EffekseerCoreDeviceType::Vulkan:
        flags |= BEHEMIRON_EFFEKSEER_FEATURE_VULKAN;
        break;
    case EffekseerCoreDeviceType::Unknown:
    default:
        break;
    }
    return flags;
}

void be_effekseer_set_log_callback(BehemironEffekseerLogCallback callback, void* user_data)
{
    EffekseerBackendCore::SetLogCallback(callback, user_data);
}

const char* be_effekseer_get_last_error()
{
    return EffekseerBackendCore::GetLastError();
}

void be_effekseer_clear_last_error()
{
    EffekseerBackendCore::ClearLastError();
}

// 终止 backend 全局状态。
void be_effekseer_backend_terminate()
{
    EffekseerBackendCore::Terminate();
}

// endregion

// region Manager 通用接口

// 创建 manager core。
BehemironEffekseerManagerHandle be_effekseer_manager_create()
{
    return new EffekseerManagerCore();
}

// 销毁 manager core。
void be_effekseer_manager_destroy(const BehemironEffekseerManagerHandle handle)
{
    delete ToManager(handle);
}

// 初始化 manager。
int32_t be_effekseer_manager_initialize(const BehemironEffekseerManagerHandle handle, const int32_t sprite_max_count, const int32_t srgb_mode)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        return ToInt(manager->Initialize(sprite_max_count, srgb_mode != 0));
    }
    return 0;
}

// 开始 update 批次。
void be_effekseer_manager_begin_update(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->BeginUpdate();
    }
}

// 结束 update 批次。
void be_effekseer_manager_end_update(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->EndUpdate();
    }
}

// 推进 manager 时间。
void be_effekseer_manager_update(const BehemironEffekseerManagerHandle handle, const float delta_frames)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->Update(delta_frames);
    }
}

// 推进实例到指定帧。
void be_effekseer_manager_update_handle_to_move_to_frame(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const float frame)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->UpdateHandleToMoveToFrame(effect_handle, frame);
    }
}

// 播放 effect。
int32_t be_effekseer_manager_play(const BehemironEffekseerManagerHandle handle, const BehemironEffekseerEffectHandle effect)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        if (const auto core = ToEffect(effect); core != nullptr)
        {
            return manager->Play(core);
        }
    }
    return -1;
}

int32_t be_effekseer_manager_play_with_options(
    const BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerEffectHandle effect,
    const BehemironEffekseerPlayOptions* options)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        if (const auto core = ToEffect(effect); core != nullptr)
        {
            const auto* actualOptions = options == nullptr ? &kDefaultPlayOptions : options;
            return manager->PlayWithOptions(
                core,
                actualOptions->position_x,
                actualOptions->position_y,
                actualOptions->position_z,
                actualOptions->rotation_x,
                actualOptions->rotation_y,
                actualOptions->rotation_z,
                actualOptions->scale_x,
                actualOptions->scale_y,
                actualOptions->scale_z,
                actualOptions->start_frame);
        }
    }
    return -1;
}

// 停止全部实例。
void be_effekseer_manager_stop_all(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->StopAllEffects();
    }
}

// 停止指定实例。
void be_effekseer_manager_stop(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->Stop(effect_handle);
    }
}

// 设置暂停状态。
void be_effekseer_manager_set_paused(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const int32_t paused)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetPaused(effect_handle, paused != 0);
    }
}

// 设置显示状态。
void be_effekseer_manager_set_shown(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const int32_t shown)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetShown(effect_handle, shown != 0);
    }
}

// 发送 trigger。
void be_effekseer_manager_send_trigger(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const int32_t index)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SendTrigger(effect_handle, index);
    }
}

// 设置实例位置。
void be_effekseer_manager_set_effect_position(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const float x, const float y, const float z)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetEffectPosition(effect_handle, x, y, z);
    }
}

// 设置实例旋转。
void be_effekseer_manager_set_effect_rotation(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const float x, const float y, const float z)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetEffectRotation(effect_handle, x, y, z);
    }
}

// 设置实例缩放。
void be_effekseer_manager_set_effect_scale(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const float x, const float y, const float z)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetEffectScale(effect_handle, x, y, z);
    }
}

// 设置 layer 参数。
void be_effekseer_manager_set_layer_parameter(
    const BehemironEffekseerManagerHandle handle,
    const int32_t layer,
    const float viewer_pos_x,
    const float viewer_pos_y,
    const float viewer_pos_z,
    const float distance_bias)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetLayerParameter(layer, viewer_pos_x, viewer_pos_y, viewer_pos_z, distance_bias);
    }
}

// 设置实例变换矩阵。
void be_effekseer_manager_set_effect_transform_matrix(
    const BehemironEffekseerManagerHandle handle,
    const int32_t effect_handle,
    const float v0,
    const float v1,
    const float v2,
    const float v3,
    const float v4,
    const float v5,
    const float v6,
    const float v7,
    const float v8,
    const float v9,
    const float v10,
    const float v11)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetEffectTransformMatrix(effect_handle, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
    }
}

// 设置实例基础变换矩阵。
void be_effekseer_manager_set_effect_transform_base_matrix(
    const BehemironEffekseerManagerHandle handle,
    const int32_t effect_handle,
    const float v0,
    const float v1,
    const float v2,
    const float v3,
    const float v4,
    const float v5,
    const float v6,
    const float v7,
    const float v8,
    const float v9,
    const float v10,
    const float v11)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetEffectTransformBaseMatrix(effect_handle, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
    }
}

// 绘制后景层。
void be_effekseer_manager_draw_back(const BehemironEffekseerManagerHandle handle, const int32_t layer)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->DrawBack(layer);
    }
}

// 绘制前景层。
void be_effekseer_manager_draw_front(const BehemironEffekseerManagerHandle handle, const int32_t layer)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->DrawFront(layer);
    }
}

// 设置实例 layer。
void be_effekseer_manager_set_layer(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle, const int32_t layer)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetLayer(effect_handle, layer);
    }
}

// 设置相机参数。
void be_effekseer_manager_set_camera_parameter(
    const BehemironEffekseerManagerHandle handle,
    const float front_x,
    const float front_y,
    const float front_z,
    const float pos_x,
    const float pos_y,
    const float pos_z)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetCameraParameter(front_x, front_y, front_z, pos_x, pos_y, pos_z);
    }
}

// 设置投影矩阵。
void be_effekseer_manager_set_projection_matrix(const BehemironEffekseerManagerHandle handle, const float* values16)
{
    if (const auto manager = ToManager(handle); manager != nullptr && values16 != nullptr)
    {
        manager->SetProjectionMatrix(
            values16[0], values16[1], values16[2], values16[3],
            values16[4], values16[5], values16[6], values16[7],
            values16[8], values16[9], values16[10], values16[11],
            values16[12], values16[13], values16[14], values16[15]);
    }
}

// 设置相机矩阵。
void be_effekseer_manager_set_camera_matrix(const BehemironEffekseerManagerHandle handle, const float* values16)
{
    if (const auto manager = ToManager(handle); manager != nullptr && values16 != nullptr)
    {
        manager->SetCameraMatrix(
            values16[0], values16[1], values16[2], values16[3],
            values16[4], values16[5], values16[6], values16[7],
            values16[8], values16[9], values16[10], values16[11],
            values16[12], values16[13], values16[14], values16[15]);
    }
}

// 查询实例是否存在。
int32_t be_effekseer_manager_exists(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        return ToInt(manager->Exists(effect_handle));
    }
    return 0;
}

// 根据窗口大小设置简化矩阵。
void be_effekseer_manager_set_view_projection_matrix_with_simple_window(
    const BehemironEffekseerManagerHandle handle,
    const int32_t window_width,
    const int32_t window_height)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetViewProjectionMatrixWithSimpleWindow(window_width, window_height);
    }
}

// 设置动态输入。
void be_effekseer_manager_set_dynamic_input(
    const BehemironEffekseerManagerHandle handle,
    const int32_t effect_handle,
    const int32_t index,
    const float value)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetDynamicInput(effect_handle, index, value);
    }
}

// 获取动态输入。
float be_effekseer_manager_get_dynamic_input(
    const BehemironEffekseerManagerHandle handle,
    const int32_t effect_handle,
    const int32_t index)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        return manager->GetDynamicInput(effect_handle, index);
    }
    return 0.0f;
}

int32_t be_effekseer_manager_batch_play(
    const BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchPlayItem* items,
    const int32_t item_count,
    int32_t* out_handles)
{
    if (const auto manager = ToManager(handle); manager != nullptr && items != nullptr && item_count > 0 && out_handles != nullptr)
    {
        int32_t played = 0;
        for (int32_t i = 0; i < item_count; i++)
        {
            const auto effect = ToEffect(items[i].effect_handle);
            const auto effectHandle = effect == nullptr ? -1 : manager->PlayWithOptions(
                effect,
                items[i].options.position_x,
                items[i].options.position_y,
                items[i].options.position_z,
                items[i].options.rotation_x,
                items[i].options.rotation_y,
                items[i].options.rotation_z,
                items[i].options.scale_x,
                items[i].options.scale_y,
                items[i].options.scale_z,
                items[i].options.start_frame);
            out_handles[i] = effectHandle;
            if (effectHandle >= 0)
            {
                played++;
            }
        }
        return played;
    }

    return 0;
}

void be_effekseer_manager_batch_set_transform(
    const BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchTransformItem* items,
    const int32_t item_count)
{
    if (const auto manager = ToManager(handle); manager != nullptr && items != nullptr && item_count > 0)
    {
        for (int32_t i = 0; i < item_count; i++)
        {
            manager->SetTransform(
                items[i].effect_handle,
                items[i].position_x,
                items[i].position_y,
                items[i].position_z,
                items[i].rotation_x,
                items[i].rotation_y,
                items[i].rotation_z,
                items[i].scale_x,
                items[i].scale_y,
                items[i].scale_z);
        }
    }
}

void be_effekseer_manager_batch_set_dynamic_input(
    const BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerBatchDynamicInputItem* items,
    const int32_t item_count)
{
    if (const auto manager = ToManager(handle); manager != nullptr && items != nullptr && item_count > 0)
    {
        for (int32_t i = 0; i < item_count; i++)
        {
            manager->SetDynamicInput(items[i].effect_handle, items[i].input_index, items[i].value);
        }
    }
}

// 设置世界碰撞回调。
void be_effekseer_manager_set_collision_callback(
    const BehemironEffekseerManagerHandle handle,
    const BehemironEffekseerCollisionCallback callback,
    void* user_data)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetCollisionCallback(callback, user_data);
    }
}

// 启动 worker 线程。
void be_effekseer_manager_launch_worker_threads(const BehemironEffekseerManagerHandle handle, const int32_t worker_count)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->LaunchWorkerThreads(worker_count);
    }
}

// endregion

// region Manager VK 接口

// 开始 Vulkan 命令列表绑定。
void be_effekseer_manager_begin_command_list_vk(const BehemironEffekseerManagerHandle handle, const uint64_t native_command_buffer)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->BeginVulkanCommandList(native_command_buffer);
    }
}

// 结束 Vulkan 命令列表绑定。
void be_effekseer_manager_end_command_list_vk(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->EndVulkanCommandList();
    }
}

// endregion

// region Manager GL 接口

// 设置 OpenGL 背景纹理。
void be_effekseer_manager_set_background_gl(const BehemironEffekseerManagerHandle handle, const uint32_t glid, const int32_t has_mipmap)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetBackground(glid, has_mipmap != 0);
    }
}

// 设置 OpenGL 深度纹理。
void be_effekseer_manager_set_depth_gl(const BehemironEffekseerManagerHandle handle, const uint32_t glid, const int32_t has_mipmap)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetDepth(glid, has_mipmap != 0);
    }
}

// endregion

// region Manager VK 资源接口

// 设置 Vulkan 背景图像。
void be_effekseer_manager_set_background_vk(
    const BehemironEffekseerManagerHandle handle,
    const uint64_t image,
    const uint32_t aspect,
    const uint32_t format)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetBackgroundVulkan(image, aspect, format);
    }
}

// 设置 Vulkan 深度图像。
void be_effekseer_manager_set_depth_vk(
    const BehemironEffekseerManagerHandle handle,
    const uint64_t image,
    const uint32_t aspect,
    const uint32_t format)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->SetDepthVulkan(image, aspect, format);
    }
}

// endregion

// region Manager 共享渲染资源接口

// 清除背景资源。
void be_effekseer_manager_unset_background(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->UnsetBackground();
    }
}

// 清除深度资源。
void be_effekseer_manager_unset_depth(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        manager->UnsetDepth();
    }
}

// 获取实例粒子数。
int32_t be_effekseer_manager_get_instance_count(const BehemironEffekseerManagerHandle handle, const int32_t effect_handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        return manager->GetInstanceCount(effect_handle);
    }
    return 0;
}

// 获取 manager 总粒子数。
int32_t be_effekseer_manager_get_total_instance_count(const BehemironEffekseerManagerHandle handle)
{
    if (const auto manager = ToManager(handle); manager != nullptr)
    {
        return manager->GetTotalInstanceCount();
    }
    return 0;
}

int32_t be_effekseer_manager_get_stats(
    const BehemironEffekseerManagerHandle handle,
    BehemironEffekseerManagerStats* out_stats)
{
    if (const auto manager = ToManager(handle); manager != nullptr && out_stats != nullptr)
    {
        out_stats->total_instance_count = manager->GetTotalInstanceCount();
        out_stats->rest_instance_count = manager->GetRestInstanceCount();
        out_stats->update_time = manager->GetUpdateTime();
        out_stats->draw_time = manager->GetDrawTime();
        out_stats->gpu_time = manager->GetGpuTime();
        out_stats->draw_call_count = manager->GetDrawCallCount();
        out_stats->draw_vertex_count = manager->GetDrawVertexCount();
        return 1;
    }

    return 0;
}

// endregion

// region Effect 接口

// 创建 effect core。
BehemironEffekseerEffectHandle be_effekseer_effect_create()
{
    return new EffekseerEffectCore();
}

// 销毁 effect core。
void be_effekseer_effect_destroy(const BehemironEffekseerEffectHandle handle)
{
    delete ToEffect(handle);
}

// 加载 efk 本体。
int32_t be_effekseer_effect_load(const BehemironEffekseerEffectHandle handle, const uint8_t* data, const int32_t len, const float magnification)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && data != nullptr)
    {
        return ToInt(effect->Load(data, len, magnification));
    }
    return 0;
}

// 获取纹理路径。
const uint16_t* be_effekseer_effect_get_texture_path_utf16(const BehemironEffekseerEffectHandle handle, const int32_t index, const int32_t texture_type)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return reinterpret_cast<const uint16_t*>(effect->GetTexturePath(index, ToTextureType(texture_type)));
    }
    return nullptr;
}

// 获取纹理数量。
int32_t be_effekseer_effect_get_texture_count(const BehemironEffekseerEffectHandle handle, const int32_t texture_type)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetTextureCount(ToTextureType(texture_type));
    }
    return 0;
}

// 加载纹理资源。
int32_t be_effekseer_effect_load_texture(
    const BehemironEffekseerEffectHandle handle,
    const uint8_t* data,
    const int32_t len,
    const int32_t index,
    const int32_t texture_type)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && data != nullptr)
    {
        return ToInt(effect->LoadTexture(data, len, index, ToTextureType(texture_type)));
    }
    return 0;
}

// 查询纹理是否已加载。
int32_t be_effekseer_effect_has_texture_loaded(const BehemironEffekseerEffectHandle handle, const int32_t index, const int32_t texture_type)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return ToInt(effect->HasTextureLoaded(index, ToTextureType(texture_type)));
    }
    return 0;
}

// 获取模型路径。
const uint16_t* be_effekseer_effect_get_model_path_utf16(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return reinterpret_cast<const uint16_t*>(effect->GetModelPath(index));
    }
    return nullptr;
}

// 获取模型数量。
int32_t be_effekseer_effect_get_model_count(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetModelCount();
    }
    return 0;
}

// 加载模型资源。
int32_t be_effekseer_effect_load_model(const BehemironEffekseerEffectHandle handle, const uint8_t* data, const int32_t len, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && data != nullptr)
    {
        return ToInt(effect->LoadModel(data, len, index));
    }
    return 0;
}

// 查询模型是否已加载。
int32_t be_effekseer_effect_has_model_loaded(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return ToInt(effect->HasModelLoaded(index));
    }
    return 0;
}

// 获取材质路径。
const uint16_t* be_effekseer_effect_get_material_path_utf16(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return reinterpret_cast<const uint16_t*>(effect->GetMaterialPath(index));
    }
    return nullptr;
}

// 获取材质数量。
int32_t be_effekseer_effect_get_material_count(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetMaterialCount();
    }
    return 0;
}

// 加载材质资源。
int32_t be_effekseer_effect_load_material(const BehemironEffekseerEffectHandle handle, const uint8_t* data, const int32_t len, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && data != nullptr)
    {
        return ToInt(effect->LoadMaterial(data, len, index));
    }
    return 0;
}

// 查询材质是否已加载。
int32_t be_effekseer_effect_has_material_loaded(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return ToInt(effect->HasMaterialLoaded(index));
    }
    return 0;
}

// 获取曲线路径。
const uint16_t* be_effekseer_effect_get_curve_path_utf16(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return reinterpret_cast<const uint16_t*>(effect->GetCurvePath(index));
    }
    return nullptr;
}

// 获取曲线数量。
int32_t be_effekseer_effect_get_curve_count(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetCurveCount();
    }
    return 0;
}

// 加载曲线资源。
int32_t be_effekseer_effect_load_curve(const BehemironEffekseerEffectHandle handle, const uint8_t* data, const int32_t len, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && data != nullptr)
    {
        return ToInt(effect->LoadCurve(data, len, index));
    }
    return 0;
}

// 查询曲线是否已加载。
int32_t be_effekseer_effect_has_curve_loaded(const BehemironEffekseerEffectHandle handle, const int32_t index)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return ToInt(effect->HasCurveLoaded(index));
    }
    return 0;
}

// 获取生命周期最大帧。
int32_t be_effekseer_effect_get_term_max(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetTermMax();
    }
    return 0;
}

// 获取生命周期最小帧。
int32_t be_effekseer_effect_get_term_min(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetTermMin();
    }
    return 0;
}

int32_t be_effekseer_effect_get_dependency_count(const BehemironEffekseerEffectHandle handle)
{
    if (const auto effect = ToEffect(handle); effect != nullptr)
    {
        return effect->GetDependencyCount();
    }
    return 0;
}

int32_t be_effekseer_effect_get_dependency(
    const BehemironEffekseerEffectHandle handle,
    const int32_t index,
    BehemironEffekseerEffectDependency* out_dependency)
{
    if (const auto effect = ToEffect(handle); effect != nullptr && out_dependency != nullptr)
    {
        int32_t type = 0;
        int32_t slot = 0;
        const char16_t* path = nullptr;
        if (!effect->GetDependency(index, type, slot, path))
        {
            return 0;
        }

        out_dependency->type = type;
        out_dependency->slot = slot;
        out_dependency->path_utf16 = reinterpret_cast<const uint16_t*>(path);
        return 1;
    }

    return 0;
}

// endregion
