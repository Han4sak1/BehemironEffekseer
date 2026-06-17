package com.behemiron.engine.external.effect.effekseer.backend.gl;

import com.behemiron.engine.external.effect.effekseer.internal.raw.gl.BehemironEffekseerGLNative;
import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerManager;

/**
 * 在通用 manager 基础上补充 OpenGL 专属资源绑定能力。
 */
public final class EffekseerGLManager extends EffekseerManager {

    /**
     * 在指定 OpenGL FBO 上提交完整 Effekseer 帧。
     *
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
    public void renderGL(
            int targetFbo,
            int width,
            int height,
            int backgroundGlid,
            boolean backgroundHasMipmap,
            int depthGlid,
            boolean depthHasMipmap,
            int layer,
            boolean drawBack,
            boolean drawFront
    ) {
        BehemironEffekseerGLNative.managerRenderGL(
                handle(),
                targetFbo,
                width,
                height,
                backgroundGlid,
                backgroundHasMipmap ? 1 : 0,
                depthGlid,
                depthHasMipmap ? 1 : 0,
                layer,
                drawBack ? 1 : 0,
                drawFront ? 1 : 0
        );
    }
}
