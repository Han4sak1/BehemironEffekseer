#pragma once

#include "BehemironEffekseerCollisionTypes.h"
#include <EffekseerRendererCommon/EffekseerRenderer.Renderer.h>

class EffekseerEffectCore;

// region Manager 封装

class EffekseerManagerCore {
    Effekseer::ManagerRef manager_ = nullptr;
    EffekseerRenderer::RendererRef renderer_ = nullptr;
    Effekseer::RefPtr<::EffekseerRenderer::SingleFrameMemoryPool> singleFrameMemoryPool_ = nullptr;
    Effekseer::RefPtr<::EffekseerRenderer::CommandList> commandList_ = nullptr;
    float restDeltaTime_ = 0.0f;
    Effekseer::Backend::TextureRef backgroundtx_;
    Effekseer::Backend::TextureRef depthtx_;
    BehemironEffekseerCollisionCallback collisionCallback_ = nullptr;
    void* collisionCallbackUserData_ = nullptr;

    // 把当前保存的 C 风格碰撞回调重新挂到 Effekseer manager 上。
    void ApplyCollisionCallback() const;

public:
    EffekseerManagerCore() = default;

    ~EffekseerManagerCore();

    // 根据当前 backend 创建 renderer，并把标准 renderer 挂到 manager 上。
    bool Initialize(int32_t spriteMaxCount, bool srgbMode = false);

    void Update(float deltaFrames);

    void BeginUpdate() const;

    void EndUpdate() const;

    void UpdateHandleToMoveToFrame(int handle, float v) const;

    int Play(const EffekseerEffectCore* effect) const;

    int PlayWithOptions(const EffekseerEffectCore* effect,
                        float positionX,
                        float positionY,
                        float positionZ,
                        float rotationX,
                        float rotationY,
                        float rotationZ,
                        float scaleX,
                        float scaleY,
                        float scaleZ,
                        int32_t startFrame) const;

    void StopAllEffects() const;

    void Stop(int handle) const;

    void SetPaused(int handle, bool v) const;

    void SetShown(int handle, bool v) const;

    void SendTrigger(int handle, int index) const;

    void SetEffectPosition(int handle, float x, float y, float z) const;

    void SetEffectRotation(int handle, float x, float y, float z) const;

    void SetEffectScale(int handle, float x, float y, float z) const;

    void SetTransform(int handle,
                      float positionX,
                      float positionY,
                      float positionZ,
                      float rotationX,
                      float rotationY,
                      float rotationZ,
                      float scaleX,
                      float scaleY,
                      float scaleZ) const;

    void SetLayerParameter(int layer, float viewerPosX, float viewerPosY, float viewerPosZ, float distanceBias) const;

    void SetEffectTransformMatrix(int handle,
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
                                  float v11) const;

    void SetEffectTransformBaseMatrix(int handle,
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
                                      float v11) const;

    void DrawBack(int layer = 1) const;

    void DrawFront(int layer = 1) const;

    void SetLayer(int handle, int layer) const;

    void SetCameraParameter(float frontX, float frontY, float frontZ, float posX, float posY, float posZ) const;

    void SetProjectionMatrix(float v0,
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
                             float v11,
                             float v12,
                             float v13,
                             float v14,
                             float v15) const;

    void SetCameraMatrix(float v0,
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
                         float v11,
                         float v12,
                         float v13,
                         float v14,
                         float v15) const;

    bool Exists(int handle) const;

    void SetViewProjectionMatrixWithSimpleWindow(int32_t windowWidth, int32_t windowHeight) const;

    void SetDynamicInput(int handle, int32_t index, float value) const;

    float GetDynamicInput(int handle, int32_t index) const;

    // 设置世界碰撞查询回调，供上层把中立几何命中结果回传给 Effekseer。
    void SetCollisionCallback(BehemironEffekseerCollisionCallback callback, void* userData);

    void LaunchWorkerThreads(int32_t n) const;

    // 仅在 Vulkan backend 下有效，用于把外部命令缓冲绑定给 Effekseer。
    void BeginVulkanCommandList(uint64_t nativeCommandBuffer) const;

    void EndVulkanCommandList() const;

    void SetBackground(uint32_t glid, bool hasMipmap);

    void SetBackgroundVulkan(uint64_t image, uint32_t aspect, uint32_t format);

    void UnsetBackground() const;

    void SetDepth(uint32_t glid, bool hasMipmap);

    void SetDepthVulkan(uint64_t image, uint32_t aspect, uint32_t format);

    void UnsetDepth() const;

    int GetInstanceCount(int handle) const;

    int GetTotalInstanceCount() const;

    int GetRestInstanceCount() const;

    int GetUpdateTime() const;

    int GetDrawTime() const;

    int GetGpuTime() const;

    int GetDrawCallCount() const;

    int GetDrawVertexCount() const;
};

// endregion
