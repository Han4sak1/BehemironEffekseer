#pragma once

#include <stdint.h>

// region 碰撞查询类型

typedef struct BehemironEffekseerCollisionQuery {
    float start_x;
    float start_y;
    float start_z;
    float end_x;
    float end_y;
    float end_z;
} BehemironEffekseerCollisionQuery;

typedef struct BehemironEffekseerCollisionHit {
    float position_x;
    float position_y;
    float position_z;
    float normal_x;
    float normal_y;
    float normal_z;
    float distance;
    uint32_t user_flags;
} BehemironEffekseerCollisionHit;

typedef int32_t (*BehemironEffekseerCollisionCallback)(
    const BehemironEffekseerCollisionQuery* query,
    BehemironEffekseerCollisionHit* hit,
    void* user_data
);

// endregion
