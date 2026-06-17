cmake_minimum_required(VERSION 3.15)

project(EffekseerCore C CXX)

find_package(Threads REQUIRED)

add_library(
  EffekseerCore
  STATIC
  EffekseerBackendCore.h
  EffekseerBackendCore.cpp
  EffekseerEffectCore.h
  EffekseerEffectCore.cpp
  EffekseerManagerCore.h
  EffekseerManagerCore.cpp
)

target_include_directories(
  EffekseerCore
  PUBLIC
  ../Effekseer/Dev/Cpp/Effekseer/
)

target_link_libraries(
  EffekseerCore
  PUBLIC
  Effekseer
)

if(BUILD_GL)
  target_include_directories(
    EffekseerCore
    PUBLIC
    ../Effekseer/Dev/Cpp/EffekseerRendererGL/
  )
  target_link_libraries(
    EffekseerCore
    PUBLIC
    EffekseerRendererGL
  )
  target_compile_definitions(
    EffekseerCore
    PUBLIC
    BEHEMIRON_EFFEKSEER_HAS_GL=1
  )
  if(USE_OPENGLES2)
    target_compile_definitions(
      EffekseerCore
      PUBLIC
      __EFFEKSEER_RENDERER_GLES2__
    )
  endif()
  if(USE_OPENGLES3)
    target_compile_definitions(
      EffekseerCore
      PUBLIC
      __EFFEKSEER_RENDERER_GLES3__
    )
  endif()
  if(USE_OPENGL3)
    target_compile_definitions(
      EffekseerCore
      PUBLIC
      __EFFEKSEER_RENDERER_GL3__
    )
  endif()
endif()

if(BUILD_VULKAN)
  find_package(Vulkan REQUIRED)
  target_include_directories(
    EffekseerCore
    PUBLIC
    ../Effekseer/Dev/Cpp/EffekseerRendererLLGI/
    ../Effekseer/Dev/Cpp/EffekseerRendererVulkan/
    ../Effekseer/Dev/Cpp/3rdParty/LLGI/src
    ${Vulkan_INCLUDE_DIRS}
  )
  target_link_libraries(
    EffekseerCore
    PUBLIC
    EffekseerRendererLLGI
    EffekseerRendererVulkan
    ${Vulkan_LIBRARIES}
  )
  target_compile_definitions(
    EffekseerCore
    PUBLIC
    __EFFEKSEER_BUILD_VULKAN__
  )
endif()

target_compile_features(EffekseerCore PUBLIC cxx_std_17)

if(ANDROID)
  if(BUILD_GL)
    target_link_libraries(
      EffekseerCore
      PUBLIC
      EGL
      GLESv3
    )
  endif()
  target_link_libraries(
    EffekseerCore
    PUBLIC
    log
  )
elseif(BUILD_GL AND APPLE)
  find_library(OPENGL_FRAMEWORK OpenGL)
  target_link_libraries(
    EffekseerCore
    PUBLIC
    ${OPENGL_FRAMEWORK}
  )
elseif(BUILD_GL AND UNIX)
  find_package(OpenGL REQUIRED)
  target_link_libraries(
    EffekseerCore
    PUBLIC
    ${OPENGL_LIBRARIES}
  )
endif()
