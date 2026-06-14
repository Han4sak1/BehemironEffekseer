package com.behemiron.engine.external.effect.effekseer.internal.raw.gl;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * OpenGL C ABI 的 JavaCPP 绑定声明。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public final class BehemironEffekseerGLNative {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    /**
     * 工具类不允许实例化。
     */
    private BehemironEffekseerGLNative() {
    }

    /**
     * 初始化 OpenGL backend。
     *
     * @return 是否初始化成功
     */
    @Name("be_effekseer_backend_initialize_gl")
    public static native @Cast("int32_t") int backendInitializeGL();

    /**
     * 绑定 OpenGL 背景纹理。
     *
     * @param handle    Native manager 指针
     * @param glid      OpenGL 纹理 ID
     * @param hasMipmap 是否包含 mipmap
     */
    @Name("be_effekseer_manager_set_background_gl")
    public static native void managerSetBackgroundGL(Pointer handle, @Cast("uint32_t") int glid, @Cast("int32_t") int hasMipmap);

    /**
     * 绑定 OpenGL 深度纹理。
     *
     * @param handle    Native manager 指针
     * @param glid      OpenGL 纹理 ID
     * @param hasMipmap 是否包含 mipmap
     */
    @Name("be_effekseer_manager_set_depth_gl")
    public static native void managerSetDepthGL(Pointer handle, @Cast("uint32_t") int glid, @Cast("int32_t") int hasMipmap);
}
