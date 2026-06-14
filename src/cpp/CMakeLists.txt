
project(cpp)

set(BUILD_VIEWER OFF CACHE BOOL "" FORCE)
set(BUILD_EDITOR OFF CACHE BOOL "" FORCE)
set(BUILD_TEST OFF CACHE BOOL "" FORCE)
set(BUILD_TOOLS OFF CACHE BOOL "" FORCE)
set(BUILD_EXAMPLES OFF)
set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_METAL OFF CACHE BOOL "" FORCE)
set(BUILD_WEBGPU OFF CACHE BOOL "" FORCE)
set(NETWORK_ENABLED OFF CACHE BOOL "" FORCE)
set(USE_OPENAL OFF CACHE BOOL "" FORCE)
set(USE_XAUDIO2 OFF CACHE BOOL "" FORCE)
set(USE_DSOUND OFF CACHE BOOL "" FORCE)
set(USE_OSM OFF CACHE BOOL "" FORCE)
add_subdirectory(Effekseer)
add_subdirectory(Core)

add_library(BehemironEffekseerNative SHARED)

set_target_properties(
  BehemironEffekseerNative
  PROPERTIES
  OUTPUT_NAME behemiron_effekseer
)

if(WIN32)
  set_target_properties(
    BehemironEffekseerNative
    PROPERTIES
    PREFIX ""
  )
endif()

target_link_libraries(
  BehemironEffekseerNative
  PRIVATE
  EffekseerCore
)

target_include_directories(
  BehemironEffekseerNative
  PRIVATE
  Effekseer/Dev/Cpp/Effekseer/
  Effekseer/Dev/Cpp/EffekseerRendererGL/
  Effekseer/Dev/Cpp/EffekseerRendererLLGI/
  Effekseer/Dev/Cpp/EffekseerRendererVulkan/
)

target_sources(
  BehemironEffekseerNative
  PRIVATE
  Core/BehemironEffekseerCAPI.cpp
)

target_compile_definitions(
  BehemironEffekseerNative
  PRIVATE
  BEHEMIRON_EFFEKSEER_BUILD_SHARED=1
)

target_compile_features(BehemironEffekseerNative PUBLIC cxx_std_17)
