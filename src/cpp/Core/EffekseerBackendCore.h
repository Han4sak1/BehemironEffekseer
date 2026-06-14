#pragma once

#include <Effekseer.h>
#include <array>

// region 后端枚举

enum class EffekseerCoreDeviceType {
    Unknown,
    OpenGL,
    Vulkan,
};

// endregion

// region 日志回调

using EffekseerCoreLogCallback = void(*)(int32_t logType, const char *message, void *userData);

// endregion

// region Vulkan 渲染通道信息

struct EffekseerCoreVulkanRenderPassInfo {
    bool doesPresentToScreen = false;
    std::array<uint32_t, 8> renderTextureFormats = {};
    int32_t renderTextureCount = 1;
    uint32_t depthFormat = 0;
};

// endregion

// region Setting 缓存

class EffekseerSettingCore : public ::Effekseer::Setting {
    static Effekseer::RefPtr<EffekseerSettingCore> effekseerSetting_;

    Effekseer::Backend::GraphicsDeviceRef graphicsDevice_ = nullptr;

public:
    // 构造当前后端对应的 GraphicsDevice，并挂接统一资源加载器。
    explicit EffekseerSettingCore(bool isSrgbMode = false);

    ~EffekseerSettingCore() override;

    bool IsValid() const;

    int Release() override;

    Effekseer::Backend::GraphicsDeviceRef GetGraphicsDevice() const;

    static Effekseer::RefPtr<EffekseerSettingCore> Create(bool isSrgbMode = false);

    static void ResetCache();
};

// endregion

// region 后端入口

class EffekseerBackendCore {
public:
    EffekseerBackendCore() = default;

    ~EffekseerBackendCore() = default;

    static EffekseerCoreDeviceType GetDevice();

    static bool InitializeWithOpenGL();

    static bool InitializeWithVulkan(uint64_t physicalDevice,
                                     uint64_t device,
                                     uint64_t queue,
                                     uint64_t commandPool,
                                     int32_t swapBufferCount,
                                     const EffekseerCoreVulkanRenderPassInfo &renderPassInfo);

    static const EffekseerCoreVulkanRenderPassInfo &GetVulkanRenderPassInfo();

    static void SetLogCallback(EffekseerCoreLogCallback callback, void *userData);

    static const char *GetLastError();

    static void SetLastError(const char *message);

    static void ClearLastError();

    static void Terminate();
};

// endregion
