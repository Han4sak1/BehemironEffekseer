package com.behemiron.engine.external.effect.effekseer.internal.presets;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.presets.javacpp;

/**
 * JavaCPP preset 定义，负责声明 C API 头文件和 Native 库链接关系。
 */
@Properties(
        inherit = javacpp.class,
        value = {
                @Platform(
                        include = "BehemironEffekseerCAPI.h",
                        link = {
                                "behemiron_effekseer"
                        },
                        preload = {
                                "behemiron_effekseer"
                        },
                        library = "jniBehemironEffekseer"
                )
        }
)
public class BehemironEffekseerPreset {

    /*
      校验当前 JavaCPP 运行时版本。
     */
    static {
        Loader.checkVersion("org.bytedeco", "javacpp");
    }
}
