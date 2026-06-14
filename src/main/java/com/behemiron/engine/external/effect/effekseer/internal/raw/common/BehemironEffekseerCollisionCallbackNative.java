package com.behemiron.engine.external.effect.effekseer.internal.raw.common;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.FunctionPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * 对应 C ABI 中的世界碰撞回调函数指针。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public abstract class BehemironEffekseerCollisionCallbackNative extends FunctionPointer {

    static {
        Loader.load();
    }

    /**
     * 供 JavaCPP 运行时实例化。
     */
    protected BehemironEffekseerCollisionCallbackNative() {
    }

    /**
     * 处理一次从 Effekseer 发起的世界碰撞查询。
     *
     * @param query    射线查询参数
     * @param hit      命中结果输出
     * @param userData 透传的用户数据指针
     * @return 非 0 表示命中
     */
    public abstract @Cast("int32_t") int call(
            BehemironEffekseerCollisionQueryNative query,
            BehemironEffekseerCollisionHitNative hit,
            Pointer userData
    );
}
