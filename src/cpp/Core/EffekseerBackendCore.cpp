#include "EffekseerBackendCore.h"

#include <EffekseerRendererCommon/TextureLoader.h>
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
#include <EffekseerRendererGL.h>
#endif

#ifdef __EFFEKSEER_BUILD_VULKAN__
#include <EffekseerRendererLLGI/Common.h>
#include <EffekseerRendererLLGI/EffekseerRendererLLGI.MaterialLoader.h>
#include <EffekseerRendererVulkan.h>
#include <EffekseerRendererVulkan/EffekseerMaterialCompilerVulkan.h>
#endif

#include <iostream>
#include <string>

// region 全局后端状态

namespace {
    EffekseerCoreLogCallback logCallback_ = nullptr;
    void *logCallbackUserData_ = nullptr;
    std::string lastErrorMessage_;

    void DispatchLog(const Effekseer::LogType logType, const std::string &message) {
        if (logCallback_ != nullptr) {
            logCallback_(static_cast<int32_t>(logType), message.c_str(), logCallbackUserData_);
        }
    }

    void SetupLogger() {
        Effekseer::SetLogger(
            [](const Effekseer::LogType logType, const std::string &s) {
                if (logType == Effekseer::LogType::Error) {
                    lastErrorMessage_ = s;
                }
                DispatchLog(logType, s);
                if (logType != Effekseer::LogType::Info) {
                    std::cout << s << std::endl;
                }
            });
    }

    auto deviceType_ = EffekseerCoreDeviceType::Unknown;
    std::array<uint64_t, 4> userData_ = {};
    EffekseerCoreVulkanRenderPassInfo vulkanRenderPassInfo_ = {};
    int32_t vulkanSwapBufferCount_ = 0;

#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    EffekseerRendererGL::OpenGLDeviceType GetOpenGLDeviceType() {
#if defined(__ANDROID__)
        return EffekseerRendererGL::OpenGLDeviceType::OpenGLES3;
#else
        return EffekseerRendererGL::OpenGLDeviceType::OpenGL3;
#endif
    }
#endif
} // namespace

// endregion

// region 资源加载器

class CustomTextureLoader : public Effekseer::TextureLoader {
    Effekseer::TextureLoaderRef internalLoader_ = nullptr;

public:
    // 统一把字节流转给后端自己的纹理加载器。
    explicit CustomTextureLoader(const Effekseer::Backend::GraphicsDeviceRef &graphicsDevice,
                                 const bool isSrgbMode = false) {
        internalLoader_ = EffekseerRenderer::CreateTextureLoader(
            graphicsDevice, nullptr, isSrgbMode ? Effekseer::ColorSpaceType::Linear : Effekseer::ColorSpaceType::Gamma);
    }

    ~CustomTextureLoader() override = default;

    Effekseer::TextureRef Load(const EFK_CHAR *path, Effekseer::TextureType textureType) override {
        return nullptr;
    }

    Effekseer::TextureRef Load(const void *data, const int32_t size, const Effekseer::TextureType textureType,
                               const bool isMipMapEnabled) override {
        return internalLoader_->Load(data, size, textureType, isMipMapEnabled);
    }

    void Unload(const Effekseer::TextureRef data) override {
        internalLoader_->Unload(data);
    }
};

class CustomModelLoader : public Effekseer::ModelLoader {
    Effekseer::ModelLoaderRef internalLoader_ = nullptr;

public:
    explicit CustomModelLoader(const Effekseer::Backend::GraphicsDeviceRef &graphicsDevice) {
        internalLoader_ = EffekseerRenderer::CreateModelLoader(graphicsDevice);
    }

    ~CustomModelLoader() override = default;

    Effekseer::ModelRef Load(const EFK_CHAR *path) override {
        return nullptr;
    }

    Effekseer::ModelRef Load(const void *data, const int32_t size) override {
        return internalLoader_->Load(data, size);
    }

    void Unload(const Effekseer::ModelRef data) override {
        internalLoader_->Unload(data);
    }
};

class CustomMaterialLoader : public Effekseer::MaterialLoader {
    Effekseer::MaterialLoaderRef internalLoader_ = nullptr;

public:
    // Vulkan 使用专门的材质编译器，GL 继续走原有材质加载器。
    explicit CustomMaterialLoader(Effekseer::Backend::GraphicsDeviceRef graphicsDevice) {
        if (deviceType_ == EffekseerCoreDeviceType::Vulkan) {
#ifdef __EFFEKSEER_BUILD_VULKAN__
            internalLoader_ = Effekseer::MakeRefPtr<EffekseerRendererLLGI::MaterialLoader>(
                graphicsDevice,
                nullptr,
                Effekseer::CompiledMaterialPlatformType::Vulkan,
                new Effekseer::MaterialCompilerVulkan());
#endif
        }

        if (internalLoader_ == nullptr) {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
            internalLoader_ = EffekseerRendererGL::CreateMaterialLoader(graphicsDevice);
#endif
        }
    }

    ~CustomMaterialLoader() override = default;

    Effekseer::MaterialRef Load(const EFK_CHAR *path) override {
        return nullptr;
    }

    Effekseer::MaterialRef
    Load(const void *data, const int32_t size, const Effekseer::MaterialFileType fileType) override {
        return internalLoader_->Load(data, size, fileType);
    }

    void Unload(const Effekseer::MaterialRef data) override {
        internalLoader_->Unload(data);
    }
};

// endregion

// region Setting

Effekseer::RefPtr<EffekseerSettingCore> EffekseerSettingCore::effekseerSetting_ = nullptr;

EffekseerSettingCore::EffekseerSettingCore(bool isSrgbMode) {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    if (deviceType_ == EffekseerCoreDeviceType::OpenGL) {
        graphicsDevice_ = EffekseerRendererGL::CreateGraphicsDevice(GetOpenGLDeviceType());
    }
#endif
#ifdef __EFFEKSEER_BUILD_VULKAN__
	else if (deviceType_ == EffekseerCoreDeviceType::Vulkan) {
        graphicsDevice_ = EffekseerRendererVulkan::CreateGraphicsDevice(
            reinterpret_cast<VkPhysicalDevice>(userData_[0]),
            reinterpret_cast<VkDevice>(userData_[1]),
            reinterpret_cast<VkQueue>(userData_[2]),
            reinterpret_cast<VkCommandPool>(userData_[3]),
            vulkanSwapBufferCount_);
    }
#endif

    if (graphicsDevice_ != nullptr) {
        SetTextureLoader(Effekseer::MakeRefPtr<CustomTextureLoader>(graphicsDevice_, isSrgbMode));
        SetModelLoader(Effekseer::MakeRefPtr<CustomModelLoader>(graphicsDevice_));
        SetMaterialLoader(Effekseer::MakeRefPtr<CustomMaterialLoader>(graphicsDevice_));
        SetCurveLoader(Effekseer::MakeRefPtr<Effekseer::CurveLoader>());
    }
}

EffekseerSettingCore::~EffekseerSettingCore() {
    graphicsDevice_.Reset();
}

bool EffekseerSettingCore::IsValid() const {
    return graphicsDevice_ != nullptr;
}

int EffekseerSettingCore::Release() {
    const auto ret = Setting::Release();
    if (ret == 1) {
        effekseerSetting_ = nullptr;
    }
    return ret;
}

Effekseer::Backend::GraphicsDeviceRef EffekseerSettingCore::GetGraphicsDevice() const {
    return graphicsDevice_;
}

Effekseer::RefPtr<EffekseerSettingCore> EffekseerSettingCore::Create(bool isSrgbMode) {
    if (effekseerSetting_ == nullptr) {
        effekseerSetting_ = Effekseer::MakeRefPtr<EffekseerSettingCore>(isSrgbMode);
        if (!effekseerSetting_->IsValid()) {
            effekseerSetting_.Reset();
        }
    }

    return effekseerSetting_;
}

void EffekseerSettingCore::ResetCache() {
    effekseerSetting_ = nullptr;
}

// endregion

// region 后端控制

EffekseerCoreDeviceType EffekseerBackendCore::GetDevice() {
    return deviceType_;
}

bool EffekseerBackendCore::InitializeWithOpenGL() {
#if !defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    SetLastError("Failed to initialize OpenGL backend : build was configured without BUILD_GL");
    return false;
#else
    EffekseerSettingCore::ResetCache();
    deviceType_ = EffekseerCoreDeviceType::OpenGL;
    userData_.fill(0);
    vulkanRenderPassInfo_ = {};
    vulkanSwapBufferCount_ = 0;
    ClearLastError();
    SetupLogger();
    return true;
#endif
}

bool EffekseerBackendCore::InitializeWithVulkan(const uint64_t physicalDevice,
                                                const uint64_t device,
                                                const uint64_t queue,
                                                const uint64_t commandPool,
                                                const int32_t swapBufferCount,
                                                const EffekseerCoreVulkanRenderPassInfo &renderPassInfo) {
#ifndef __EFFEKSEER_BUILD_VULKAN__
    (void) physicalDevice;
    (void) device;
    (void) queue;
    (void) commandPool;
    (void) swapBufferCount;
    (void) renderPassInfo;
    return false;
#else
    if (swapBufferCount <= 0) {
        SetLastError("Failed to initialize Vulkan backend : swapBufferCount must be positive");
        return false;
    }

    EffekseerSettingCore::ResetCache();
    deviceType_ = EffekseerCoreDeviceType::Vulkan;
    userData_[0] = physicalDevice;
    userData_[1] = device;
    userData_[2] = queue;
    userData_[3] = commandPool;
    vulkanRenderPassInfo_ = renderPassInfo;
    vulkanSwapBufferCount_ = swapBufferCount;
    ClearLastError();
    SetupLogger();
    return true;
#endif
}

const EffekseerCoreVulkanRenderPassInfo &EffekseerBackendCore::GetVulkanRenderPassInfo() {
    return vulkanRenderPassInfo_;
}

void EffekseerBackendCore::SetLogCallback(const EffekseerCoreLogCallback callback, void *userData) {
    logCallback_ = callback;
    logCallbackUserData_ = userData;
}

const char *EffekseerBackendCore::GetLastError() {
    return lastErrorMessage_.empty() ? nullptr : lastErrorMessage_.c_str();
}

void EffekseerBackendCore::SetLastError(const char *message) {
    lastErrorMessage_ = message == nullptr ? "" : message;
    if (!lastErrorMessage_.empty()) {
        DispatchLog(Effekseer::LogType::Error, lastErrorMessage_);
    }
}

void EffekseerBackendCore::ClearLastError() {
    lastErrorMessage_.clear();
}

void EffekseerBackendCore::Terminate() {
    EffekseerSettingCore::ResetCache();
    deviceType_ = EffekseerCoreDeviceType::Unknown;
    userData_.fill(0);
    vulkanRenderPassInfo_ = {};
    vulkanSwapBufferCount_ = 0;
}

// endregion
