#include "EffekseerEffectCore.h"

#include <assert.h>

#include "BehemironEffekseerBatchTypes.h"

// region 工具函数

namespace
{
Effekseer::TextureType ToTextureType(const EffekseerTextureType type)
{
	return static_cast<Effekseer::TextureType>(type);
}

int32_t ToDependencyType(const EffekseerTextureType type)
{
	switch (type)
	{
	case EffekseerTextureType::Normal:
		return BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_NORMAL;
	case EffekseerTextureType::Distortion:
		return BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_DISTORTION;
	case EffekseerTextureType::Color:
	default:
		return BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_COLOR;
	}
}
}

// endregion

// region 依赖视图

int32_t EffekseerEffectCore::GetTextureDependencyCount() const
{
	return GetTextureCount(EffekseerTextureType::Color)
		+ GetTextureCount(EffekseerTextureType::Normal)
		+ GetTextureCount(EffekseerTextureType::Distortion);
}

const char16_t* EffekseerEffectCore::GetTextureDependencyPath(const int32_t index, const EffekseerTextureType type) const
{
	return GetTexturePath(index, type);
}

int32_t EffekseerEffectCore::GetDependencyCount() const
{
	if (effect_ == nullptr)
	{
		return 0;
	}

	return GetTextureDependencyCount()
		+ GetModelCount()
		+ GetMaterialCount()
		+ GetCurveCount();
}

bool EffekseerEffectCore::GetDependency(const int32_t index, int32_t& type, int32_t& slot, const char16_t*& path) const
{
	if (effect_ == nullptr || index < 0)
	{
		return false;
	}

	int32_t cursor = index;
	constexpr EffekseerTextureType textureTypes[] = {
		EffekseerTextureType::Color,
		EffekseerTextureType::Normal,
		EffekseerTextureType::Distortion,
	};

	for (const auto textureType : textureTypes)
	{
		const auto count = GetTextureCount(textureType);
		if (cursor < count)
		{
			type = ToDependencyType(textureType);
			slot = cursor;
			path = GetTextureDependencyPath(cursor, textureType);
			return true;
		}
		cursor -= count;
	}

	const auto modelCount = GetModelCount();
	if (cursor < modelCount)
	{
		type = BEHEMIRON_EFFEKSEER_DEPENDENCY_MODEL;
		slot = cursor;
		path = GetModelPath(cursor);
		return true;
	}
	cursor -= modelCount;

	const auto materialCount = GetMaterialCount();
	if (cursor < materialCount)
	{
		type = BEHEMIRON_EFFEKSEER_DEPENDENCY_MATERIAL;
		slot = cursor;
		path = GetMaterialPath(cursor);
		return true;
	}
	cursor -= materialCount;

	if (const auto curveCount = GetCurveCount(); cursor < curveCount)
	{
		type = BEHEMIRON_EFFEKSEER_DEPENDENCY_CURVE;
		slot = cursor;
		path = GetCurvePath(cursor);
		return true;
	}

	return false;
}

// endregion

// region 生命周期

EffekseerEffectCore::EffekseerEffectCore() = default;

EffekseerEffectCore::~EffekseerEffectCore() = default;

// endregion

// region Effect 本体

bool EffekseerEffectCore::Load(const unsigned char* data, const int len, const float magnification)
{
	if (data == nullptr || len <= 0)
	{
		return false;
	}

	// 中文注释：这里只创建 Effect，本地资源路径解析交给外层自行处理。
	effect_ = ::Effekseer::Effect::Create(
		EffekseerSettingCore::Create().DownCast<Effekseer::Setting>(),
		const_cast<void*>(static_cast<const void*>(data)),
		len,
		magnification);
	return effect_ != nullptr;
}

Effekseer::EffectRef EffekseerEffectCore::GetInternal() const
{
	return effect_;
}

int32_t EffekseerEffectCore::GetTermMax() const
{
	if (effect_ == nullptr)
	{
		return 0;
	}

	Effekseer::EffectTerm t = effect_->CalculateTerm();
	return t.TermMax;
}

int32_t EffekseerEffectCore::GetTermMin() const
{
	if (effect_ == nullptr)
	{
		return 0;
	}

	Effekseer::EffectTerm t = effect_->CalculateTerm();
	return t.TermMin;
}

// endregion

// region 纹理资源

const char16_t* EffekseerEffectCore::GetTexturePath(const int32_t index, const EffekseerTextureType type) const
{
	if (effect_ == nullptr)
	{
		return nullptr;
	}

	if (type == EffekseerTextureType::Color)
	{
		return effect_->GetColorImagePath(index);
	}
	if (type == EffekseerTextureType::Normal)
	{
		return effect_->GetNormalImagePath(index);
	}
	if (type == EffekseerTextureType::Distortion)
	{
		return effect_->GetDistortionImagePath(index);
	}

	assert(0);
	return nullptr;
}

int32_t EffekseerEffectCore::GetTextureCount(const EffekseerTextureType type) const
{
	if (effect_ == nullptr)
	{
		return 0;
	}

	if (type == EffekseerTextureType::Color)
	{
		return effect_->GetColorImageCount();
	}
	if (type == EffekseerTextureType::Normal)
	{
		return effect_->GetNormalImageCount();
	}
	if (type == EffekseerTextureType::Distortion)
	{
		return effect_->GetDistortionImageCount();
	}

	assert(0);
	return 0;
}

bool EffekseerEffectCore::LoadTexture(const unsigned char* data,
                                      const int len,
                                      const int32_t index,
                                      const EffekseerTextureType type) const {
	if (effect_ == nullptr || data == nullptr || len <= 0)
	{
		return false;
	}

	const auto loader = effect_->GetSetting()->GetTextureLoader();
	if (loader == nullptr)
	{
		return false;
	}

	const auto texture = loader->Load(data, len, ToTextureType(type), true);
	if (texture == nullptr)
	{
		return false;
	}

	effect_->SetTexture(index, ToTextureType(type), texture);
	return true;
}

bool EffekseerEffectCore::HasTextureLoaded(const int32_t index, const EffekseerTextureType type) const
{
	if (effect_ == nullptr)
	{
		return false;
	}

	if (type == EffekseerTextureType::Color)
	{
		return effect_->GetColorImage(index) != nullptr;
	}
	if (type == EffekseerTextureType::Normal)
	{
		return effect_->GetNormalImage(index) != nullptr;
	}
	if (type == EffekseerTextureType::Distortion)
	{
		return effect_->GetDistortionImage(index) != nullptr;
	}

	return false;
}

// endregion

// region 模型资源

const char16_t* EffekseerEffectCore::GetModelPath(const int32_t index) const
{
	return effect_ == nullptr ? nullptr : effect_->GetModelPath(index);
}

int32_t EffekseerEffectCore::GetModelCount() const
{
	return effect_ == nullptr ? 0 : effect_->GetModelCount();
}

bool EffekseerEffectCore::LoadModel(const unsigned char* data, const int len, const int32_t index) const {
	if (effect_ == nullptr || data == nullptr || len <= 0)
	{
		return false;
	}

	const auto loader = effect_->GetSetting()->GetModelLoader();
	if (loader == nullptr)
	{
		return false;
	}

	const auto model = loader->Load(data, len);
	if (model == nullptr)
	{
		return false;
	}

	effect_->SetModel(index, model);
	return true;
}

bool EffekseerEffectCore::HasModelLoaded(const int32_t index) const
{
	return effect_ != nullptr && effect_->GetModel(index) != nullptr;
}

// endregion

// region 材质与曲线资源

const char16_t* EffekseerEffectCore::GetMaterialPath(const int32_t index) const
{
	return effect_ == nullptr ? nullptr : effect_->GetMaterialPath(index);
}

int32_t EffekseerEffectCore::GetMaterialCount() const
{
	return effect_ == nullptr ? 0 : effect_->GetMaterialCount();
}

bool EffekseerEffectCore::LoadMaterial(const unsigned char* data, const int len, const int32_t index) const {
	if (effect_ == nullptr || data == nullptr || len <= 0)
	{
		return false;
	}

	const auto loader = effect_->GetSetting()->GetMaterialLoader();
	if (loader == nullptr)
	{
		return false;
	}

	const auto material = loader->Load(data, len, Effekseer::MaterialFileType::Code);
	if (material == nullptr)
	{
		return false;
	}

	effect_->SetMaterial(index, material);
	return true;
}

bool EffekseerEffectCore::HasMaterialLoaded(const int32_t index) const
{
	return effect_ != nullptr && effect_->GetMaterial(index) != nullptr;
}

const char16_t* EffekseerEffectCore::GetCurvePath(const int32_t index) const
{
	return effect_ == nullptr ? nullptr : effect_->GetCurvePath(index);
}

int32_t EffekseerEffectCore::GetCurveCount() const
{
	return effect_ == nullptr ? 0 : effect_->GetCurveCount();
}

bool EffekseerEffectCore::LoadCurve(const unsigned char* data, const int len, const int32_t index) const {
	if (effect_ == nullptr || data == nullptr || len <= 0)
	{
		return false;
	}

	const auto loader = effect_->GetSetting()->GetCurveLoader();
	if (loader == nullptr)
	{
		return false;
	}

	const auto curve = loader->Load(data, len);
	if (curve == nullptr)
	{
		return false;
	}

	effect_->SetCurve(index, curve);
	return true;
}

bool EffekseerEffectCore::HasCurveLoaded(const int32_t index) const
{
	return effect_ != nullptr && effect_->GetCurve(index) != nullptr;
}

// endregion
