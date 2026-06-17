# Behemiron Effekseer Java 绑定

[English](README.md)

[![Maven Central](https://img.shields.io/maven-central/v/com.behemiron.engine/behemiron-effekseer)](https://repo1.maven.org/maven2/com/behemiron/engine/behemiron-effekseer/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/license/mit)

这是 Behemiron Effekseer Runtime 的 Java 绑定项目。它基于稳定的 C ABI，并使用 JavaCPP 构建 Java 层与平台原生库的装配。

## 当前状态

- Effekseer `1.80.5`
- Java `17+`
- 稳定导出的 C ABI
- CI 构建平台：
  - `windows-x86_64`
  - `linux-x86_64`
  - `macosx-x86_64`
  - `macosx-arm64`
  - `android-arm64`

当前绑定暴露的图形后端：

- OpenGL 3
- Android 上的 OpenGL ES 3
- Vulkan

## Maven 坐标

基础包：

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer</artifactId>
    <version>{version}</version>
</dependency>
```

平台 classifier：

- `windows-x86_64`
- `linux-x86_64`
- `macosx-x86_64`
- `macosx-arm64`
- `android-arm64`

示例：

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer</artifactId>
    <version>{version}</version>
    <classifier>windows-x86_64</classifier>
</dependency>
```

聚合包：

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer-platform</artifactId>
    <version>{version}</version>
</dependency>
```

如果你明确知道目标平台，直接依赖基础包和对应 classifier 即可。如果你希望按 JavaCPP preset 的常见方式一次性拉全平台依赖，可以使用 `behemiron-effekseer-platform`。

## API 概览

主要入口：

- `Effekseer`
  - 通用门面
  - 日志回调
  - backend 能力查询
  - effect / manager 创建
- `EffekseerGL`
  - OpenGL backend 初始化
  - OpenGL manager 创建
- `EffekseerVK`
  - Vulkan backend 初始化
  - Vulkan manager 创建
- `EffekseerEffect`
  - 加载 `.efkefc`
  - 查询依赖
  - 注入外部纹理 / 模型 / 材质 / 曲线
- `EffekseerManager`
  - update / play / control
  - typed 播放参数
  - batch API
  - collision callback

## 最小 OpenGL 用法

```java
import com.behemiron.engine.external.effect.effekseer.Effekseer;
import com.behemiron.engine.external.effect.effekseer.EffekseerGL;
import com.behemiron.engine.external.effect.effekseer.data.EffekseerEffect;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerInstance;
import com.behemiron.engine.external.effect.effekseer.backend.gl.EffekseerGLManager;

byte[] effectBytes = java.nio.file.Files.readAllBytes(
        java.nio.file.Path.of("example.efkefc")
);

Effekseer.setLogCallback((level, message) ->
        System.out.println("[Effekseer][" + level + "] " + message)
);

try (EffekseerGLManager manager = EffekseerGL.initializeAndCreateManager(8192, false);
     EffekseerEffect effect = Effekseer.loadEffect(effectBytes, 1.0f)) {

    manager.setViewProjectionMatrixWithSimpleWindow(1920, 1080);

    EffekseerInstance instance = manager.playInstance(effect);
    instance.setPosition(0.0f, 0.0f, 0.0f);

    manager.beginUpdate();
    manager.update(1.0f);
    manager.endUpdate();

    manager.renderGL(
            0,
            1920,
            1080,
            0,
            false,
            0,
            false,
            0,
            true,
            true
    );
}
```

## 最小 Vulkan 用法

```java
import com.behemiron.engine.external.effect.effekseer.Effekseer;
import com.behemiron.engine.external.effect.effekseer.EffekseerVK;
import com.behemiron.engine.external.effect.effekseer.backend.vk.EffekseerVKManager;
import com.behemiron.engine.external.effect.effekseer.backend.vk.VulkanRenderPassInfo;
import com.behemiron.engine.external.effect.effekseer.data.EffekseerEffect;

long physicalDevice = /* 宿主 VkPhysicalDevice 句柄 */;
long device = /* 宿主 VkDevice 句柄 */;
long queue = /* 宿主图形 VkQueue 句柄 */;
long commandPool = /* 宿主 VkCommandPool 句柄 */;
long commandBuffer = /* 宿主 VkCommandBuffer 句柄 */;
long backgroundImage = 0L;
int backgroundAspect = 0;
int backgroundFormat = 0;
long depthImage = 0L;
int depthAspect = 0;
int depthFormat = 0;

VulkanRenderPassInfo renderPassInfo = new VulkanRenderPassInfo(
        true,
        new int[] { /* 颜色附件 VkFormat */ },
        /* 深度附件 VkFormat */
);

byte[] effectBytes = java.nio.file.Files.readAllBytes(
        java.nio.file.Path.of("example.efkefc")
);

try (EffekseerVKManager manager = EffekseerVK.initializeAndCreateManager(
        physicalDevice,
        device,
        queue,
        commandPool,
        2,
        renderPassInfo,
        8192,
        false
);
     EffekseerEffect effect = Effekseer.loadEffect(effectBytes, 1.0f)) {

    manager.setViewProjectionMatrixWithSimpleWindow(1920, 1080);

    manager.beginUpdate();
    manager.update(1.0f);
    manager.endUpdate();

    manager.play(effect);
    manager.renderVK(
            commandBuffer,
            backgroundImage,
            backgroundAspect,
            backgroundFormat,
            depthImage,
            depthAspect,
            depthFormat,
            0,
            true,
            true
    );
}
```

Vulkan 帧入口需要由宿主渲染器提供原始 Vulkan 句柄，以及当前 render pass 的格式信息。

## Typed 播放参数

```java
import com.behemiron.engine.external.effect.effekseer.math.EffekseerTransform;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerVec3;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerPlayOptions;

EffekseerPlayOptions options = new EffekseerPlayOptions(
        new EffekseerTransform(
                new EffekseerVec3(0.0f, 1.0f, 0.0f),
                EffekseerVec3.zero(),
                EffekseerVec3.one()
        ),
        0
);

manager.playInstance(effect, options);
```

## Collision Callback

```java
manager.setWorldCollisionCallback(query -> {
    // 在这里接宿主世界的射线检测。
    // 没有命中时返回 null。
    return null;
});
```

碰撞回调支持返回命中点、法线、距离和自定义 `userFlags`。

## 目录

- `src/cpp/Core/`
  - 核心 native 封装
  - `BehemironEffekseerCAPI.*` 是稳定的导出 ABI
- `src/main/java/`
  - JavaCPP preset 与公共 Java API
- `platform/`
  - 聚合 platform artifact
- `cppbuild.sh`
  - 安装头文件与原生库到 `cppbuild/<platform>/`
- `CMakePresets.json`
  - 本地与 CI 共用的 CMake 入口

## License

- 本项目：MIT
- [effekseer/Effekseer](https://github.com/effekseer/Effekseer)：MIT
- [bytedeco/javacpp](https://github.com/bytedeco/javacpp)：Apache 2.0
