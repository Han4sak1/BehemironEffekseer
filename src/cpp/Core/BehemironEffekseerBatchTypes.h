#pragma once

#include <stdint.h>

// region Effect 依赖类型

typedef enum BehemironEffekseerEffectDependencyType {
    BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_COLOR = 0,
    BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_NORMAL = 1,
    BEHEMIRON_EFFEKSEER_DEPENDENCY_TEXTURE_DISTORTION = 2,
    BEHEMIRON_EFFEKSEER_DEPENDENCY_MODEL = 3,
    BEHEMIRON_EFFEKSEER_DEPENDENCY_MATERIAL = 4,
    BEHEMIRON_EFFEKSEER_DEPENDENCY_CURVE = 5,
} BehemironEffekseerEffectDependencyType;

// endregion

// region Effect 依赖项

typedef struct BehemironEffekseerEffectDependency {
    int32_t type;
    int32_t slot;
    const uint16_t* path_utf16;
} BehemironEffekseerEffectDependency;

// endregion

// region Typed 播放与批量结构

typedef struct BehemironEffekseerPlayOptions {
    float position_x;
    float position_y;
    float position_z;
    float rotation_x;
    float rotation_y;
    float rotation_z;
    float scale_x;
    float scale_y;
    float scale_z;
    int32_t start_frame;
} BehemironEffekseerPlayOptions;

typedef struct BehemironEffekseerBatchPlayItem {
    void* effect_handle;
    BehemironEffekseerPlayOptions options;
} BehemironEffekseerBatchPlayItem;

typedef struct BehemironEffekseerBatchTransformItem {
    int32_t effect_handle;
    float position_x;
    float position_y;
    float position_z;
    float rotation_x;
    float rotation_y;
    float rotation_z;
    float scale_x;
    float scale_y;
    float scale_z;
} BehemironEffekseerBatchTransformItem;

typedef struct BehemironEffekseerBatchDynamicInputItem {
    int32_t effect_handle;
    int32_t input_index;
    float value;
} BehemironEffekseerBatchDynamicInputItem;

// endregion

// region Diagnostics 结构

typedef struct BehemironEffekseerManagerStats {
    int32_t total_instance_count;
    int32_t rest_instance_count;
    int32_t update_time;
    int32_t draw_time;
    int32_t gpu_time;
    int32_t draw_call_count;
    int32_t draw_vertex_count;
} BehemironEffekseerManagerStats;

typedef void(*BehemironEffekseerLogCallback)(
    int32_t log_type,
    const char* message,
    void* user_data
);

// endregion
