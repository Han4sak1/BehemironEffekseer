#pragma once

#include "EffekseerBackendCore.h"

// region 纹理类型

enum class EffekseerTextureType {
    Color,
    Normal,
    Distortion,
};

// endregion

// region Effect 封装

class EffekseerEffectCore {
    Effekseer::EffectRef effect_ = nullptr;

    int32_t GetTextureDependencyCount() const;

    const char16_t *GetTextureDependencyPath(int32_t index, EffekseerTextureType type) const;

public:
    EffekseerEffectCore();

    ~EffekseerEffectCore();

    // 仅加载 efk 二进制本体，外部资源通过后续接口按需注入。
    bool Load(const unsigned char *data, int len, float magnification);

    const char16_t *GetTexturePath(int32_t index, EffekseerTextureType type) const;

    int32_t GetTextureCount(EffekseerTextureType type) const;

    bool LoadTexture(const unsigned char *data, int len, int32_t index, EffekseerTextureType type) const;

    bool HasTextureLoaded(int32_t index, EffekseerTextureType type) const;

    const char16_t *GetModelPath(int32_t index) const;

    int32_t GetModelCount() const;

    bool LoadModel(const unsigned char *data, int len, int32_t index) const;

    bool HasModelLoaded(int32_t index) const;

    const char16_t *GetMaterialPath(int32_t index) const;

    int32_t GetMaterialCount() const;

    bool LoadMaterial(const unsigned char *data, int len, int32_t index) const;

    const char16_t *GetCurvePath(int32_t index) const;

    bool HasMaterialLoaded(int32_t index) const;

    int32_t GetCurveCount() const;

    bool LoadCurve(const unsigned char *data, int len, int32_t index) const;

    bool HasCurveLoaded(int32_t index) const;

    int32_t GetDependencyCount() const;

    bool GetDependency(int32_t index, int32_t &type, int32_t &slot, const char16_t *&path) const;

    Effekseer::EffectRef GetInternal() const;

    int32_t GetTermMax() const;

    int32_t GetTermMin() const;
};

// endregion
