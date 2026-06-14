# Behemiron Effekseer Java Binding

项目现状：
- Effekseer 1.80.4
- 稳定的 `C ABI`
- `JavaCPP` Java 桥接（Java17+）
- 面向 `Windows / Linux / macOS / Android arm64` 的 CI 构建

相比较于 [EffekseerForMultiLanguages](https://github.com/effekseer/EffekseerForMultiLanguages) 项目，本项目只维护 JavaCPP 构造的 Java 绑定

ABI:
- Windows / Linux / MacOS AMD64
- MacOS AARCH64
- Android AARCH64

图形API:
- OpenGL3（Android时为OpenGLES3）
- Vulkan

## 目录

- `src/cpp/Core/`
  - C++ core 封装
  - `BehemironEffekseerCAPI.*` 只在最终 shared library 中导出，作为稳定 ABI
- `src/main/java/`
  - JavaCPP preset 与 Java API
- `platform/`
  - 聚合所有 classifier 的 platform artifact
- `cppbuild.sh`
  - 安装 native 头文件与库到 `cppbuild/<platform>/`
- `CMakePresets.json`
  - 本地与 CI 的统一 CMake 入口

# Licenses
- [effekseer/Effekseer](https://github.com/effekseer/Effekseer) : MIT License
- [bytedeco/javacpp](https://github.com/bytedeco/javacpp) : Apache 2.0 License
