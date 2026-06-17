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
     * 开始隔离 OpenGL 加载态。
     *
     * @return Native 状态句柄
     */
    @Name("be_effekseer_gl_begin_load_state")
    public static native Pointer glBeginLoadState();

    /**
     * 结束 OpenGL 加载态隔离。
     *
     * @param state Native 状态句柄
     */
    @Name("be_effekseer_gl_end_load_state")
    public static native void glEndLoadState(Pointer state);

    /**
     * 在指定 OpenGL FBO 上提交完整 Effekseer 帧。
     *
     * @param handle              Native manager 指针
     * @param targetFbo           目标 FBO
     * @param width               视口宽度
     * @param height              视口高度
     * @param backgroundGlid      背景纹理 ID，0 表示无
     * @param backgroundHasMipmap 背景纹理是否包含 mipmap
     * @param depthGlid           深度纹理 ID，0 表示无
     * @param depthHasMipmap      深度纹理是否包含 mipmap
     * @param layer               layer 索引
     * @param drawBack            是否绘制后景
     * @param drawFront           是否绘制前景
     */
    @Name("be_effekseer_manager_render_gl")
    public static native void managerRenderGL(
            Pointer handle,
            @Cast("uint32_t") int targetFbo,
            @Cast("int32_t") int width,
            @Cast("int32_t") int height,
            @Cast("uint32_t") int backgroundGlid,
            @Cast("int32_t") int backgroundHasMipmap,
            @Cast("uint32_t") int depthGlid,
            @Cast("int32_t") int depthHasMipmap,
            @Cast("int32_t") int layer,
            @Cast("int32_t") int drawBack,
            @Cast("int32_t") int drawFront
    );
}
