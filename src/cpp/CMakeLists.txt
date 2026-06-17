
project(cpp)

set(BUILD_VIEWER OFF CACHE BOOL "" FORCE)
set(BUILD_EDITOR OFF CACHE BOOL "" FORCE)
set(BUILD_TEST OFF CACHE BOOL "" FORCE)
set(BUILD_TOOLS OFF CACHE BOOL "" FORCE)
set(BUILD_TOOL OFF CACHE BOOL "" FORCE)
set(BUILD_EXAMPLES OFF)
set(BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_METAL OFF CACHE BOOL "" FORCE)
set(BUILD_WEBGPU OFF CACHE BOOL "" FORCE)
set(BUILD_VULKAN_COMPILER OFF CACHE BOOL "" FORCE)
set(NETWORK_ENABLED OFF CACHE BOOL "" FORCE)
set(USE_OPENAL OFF CACHE BOOL "" FORCE)
set(USE_XAUDIO2 OFF CACHE BOOL "" FORCE)
set(USE_DSOUND OFF CACHE BOOL "" FORCE)
set(USE_OSM OFF CACHE BOOL "" FORCE)
add_subdirectory(Effekseer)
add_subdirectory(Core)

# Match the vendored LLGI path casing so Linux can resolve it.
set(LLGI_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/Effekseer/Dev/Cpp/3rdParty/LLGI/src)
set(BEHEMIRON_EFFEKSEER_VULKAN_PLATFORM_DEFINITIONS)
if(BUILD_VULKAN)
  if(APPLE)
    list(APPEND BEHEMIRON_EFFEKSEER_VULKAN_PLATFORM_DEFINITIONS
         VK_ENABLE_BETA_EXTENSIONS
         VK_USE_PLATFORM_MACOS_MVK)
  elseif(UNIX AND NOT ANDROID)
    list(APPEND BEHEMIRON_EFFEKSEER_VULKAN_PLATFORM_DEFINITIONS
         VK_USE_PLATFORM_XCB_KHR)
  endif()
endif()

foreach(LLGI_CONSUMER
        EffekseerRendererLLGI
        EffekseerRendererVulkan
        EffekseerRendererDX12
        EffekseerRendererMetal)
  if(TARGET ${LLGI_CONSUMER})
    target_include_directories(
      ${LLGI_CONSUMER}
      PRIVATE
      ${LLGI_SOURCE_DIR}
    )
  endif()
endforeach()

# EffekseerRendererVulkan's public headers include vulkan.h before their own
# platform setup on non-Windows targets, so add the required surface defines
# only to the targets whose translation units include those headers directly.
if(BEHEMIRON_EFFEKSEER_VULKAN_PLATFORM_DEFINITIONS)
  foreach(VULKAN_TARGET
          EffekseerRendererVulkan
          EffekseerCore)
    if(TARGET ${VULKAN_TARGET})
      target_compile_definitions(
        ${VULKAN_TARGET}
        PRIVATE
        ${BEHEMIRON_EFFEKSEER_VULKAN_PLATFORM_DEFINITIONS}
      )
    endif()
  endforeach()
endif()

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

if(BUILD_VULKAN)
  target_link_libraries(
    BehemironEffekseerNative
    PRIVATE
    glslang
    glslang-default-resource-limits
    SPIRV
    OSDependent
  )
endif()

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
