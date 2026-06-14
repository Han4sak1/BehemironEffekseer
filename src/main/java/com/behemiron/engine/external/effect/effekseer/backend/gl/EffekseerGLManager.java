package com.behemiron.engine.external.effect.effekseer.backend.gl;

import com.behemiron.engine.external.effect.effekseer.runtime.EffekseerManager;
import com.behemiron.engine.external.effect.effekseer.internal.raw.gl.BehemironEffekseerGLNative;

/**
 * 在通用 manager 基础上补充 OpenGL 专属资源绑定能力。
 */
public final class EffekseerGLManager extends EffekseerManager {

    /**
     * 绑定 OpenGL 背景纹理。
     *
     * @param glid OpenGL 纹理 ID
     * @param hasMipmap 该纹理是否包含 mipmap
     */
    public void setBackgroundGL(int glid, boolean hasMipmap) {
        BehemironEffekseerGLNative.managerSetBackgroundGL(handle(), glid, hasMipmap ? 1 : 0);
    }

    /**
     * 绑定 OpenGL 深度纹理。
     *
     * @param glid OpenGL 纹理 ID
     * @param hasMipmap 该纹理是否包含 mipmap
     */
    public void setDepthGL(int glid, boolean hasMipmap) {
        BehemironEffekseerGLNative.managerSetDepthGL(handle(), glid, hasMipmap ? 1 : 0);
    }
}
