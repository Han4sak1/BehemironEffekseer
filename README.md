# Behemiron Effekseer Java Binding

[中文](README.zh-CN.md)

[![Maven Central](https://img.shields.io/maven-central/v/com.behemiron.engine/behemiron-effekseer)](https://repo1.maven.org/maven2/com/behemiron/engine/behemiron-effekseer/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/license/mit)

Java bindings for the Behemiron Effekseer runtime, built on top of a stable C ABI and JavaCPP presets.

## Status

- Effekseer `1.80.4`
- Java `17+`
- Stable exported C ABI
- CI-built artifacts for:
  - `windows-x86_64`
  - `linux-x86_64`
  - `macosx-x86_64`
  - `macosx-arm64`
  - `android-arm64`

Graphics backends exposed by the binding:

- OpenGL 3
- OpenGL ES 3 on Android
- Vulkan

## Artifacts

Main artifact:

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer</artifactId>
    <version>0.1.0</version>
</dependency>
```

Platform classifiers:

- `windows-x86_64`
- `linux-x86_64`
- `macosx-x86_64`
- `macosx-arm64`
- `android-arm64`

Example:

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer</artifactId>
    <version>0.1.0</version>
    <classifier>windows-x86_64</classifier>
</dependency>
```

Platform bundle artifact:

```xml
<dependency>
    <groupId>com.behemiron.engine</groupId>
    <artifactId>behemiron-effekseer-platform</artifactId>
    <version>0.1.0</version>
</dependency>
```

Use the main artifact when you control the target platform explicitly. Use the platform bundle when you want a JavaCPP-style aggregate dependency.

## API Overview

Public entry points:

- `Effekseer`
  - common facade
  - logging
  - backend capability queries
  - effect and manager creation
- `EffekseerGL`
  - OpenGL backend bootstrap
  - OpenGL manager creation
- `EffekseerVK`
  - Vulkan backend bootstrap
  - Vulkan manager creation
- `EffekseerEffect`
  - load `.efkefc`
  - inspect dependencies
  - inject external textures/models/materials/curves
- `EffekseerManager`
  - update, play, and instance control
  - typed play options
  - batch APIs
  - collision callback integration

## Minimal OpenGL Usage

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

## Minimal Vulkan Usage

```java
import com.behemiron.engine.external.effect.effekseer.Effekseer;
import com.behemiron.engine.external.effect.effekseer.EffekseerVK;
import com.behemiron.engine.external.effect.effekseer.backend.vk.EffekseerVKManager;
import com.behemiron.engine.external.effect.effekseer.backend.vk.VulkanRenderPassInfo;
import com.behemiron.engine.external.effect.effekseer.data.EffekseerEffect;

long physicalDevice = /* host VkPhysicalDevice handle */;
long device = /* host VkDevice handle */;
long queue = /* host graphics VkQueue handle */;
long commandPool = /* host VkCommandPool handle */;
long commandBuffer = /* host VkCommandBuffer handle */;
long backgroundImage = 0L;
int backgroundAspect = 0;
int backgroundFormat = 0;
long depthImage = 0L;
int depthAspect = 0;
int depthFormat = 0;

VulkanRenderPassInfo renderPassInfo = new VulkanRenderPassInfo(
        true,
        new int[] { /* color attachment VkFormat */ },
        /* depth attachment VkFormat */
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

The Vulkan frame entry point expects raw host Vulkan handles and render-pass format metadata from your renderer integration.

## Typed Play Options

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
    // Perform a host-side ray cast here.
    // Return null when no hit is found.
    return null;
});
```

The collision callback supports hit position, hit normal, hit distance, and custom user flags.

## Repository Layout

- `src/cpp/Core/`
  - core native wrapper
  - `BehemironEffekseerCAPI.*` is the stable exported ABI surface
- `src/main/java/`
  - JavaCPP preset and public Java API
- `platform/`
  - aggregate platform artifact
- `cppbuild.sh`
  - installs headers and native libraries into `cppbuild/<platform>/`
- `CMakePresets.json`
  - shared local and CI CMake entry points

## License

- This project: MIT
- [effekseer/Effekseer](https://github.com/effekseer/Effekseer): MIT
- [bytedeco/javacpp](https://github.com/bytedeco/javacpp): Apache 2.0
