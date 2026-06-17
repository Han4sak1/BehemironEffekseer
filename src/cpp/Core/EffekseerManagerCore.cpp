#include "EffekseerManagerCore.h"

#include "EffekseerBackendCore.h"
#include "EffekseerEffectCore.h"

#include <Effekseer.h>
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
#include <EffekseerRendererGL.h>
#include <EffekseerRendererGL/EffekseerRendererGL.DeviceObject.h>
#include <EffekseerRendererGL/EffekseerRendererGL.GLExtension.h>
#endif

#ifdef __EFFEKSEER_BUILD_VULKAN__
#include <EffekseerRendererLLGI/Common.h>
#include <EffekseerRendererVulkan.h>
#endif

#include <vector>

// region 工具函数

#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
#ifndef GL_DRAW_FRAMEBUFFER
#define GL_DRAW_FRAMEBUFFER 0x8CA9
#endif
#ifndef GL_READ_FRAMEBUFFER
#define GL_READ_FRAMEBUFFER 0x8CA8
#endif
#ifndef GL_DRAW_FRAMEBUFFER_BINDING
#define GL_DRAW_FRAMEBUFFER_BINDING 0x8CA6
#endif
#ifndef GL_READ_FRAMEBUFFER_BINDING
#define GL_READ_FRAMEBUFFER_BINDING 0x8CAA
#endif
#ifndef GL_ACTIVE_TEXTURE
#define GL_ACTIVE_TEXTURE 0x84E0
#endif
#ifndef GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
#define GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS 0x8B4D
#endif
#ifndef GL_SAMPLER_BINDING
#define GL_SAMPLER_BINDING 0x8919
#endif
#ifndef GL_UNPACK_ROW_LENGTH
#define GL_UNPACK_ROW_LENGTH 0x0CF2
#endif
#ifndef GL_UNPACK_SKIP_ROWS
#define GL_UNPACK_SKIP_ROWS 0x0CF3
#endif
#ifndef GL_UNPACK_SKIP_PIXELS
#define GL_UNPACK_SKIP_PIXELS 0x0CF4
#endif
#endif

namespace {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    bool IsOpenGL3Family() {
        const auto deviceType = EffekseerRendererGL::GLExt::GetDeviceType();
        return deviceType == EffekseerRendererGL::OpenGLDeviceType::OpenGL3 ||
               deviceType == EffekseerRendererGL::OpenGLDeviceType::OpenGLES3;
    }

    bool SupportsOpenGLSamplerObjects() {
        return IsOpenGL3Family();
    }

    bool SupportsOpenGLFramebufferSplitBinding() {
        return IsOpenGL3Family();
    }

    bool SupportsOpenGLPixelStoreRows() {
        return EffekseerRendererGL::GLExt::GetDeviceType() != EffekseerRendererGL::OpenGLDeviceType::OpenGLES2;
    }

    std::vector<GLint> CaptureOpenGLSamplers() {
        std::vector<GLint> samplers;
        if (!SupportsOpenGLSamplerObjects()) {
            return samplers;
        }

        GLint maxTextureUnits = 0;
        glGetIntegerv(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, &maxTextureUnits);
        if (maxTextureUnits <= 0) {
            return samplers;
        }

        samplers.resize(static_cast<size_t>(maxTextureUnits));
        for (GLint i = 0; i < maxTextureUnits; i++) {
            EffekseerRendererGL::GLExt::glActiveTexture(GL_TEXTURE0 + static_cast<GLenum>(i));
            glGetIntegerv(GL_SAMPLER_BINDING, &samplers[static_cast<size_t>(i)]);
        }
        return samplers;
    }

    void RestoreOpenGLSamplers(const std::vector<GLint> &samplers) {
        if (samplers.empty()) {
            return;
        }

        for (size_t i = 0; i < samplers.size(); i++) {
            EffekseerRendererGL::GLExt::glBindSampler(static_cast<GLuint>(i), static_cast<GLuint>(samplers[i]));
        }
    }

    class ScopedOpenGLDrawState {
        bool splitFramebufferBinding_ = false;
        GLint previousFramebuffer_ = 0;
        GLint previousDrawFramebuffer_ = 0;
        GLint previousReadFramebuffer_ = 0;
        GLint previousViewport_[4] = {0, 0, 0, 0};
        GLint previousActiveTexture_ = GL_TEXTURE0;
        std::vector<GLint> previousSamplers_;

    public:
        ScopedOpenGLDrawState(const uint32_t targetFbo, const int32_t width, const int32_t height) {
            splitFramebufferBinding_ = SupportsOpenGLFramebufferSplitBinding();
            if (splitFramebufferBinding_) {
                glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &previousDrawFramebuffer_);
                glGetIntegerv(GL_READ_FRAMEBUFFER_BINDING, &previousReadFramebuffer_);
            } else {
                glGetIntegerv(GL_FRAMEBUFFER_BINDING, &previousFramebuffer_);
            }

            glGetIntegerv(GL_VIEWPORT, previousViewport_);
            glGetIntegerv(GL_ACTIVE_TEXTURE, &previousActiveTexture_);
            previousSamplers_ = CaptureOpenGLSamplers();
            EffekseerRendererGL::GLExt::glActiveTexture(static_cast<GLenum>(previousActiveTexture_));

            EffekseerRendererGL::GLExt::glBindFramebuffer(GL_FRAMEBUFFER, static_cast<GLuint>(targetFbo));
            glViewport(0, 0, width, height);
        }

        ~ScopedOpenGLDrawState() {
            RestoreOpenGLSamplers(previousSamplers_);
            EffekseerRendererGL::GLExt::glActiveTexture(static_cast<GLenum>(previousActiveTexture_));
            glViewport(previousViewport_[0], previousViewport_[1], previousViewport_[2], previousViewport_[3]);

            if (splitFramebufferBinding_) {
                EffekseerRendererGL::GLExt::glBindFramebuffer(
                    GL_DRAW_FRAMEBUFFER,
                    static_cast<GLuint>(previousDrawFramebuffer_));
                EffekseerRendererGL::GLExt::glBindFramebuffer(
                    GL_READ_FRAMEBUFFER,
                    static_cast<GLuint>(previousReadFramebuffer_));
            } else {
                EffekseerRendererGL::GLExt::glBindFramebuffer(GL_FRAMEBUFFER,
                                                              static_cast<GLuint>(previousFramebuffer_));
            }
        }

        ScopedOpenGLDrawState(const ScopedOpenGLDrawState &) = delete;

        ScopedOpenGLDrawState &operator=(const ScopedOpenGLDrawState &) = delete;
    };
#endif

    void MatrixFromValues(Effekseer::Matrix44 &matrix,
                          const float matrixArray0,
                          const float matrixArray1,
                          const float matrixArray2,
                          const float matrixArray3,
                          const float matrixArray4,
                          const float matrixArray5,
                          const float matrixArray6,
                          const float matrixArray7,
                          const float matrixArray8,
                          const float matrixArray9,
                          const float matrixArray10,
                          const float matrixArray11,
                          const float matrixArray12,
                          const float matrixArray13,
                          const float matrixArray14,
                          const float matrixArray15) {
        matrix.Values[0][0] = matrixArray0;
        matrix.Values[1][0] = matrixArray1;
        matrix.Values[2][0] = matrixArray2;
        matrix.Values[3][0] = matrixArray3;
        matrix.Values[0][1] = matrixArray4;
        matrix.Values[1][1] = matrixArray5;
        matrix.Values[2][1] = matrixArray6;
        matrix.Values[3][1] = matrixArray7;
        matrix.Values[0][2] = matrixArray8;
        matrix.Values[1][2] = matrixArray9;
        matrix.Values[2][2] = matrixArray10;
        matrix.Values[3][2] = matrixArray11;
        matrix.Values[0][3] = matrixArray12;
        matrix.Values[1][3] = matrixArray13;
        matrix.Values[2][3] = matrixArray14;
        matrix.Values[3][3] = matrixArray15;
    }

    void MatrixFromValues(Effekseer::Matrix43 &matrix,
                          const float matrixArray0,
                          const float matrixArray1,
                          const float matrixArray2,
                          const float matrixArray3,
                          const float matrixArray4,
                          const float matrixArray5,
                          const float matrixArray6,
                          const float matrixArray7,
                          const float matrixArray8,
                          const float matrixArray9,
                          const float matrixArray10,
                          const float matrixArray11) {
        matrix.Value[0][0] = matrixArray0;
        matrix.Value[1][0] = matrixArray1;
        matrix.Value[2][0] = matrixArray2;
        matrix.Value[3][0] = matrixArray3;

        matrix.Value[0][1] = matrixArray4;
        matrix.Value[1][1] = matrixArray5;
        matrix.Value[2][1] = matrixArray6;
        matrix.Value[3][1] = matrixArray7;

        matrix.Value[0][2] = matrixArray8;
        matrix.Value[1][2] = matrixArray9;
        matrix.Value[2][2] = matrixArray10;
        matrix.Value[3][2] = matrixArray11;
    }

#ifdef __EFFEKSEER_BUILD_VULKAN__
    EffekseerRendererVulkan::RenderPassInformation
    ToVulkanRenderPassInfo(const EffekseerCoreVulkanRenderPassInfo &info) {
        EffekseerRendererVulkan::RenderPassInformation result;
        result.DoesPresentToScreen = info.doesPresentToScreen;
        result.RenderTextureCount = info.renderTextureCount;
        for (int32_t i = 0; i < info.renderTextureCount && i < static_cast<int32_t>(info.renderTextureFormats.size()); i
             ++) {
            result.RenderTextureFormats[i] = static_cast<VkFormat>(info.renderTextureFormats[i]);
        }
        result.DepthFormat = static_cast<VkFormat>(info.depthFormat);
        return result;
    }

    EffekseerRendererVulkan::VulkanImageInfo ToVulkanImageInfo(const uint64_t image, const uint32_t aspect,
                                                               const uint32_t format) {
        EffekseerRendererVulkan::VulkanImageInfo info;
        info.image = reinterpret_cast<VkImage>(image);
        info.aspect = static_cast<VkImageAspectFlags>(aspect);
        info.format = static_cast<VkFormat>(format);
        return info;
    }
#endif

    EffekseerRenderer::DepthReconstructionParameter MakeDepthParameters(
        const EffekseerRenderer::RendererRef &renderer) {
        const auto projMat = renderer->GetProjectionMatrix();

        EffekseerRenderer::DepthReconstructionParameter params;
        params.DepthBufferScale = 1.0f;
        params.DepthBufferOffset = 0.0f;
        params.ProjectionMatrix33 = projMat.Values[2][2];
        params.ProjectionMatrix43 = projMat.Values[2][3];
        params.ProjectionMatrix34 = projMat.Values[3][2];
        params.ProjectionMatrix44 = projMat.Values[3][3];
        return params;
    }

    Effekseer::Manager::DrawParameter MakeLayerDrawParameter(
        const EffekseerRenderer::RendererRef &renderer,
        const int32_t layer) {
        Effekseer::Manager::DrawParameter drawParameter;
        drawParameter.ViewProjectionMatrix = renderer->GetCameraProjectionMatrix();
        drawParameter.CameraPosition = renderer->GetCameraPosition();
        drawParameter.CameraFrontDirection = renderer->GetCameraFrontDirection();
        drawParameter.CameraCullingMask = 1;
        if (layer > 0 && layer < Effekseer::Manager::LayerCount) {
            drawParameter.CameraCullingMask = 1 << layer;
        }
        return drawParameter;
    }
} // namespace

class EffekseerOpenGLLoadStateCore {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    bool hasVertexArray_ = false;
    bool hasPixelStoreRows_ = false;
    GLint previousVertexArray_ = 0;
    GLint previousArrayBuffer_ = 0;
    GLint previousElementArrayBuffer_ = 0;
    GLint previousPackAlignment_ = 4;
    GLint previousUnpackAlignment_ = 4;
    GLint previousUnpackRowLength_ = 0;
    GLint previousUnpackSkipRows_ = 0;
    GLint previousUnpackSkipPixels_ = 0;
#endif

public:
    EffekseerOpenGLLoadStateCore() {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
        hasVertexArray_ = EffekseerRendererGL::GLExt::IsSupportedVertexArray();
        hasPixelStoreRows_ = SupportsOpenGLPixelStoreRows();

        if (hasVertexArray_) {
            glGetIntegerv(GL_VERTEX_ARRAY_BINDING, &previousVertexArray_);
        }
        glGetIntegerv(GL_ARRAY_BUFFER_BINDING, &previousArrayBuffer_);
        glGetIntegerv(GL_ELEMENT_ARRAY_BUFFER_BINDING, &previousElementArrayBuffer_);
        glGetIntegerv(GL_PACK_ALIGNMENT, &previousPackAlignment_);
        glGetIntegerv(GL_UNPACK_ALIGNMENT, &previousUnpackAlignment_);

        if (hasPixelStoreRows_) {
            glGetIntegerv(GL_UNPACK_ROW_LENGTH, &previousUnpackRowLength_);
            glGetIntegerv(GL_UNPACK_SKIP_ROWS, &previousUnpackSkipRows_);
            glGetIntegerv(GL_UNPACK_SKIP_PIXELS, &previousUnpackSkipPixels_);

            glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
        }
#endif
    }

    ~EffekseerOpenGLLoadStateCore() {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
        if (hasVertexArray_) {
            EffekseerRendererGL::GLExt::glBindVertexArray(static_cast<GLuint>(previousVertexArray_));
        }
        EffekseerRendererGL::GLExt::glBindBuffer(GL_ARRAY_BUFFER, static_cast<GLuint>(previousArrayBuffer_));
        EffekseerRendererGL::GLExt::glBindBuffer(
            GL_ELEMENT_ARRAY_BUFFER,
            static_cast<GLuint>(previousElementArrayBuffer_));

        glPixelStorei(GL_PACK_ALIGNMENT, previousPackAlignment_);
        glPixelStorei(GL_UNPACK_ALIGNMENT, previousUnpackAlignment_);
        if (hasPixelStoreRows_) {
            glPixelStorei(GL_UNPACK_ROW_LENGTH, previousUnpackRowLength_);
            glPixelStorei(GL_UNPACK_SKIP_ROWS, previousUnpackSkipRows_);
            glPixelStorei(GL_UNPACK_SKIP_PIXELS, previousUnpackSkipPixels_);
        }
#endif
    }

    EffekseerOpenGLLoadStateCore(const EffekseerOpenGLLoadStateCore &) = delete;

    EffekseerOpenGLLoadStateCore &operator=(const EffekseerOpenGLLoadStateCore &) = delete;
};

EffekseerOpenGLLoadStateCore *BeginEffekseerOpenGLLoadState() {
    return new EffekseerOpenGLLoadStateCore();
}

void EndEffekseerOpenGLLoadState(const EffekseerOpenGLLoadStateCore *state) {
    delete state;
}

// endregion

// region 生命周期

EffekseerManagerCore::~EffekseerManagerCore() {
    commandList_.Reset();
    singleFrameMemoryPool_.Reset();
    backgroundtx_.Reset();
    depthtx_.Reset();
    manager_.Reset();
    renderer_.Reset();
}

void EffekseerManagerCore::ApplyCollisionCallback() const {
    if (manager_ == nullptr) {
        return;
    }

    if (collisionCallback_ == nullptr) {
        manager_->SetCollisionCallback(nullptr);
        return;
    }

    manager_->SetCollisionCallback(
        [this](const Effekseer::Vector3D &startPosition, const Effekseer::Vector3D &endPosition,
               Effekseer::Vector3D &collisionPosition, Effekseer::Vector3D &collisionNormal) -> bool {
            BehemironEffekseerCollisionQuery query = {};
            query.start_x = startPosition.X;
            query.start_y = startPosition.Y;
            query.start_z = startPosition.Z;
            query.end_x = endPosition.X;
            query.end_y = endPosition.Y;
            query.end_z = endPosition.Z;

            BehemironEffekseerCollisionHit hit = {};
            hit.position_x = collisionPosition.X;
            hit.position_y = collisionPosition.Y;
            hit.position_z = collisionPosition.Z;
            hit.normal_x = collisionNormal.X;
            hit.normal_y = collisionNormal.Y;
            hit.normal_z = collisionNormal.Z;

            const int32_t collided = collisionCallback_(&query, &hit, collisionCallbackUserData_);
            collisionPosition.X = hit.position_x;
            collisionPosition.Y = hit.position_y;
            collisionPosition.Z = hit.position_z;
            collisionNormal.X = hit.normal_x;
            collisionNormal.Y = hit.normal_y;
            collisionNormal.Z = hit.normal_z;
            return collided != 0;
        });
}

bool EffekseerManagerCore::Initialize(const int32_t spriteMaxCount, const bool srgbMode) {
    if (manager_ != nullptr || renderer_ != nullptr) {
        return false;
    }

    const auto setting = EffekseerSettingCore::Create(srgbMode);
    if (setting == nullptr) {
        Effekseer::Log(Effekseer::LogType::Error, "Failed to initialize EffekseerManagerCore : GraphicsError");
        return false;
    }

    manager_ = Effekseer::Manager::Create(spriteMaxCount);
    if (manager_ == nullptr) {
        Effekseer::Log(Effekseer::LogType::Error, "Failed to initialize EffekseerManagerCore : ManagerError");
        return false;
    }

    if (EffekseerBackendCore::GetDevice() == EffekseerCoreDeviceType::Vulkan) {
#ifdef __EFFEKSEER_BUILD_VULKAN__
        renderer_ = ::EffekseerRendererVulkan::Create(
            setting->GetGraphicsDevice(),
            ToVulkanRenderPassInfo(EffekseerBackendCore::GetVulkanRenderPassInfo()),
            spriteMaxCount);

        if (renderer_ != nullptr) {
            singleFrameMemoryPool_ = ::EffekseerRenderer::CreateSingleFrameMemoryPool(renderer_->GetGraphicsDevice());
            commandList_ = ::EffekseerRenderer::CreateCommandList(renderer_->GetGraphicsDevice(),
                                                                  singleFrameMemoryPool_);
        }
#endif
    } else if (EffekseerBackendCore::GetDevice() == EffekseerCoreDeviceType::OpenGL) {
#if defined(BEHEMIRON_EFFEKSEER_HAS_GL)
        renderer_ = EffekseerRendererGL::Renderer::Create(setting->GetGraphicsDevice(), spriteMaxCount);
#endif
    }

    if (renderer_ == nullptr) {
        manager_.Reset();
        Effekseer::Log(Effekseer::LogType::Error, "Failed to initialize EffekseerManagerCore : RendererError");
        return false;
    }

    manager_->SetSetting(setting);
    manager_->SetSpriteRenderer(renderer_->CreateSpriteRenderer());
    manager_->SetRibbonRenderer(renderer_->CreateRibbonRenderer());
    manager_->SetRingRenderer(renderer_->CreateRingRenderer());
    manager_->SetTrackRenderer(renderer_->CreateTrackRenderer());
    manager_->SetModelRenderer(renderer_->CreateModelRenderer());
    ApplyCollisionCallback();

    return true;
}

// endregion

// region 更新与播放

void EffekseerManagerCore::BeginUpdate() const {
    if (manager_ == nullptr) {
        return;
    }

    manager_->BeginUpdate();
}

void EffekseerManagerCore::EndUpdate() const {
    if (manager_ == nullptr) {
        return;
    }

    manager_->EndUpdate();
}

void EffekseerManagerCore::UpdateHandleToMoveToFrame(const int handle, const float v) const {
    if (manager_ == nullptr) {
        return;
    }

    manager_->UpdateHandleToMoveToFrame(handle, v);
}

void EffekseerManagerCore::Update(float deltaFrames) {
    if (manager_ == nullptr) {
        return;
    }

    deltaFrames += restDeltaTime_;
    const auto deltaFrameCount = static_cast<int32_t>(deltaFrames);
    restDeltaTime_ = deltaFrames - static_cast<float>(deltaFrameCount);
    for (int32_t loop = 0; loop < deltaFrameCount; loop++) {
        manager_->Update(1);
    }
    if (deltaFrameCount == 0) {
        manager_->Update(0);
    }
}

int EffekseerManagerCore::Play(const EffekseerEffectCore *effect) const {
    if (manager_ == nullptr || effect == nullptr || effect->GetInternal() == nullptr) {
        return -1;
    }

    return manager_->Play(effect->GetInternal(), Effekseer::Vector3D());
}

int EffekseerManagerCore::PlayWithOptions(const EffekseerEffectCore *effect,
                                          const float positionX,
                                          const float positionY,
                                          const float positionZ,
                                          const float rotationX,
                                          const float rotationY,
                                          const float rotationZ,
                                          const float scaleX,
                                          const float scaleY,
                                          const float scaleZ,
                                          const int32_t startFrame) const {
    if (manager_ == nullptr || effect == nullptr || effect->GetInternal() == nullptr) {
        return -1;
    }

    Effekseer::Manager::PlayParameter parameter;
    parameter.Effect = effect->GetInternal();
    parameter.Position = Effekseer::Vector3D(positionX, positionY, positionZ);
    parameter.Rotation = Effekseer::Vector3D(rotationX, rotationY, rotationZ);
    parameter.Scale = Effekseer::Vector3D(scaleX, scaleY, scaleZ);
    parameter.StartFrame = startFrame;
    return manager_->Play(parameter);
}

void EffekseerManagerCore::StopAllEffects() const {
    if (manager_ != nullptr) {
        manager_->StopAllEffects();
    }
}

void EffekseerManagerCore::Stop(const int handle) const {
    if (manager_ != nullptr) {
        manager_->StopEffect(handle);
    }
}

void EffekseerManagerCore::SetPaused(const int handle, const bool v) const {
    if (manager_ != nullptr) {
        manager_->SetPaused(handle, v);
    }
}

void EffekseerManagerCore::SetShown(const int handle, const bool v) const {
    if (manager_ != nullptr) {
        manager_->SetShown(handle, v);
    }
}

void EffekseerManagerCore::SendTrigger(const int handle, const int index) const {
    if (manager_ != nullptr) {
        manager_->SendTrigger(handle, index);
    }
}

// endregion

// region 渲染参数

void EffekseerManagerCore::SetEffectPosition(const int handle, const float x, const float y, const float z) const {
    if (manager_ != nullptr) {
        manager_->SetLocation(handle, x, y, z);
    }
}

void EffekseerManagerCore::SetEffectRotation(const int handle, const float x, const float y, const float z) const {
    if (manager_ != nullptr) {
        manager_->SetRotation(handle, x, y, z);
    }
}

void EffekseerManagerCore::SetEffectScale(const int handle, const float x, const float y, const float z) const {
    if (manager_ != nullptr) {
        manager_->SetScale(handle, x, y, z);
    }
}

void EffekseerManagerCore::SetTransform(const int handle,
                                        const float positionX,
                                        const float positionY,
                                        const float positionZ,
                                        const float rotationX,
                                        const float rotationY,
                                        const float rotationZ,
                                        const float scaleX,
                                        const float scaleY,
                                        const float scaleZ) const {
    if (manager_ == nullptr) {
        return;
    }

    manager_->SetLocation(handle, positionX, positionY, positionZ);
    manager_->SetRotation(handle, rotationX, rotationY, rotationZ);
    manager_->SetScale(handle, scaleX, scaleY, scaleZ);
}

void EffekseerManagerCore::SetLayerParameter(const int layer,
                                             const float viewerPosX,
                                             const float viewerPosY,
                                             const float viewerPosZ,
                                             const float distanceBias) const {
    if (manager_ == nullptr) {
        return;
    }

    Effekseer::Manager::LayerParameter layerParameter;
    layerParameter.ViewerPosition = Effekseer::Vector3D(viewerPosX, viewerPosY, viewerPosZ);
    layerParameter.DistanceBias = distanceBias;
    manager_->SetLayerParameter(layer, layerParameter);
}

void EffekseerManagerCore::SetEffectTransformMatrix(const int handle,
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
                                                    const float v11) const {
    if (manager_ == nullptr) {
        return;
    }

    auto m = Effekseer::Matrix43();
    MatrixFromValues(m, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
    manager_->SetMatrix(handle, m);
}

void EffekseerManagerCore::SetEffectTransformBaseMatrix(const int handle,
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
                                                        const float v11) const {
    if (manager_ == nullptr) {
        return;
    }

    auto m = Effekseer::Matrix43();
    MatrixFromValues(m, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11);
    manager_->SetBaseMatrix(handle, m);
}

void EffekseerManagerCore::DrawLayer(const int layer, const bool drawBack, const bool drawFront) const {
    if (manager_ == nullptr || renderer_ == nullptr || (!drawBack && !drawFront)) {
        return;
    }

    if (!renderer_->BeginRendering()) {
        return;
    }

    const auto drawParameter = MakeLayerDrawParameter(renderer_, layer);
    if (drawBack) {
        manager_->DrawBack(drawParameter);
    }
    if (drawFront) {
        manager_->DrawFront(drawParameter);
    }
    renderer_->EndRendering();
}

void EffekseerManagerCore::SetLayer(const int handle, const int layer) const {
    if (manager_ == nullptr) {
        return;
    }

    manager_->SetLayer(handle, layer);
}

void EffekseerManagerCore::SetCameraParameter(const float frontX,
                                              const float frontY,
                                              const float frontZ,
                                              const float posX,
                                              const float posY,
                                              const float posZ) const {
    if (manager_ == nullptr || renderer_ == nullptr) {
        return;
    }

    const auto pos = Effekseer::Vector3D(posX, posY, posZ);
    const auto front = Effekseer::Vector3D(frontX, frontY, frontZ);
    renderer_->SetCameraParameter(front, pos);
}

void EffekseerManagerCore::SetProjectionMatrix(const float v0,
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
                                               const float v11,
                                               const float v12,
                                               const float v13,
                                               const float v14,
                                               const float v15) const {
    if (manager_ == nullptr || renderer_ == nullptr) {
        return;
    }

    auto m = Effekseer::Matrix44();
    MatrixFromValues(m, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
    renderer_->SetProjectionMatrix(m);
}

void EffekseerManagerCore::SetCameraMatrix(const float v0,
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
                                           const float v11,
                                           const float v12,
                                           const float v13,
                                           const float v14,
                                           const float v15) const {
    if (manager_ == nullptr || renderer_ == nullptr) {
        return;
    }

    auto m = Effekseer::Matrix44();
    MatrixFromValues(m, v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15);
    renderer_->SetCameraMatrix(m);
}

void EffekseerManagerCore::SetViewProjectionMatrixWithSimpleWindow(const int32_t windowWidth,
                                                                   const int32_t windowHeight) const {
    if (manager_ == nullptr || renderer_ == nullptr) {
        return;
    }

    renderer_->SetProjectionMatrix(
        Effekseer::Matrix44().OrthographicRH(static_cast<float>(windowWidth), static_cast<float>(windowHeight), 1.0f,
                                               400.0f));

    renderer_->SetCameraMatrix(Effekseer::Matrix44().LookAtRH(
        Effekseer::Vector3D(windowWidth / 2.0f, windowHeight / 2.0f, 200.0f),
        Effekseer::Vector3D(windowWidth / 2.0f, windowHeight / 2.0f, -200.0f),
        Effekseer::Vector3D(0.0f, 1.0f, 0.0f)));
}

void EffekseerManagerCore::SetDynamicInput(const int handle, const int32_t index, const float value) const {
    if (manager_ != nullptr) {
        manager_->SetDynamicInput(handle, index, value);
    }
}

float EffekseerManagerCore::GetDynamicInput(const int handle, const int32_t index) const {
    return manager_ == nullptr ? 0.0f : manager_->GetDynamicInput(handle, index);
}

void EffekseerManagerCore::SetCollisionCallback(BehemironEffekseerCollisionCallback callback, void *userData) {
    collisionCallback_ = callback;
    collisionCallbackUserData_ = userData;
    ApplyCollisionCallback();
}

void EffekseerManagerCore::LaunchWorkerThreads(const int32_t n) const {
    if (manager_ != nullptr) {
        manager_->LaunchWorkerThreads(n);
    }
}

// endregion

// region Vulkan 绑定

void EffekseerManagerCore::SetBackground(const uint32_t glid, const bool hasMipmap) {
#if !defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    (void) glid;
    (void) hasMipmap;
    return;
#else
    if (renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::OpenGL) {
        return;
    }

    if (backgroundtx_ == nullptr) {
        backgroundtx_ = EffekseerRendererGL::CreateTexture(renderer_->GetGraphicsDevice(), glid, hasMipmap, nullptr);
    }

    const auto texture = static_cast<EffekseerRendererGL::Backend::Texture *>(backgroundtx_.Get());
    texture->Init(glid, hasMipmap, nullptr);
    renderer_->SetBackground(backgroundtx_);
#endif
}

void EffekseerManagerCore::SetBackgroundVulkan(const uint64_t image, const uint32_t aspect, const uint32_t format) {
#ifndef __EFFEKSEER_BUILD_VULKAN__
    (void) image;
    (void) aspect;
    (void) format;
#else
    if (renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::Vulkan || image == 0) {
        return;
    }

    backgroundtx_ = ::EffekseerRendererVulkan::CreateTexture(renderer_->GetGraphicsDevice(),
                                                             ToVulkanImageInfo(image, aspect, format));
    renderer_->SetBackground(backgroundtx_);
#endif
}

void EffekseerManagerCore::UnsetBackground() const {
    if (renderer_ != nullptr) {
        renderer_->SetBackground(nullptr);
    }
}

void EffekseerManagerCore::SetDepth(const uint32_t glid, const bool hasMipmap) {
#if !defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    (void) glid;
    (void) hasMipmap;
    return;
#else
    if (renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::OpenGL) {
        return;
    }

    if (depthtx_ == nullptr) {
        depthtx_ = EffekseerRendererGL::CreateTexture(renderer_->GetGraphicsDevice(), glid, hasMipmap, nullptr);
    }

    if (depthtx_ != nullptr) {
        const auto texture = static_cast<EffekseerRendererGL::Backend::Texture *>(depthtx_.Get());
        texture->Init(glid, hasMipmap, nullptr);
    }

    renderer_->SetDepth(depthtx_, MakeDepthParameters(renderer_));
#endif
}

void EffekseerManagerCore::SetDepthVulkan(const uint64_t image, const uint32_t aspect, const uint32_t format) {
#ifndef __EFFEKSEER_BUILD_VULKAN__
    (void) image;
    (void) aspect;
    (void) format;
    return;
#else
    if (renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::Vulkan || image == 0) {
        return;
    }

    depthtx_ = ::EffekseerRendererVulkan::CreateTexture(renderer_->GetGraphicsDevice(),
                                                        ToVulkanImageInfo(image, aspect, format));
    renderer_->SetDepth(depthtx_, MakeDepthParameters(renderer_));
#endif
}

void EffekseerManagerCore::UnsetDepth() const {
    if (renderer_ == nullptr) {
        return;
    }

    constexpr EffekseerRenderer::DepthReconstructionParameter params;
    renderer_->SetDepth(nullptr, params);
}

void EffekseerManagerCore::RenderOpenGLFrame(const uint32_t targetFbo,
                                             const int32_t width,
                                             const int32_t height,
                                             const uint32_t backgroundGlid,
                                             const bool backgroundHasMipmap,
                                             const uint32_t depthGlid,
                                             const bool depthHasMipmap,
                                             const int32_t layer,
                                             const bool drawBack,
                                             const bool drawFront) {
#if !defined(BEHEMIRON_EFFEKSEER_HAS_GL)
    (void) targetFbo;
    (void) width;
    (void) height;
    (void) backgroundGlid;
    (void) backgroundHasMipmap;
    (void) depthGlid;
    (void) depthHasMipmap;
    (void) layer;
    (void) drawBack;
    (void) drawFront;
#else
    if (manager_ == nullptr || renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::OpenGL ||
        width <= 0 || height <= 0) {
        return;
    }

    ScopedOpenGLDrawState state(targetFbo, width, height);

    if (backgroundGlid == 0) {
        UnsetBackground();
    } else {
        SetBackground(backgroundGlid, backgroundHasMipmap);
    }

    if (depthGlid == 0) {
        UnsetDepth();
    } else {
        SetDepth(depthGlid, depthHasMipmap);
    }

    DrawLayer(layer, drawBack, drawFront);
#endif
}

void EffekseerManagerCore::RenderVulkanFrame(const uint64_t nativeCommandBuffer,
                                             const uint64_t backgroundImage,
                                             const uint32_t backgroundAspect,
                                             const uint32_t backgroundFormat,
                                             const uint64_t depthImage,
                                             const uint32_t depthAspect,
                                             const uint32_t depthFormat,
                                             const int32_t layer,
                                             const bool drawBack,
                                             const bool drawFront) {
#ifndef __EFFEKSEER_BUILD_VULKAN__
    (void) nativeCommandBuffer;
    (void) backgroundImage;
    (void) backgroundAspect;
    (void) backgroundFormat;
    (void) depthImage;
    (void) depthAspect;
    (void) depthFormat;
    (void) layer;
    (void) drawBack;
    (void) drawFront;
#else
    if (manager_ == nullptr || renderer_ == nullptr || EffekseerBackendCore::GetDevice() != EffekseerCoreDeviceType::Vulkan ||
        commandList_ == nullptr || singleFrameMemoryPool_ == nullptr || nativeCommandBuffer == 0) {
        return;
    }

    singleFrameMemoryPool_->NewFrame();
    ::EffekseerRendererVulkan::BeginCommandList(commandList_, reinterpret_cast<VkCommandBuffer>(nativeCommandBuffer));
    renderer_->SetCommandList(commandList_);

    if (backgroundImage == 0) {
        UnsetBackground();
    } else {
        SetBackgroundVulkan(backgroundImage, backgroundAspect, backgroundFormat);
    }

    if (depthImage == 0) {
        UnsetDepth();
    } else {
        SetDepthVulkan(depthImage, depthAspect, depthFormat);
    }

    DrawLayer(layer, drawBack, drawFront);
    renderer_->SetCommandList(nullptr);
    ::EffekseerRendererVulkan::EndCommandList(commandList_);
#endif
}

// endregion

// region 查询

bool EffekseerManagerCore::Exists(const int handle) const {
    return manager_ != nullptr && manager_->Exists(handle);
}

int EffekseerManagerCore::GetInstanceCount(const int handle) const {
    return manager_ == nullptr ? 0 : manager_->GetInstanceCount(handle);
}

int EffekseerManagerCore::GetTotalInstanceCount() const {
    return manager_ == nullptr ? 0 : manager_->GetTotalInstanceCount();
}

int EffekseerManagerCore::GetRestInstanceCount() const {
    return manager_ == nullptr ? 0 : manager_->GetRestInstancesCount();
}

int EffekseerManagerCore::GetUpdateTime() const {
    return manager_ == nullptr ? 0 : manager_->GetUpdateTime();
}

int EffekseerManagerCore::GetDrawTime() const {
    return manager_ == nullptr ? 0 : manager_->GetDrawTime();
}

int EffekseerManagerCore::GetGpuTime() const {
    return manager_ == nullptr ? 0 : manager_->GetGpuTime();
}

int EffekseerManagerCore::GetDrawCallCount() const {
    return renderer_ == nullptr ? 0 : renderer_->GetDrawCallCount();
}

int EffekseerManagerCore::GetDrawVertexCount() const {
    return renderer_ == nullptr ? 0 : renderer_->GetDrawVertexCount();
}

// endregion
