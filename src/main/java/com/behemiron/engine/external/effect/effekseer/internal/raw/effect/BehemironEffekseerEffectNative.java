package com.behemiron.engine.external.effect.effekseer.internal.raw.effect;

import com.behemiron.engine.external.effect.effekseer.internal.presets.BehemironEffekseerPreset;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.annotation.Cast;
import org.bytedeco.javacpp.annotation.Name;
import org.bytedeco.javacpp.annotation.Properties;

/**
 * Effect C ABI 的 JavaCPP 绑定声明。
 */
@Properties(inherit = BehemironEffekseerPreset.class)
public final class BehemironEffekseerEffectNative {

    /*
      加载当前平台的 Native 库。
     */
    static {
        Loader.load();
    }

    /**
     * 工具类不允许实例化。
     */
    private BehemironEffekseerEffectNative() {
    }

    /**
     * 创建一个 Native effect。
     *
     * @return Native effect 指针
     */
    @Name("be_effekseer_effect_create")
    public static native Pointer effectCreate();

    /**
     * 销毁一个 Native effect。
     *
     * @param handle Native effect 指针
     */
    @Name("be_effekseer_effect_destroy")
    public static native void effectDestroy(Pointer handle);

    /**
     * 加载 Effekseer 特效二进制。
     *
     * @param handle        Native effect 指针
     * @param data          特效二进制内容
     * @param len           数据长度
     * @param magnification 播放倍率
     * @return 是否加载成功
     */
    @Name("be_effekseer_effect_load")
    public static native @Cast("int32_t") int effectLoad(
            Pointer handle,
            @Cast("const uint8_t*") BytePointer data,
            @Cast("int32_t") int len,
            float magnification
    );

    /**
     * 获取指定纹理槽位的路径。
     *
     * @param handle      Native effect 指针
     * @param index       纹理索引
     * @param textureType 纹理类型
     * @return UTF-16 纹理路径指针
     */
    @Name("be_effekseer_effect_get_texture_path_utf16")
    public static native @Cast("const uint16_t*") ShortPointer effectGetTexturePathUtf16(
            Pointer handle,
            @Cast("int32_t") int index,
            @Cast("int32_t") int textureType
    );

    /**
     * 获取指定纹理类型的数量。
     *
     * @param handle      Native effect 指针
     * @param textureType 纹理类型
     * @return 纹理数量
     */
    @Name("be_effekseer_effect_get_texture_count")
    public static native @Cast("int32_t") int effectGetTextureCount(Pointer handle, @Cast("int32_t") int textureType);

    /**
     * 向指定纹理槽位注入外部纹理数据。
     *
     * @param handle      Native effect 指针
     * @param data        纹理二进制内容
     * @param len         数据长度
     * @param index       纹理索引
     * @param textureType 纹理类型
     * @return 是否加载成功
     */
    @Name("be_effekseer_effect_load_texture")
    public static native @Cast("int32_t") int effectLoadTexture(
            Pointer handle,
            @Cast("const uint8_t*") BytePointer data,
            @Cast("int32_t") int len,
            @Cast("int32_t") int index,
            @Cast("int32_t") int textureType
    );

    /**
     * 查询指定纹理槽位是否已经加载。
     *
     * @param handle      Native effect 指针
     * @param index       纹理索引
     * @param textureType 纹理类型
     * @return 是否已经加载
     */
    @Name("be_effekseer_effect_has_texture_loaded")
    public static native @Cast("int32_t") int effectHasTextureLoaded(
            Pointer handle,
            @Cast("int32_t") int index,
            @Cast("int32_t") int textureType
    );

    /**
     * 获取指定模型槽位的路径。
     *
     * @param handle Native effect 指针
     * @param index  模型索引
     * @return UTF-16 模型路径指针
     */
    @Name("be_effekseer_effect_get_model_path_utf16")
    public static native @Cast("const uint16_t*") ShortPointer effectGetModelPathUtf16(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取模型资源数量。
     *
     * @param handle Native effect 指针
     * @return 模型数量
     */
    @Name("be_effekseer_effect_get_model_count")
    public static native @Cast("int32_t") int effectGetModelCount(Pointer handle);

    /**
     * 向指定模型槽位注入外部模型数据。
     *
     * @param handle Native effect 指针
     * @param data   模型二进制内容
     * @param len    数据长度
     * @param index  模型索引
     * @return 是否加载成功
     */
    @Name("be_effekseer_effect_load_model")
    public static native @Cast("int32_t") int effectLoadModel(
            Pointer handle,
            @Cast("const uint8_t*") BytePointer data,
            @Cast("int32_t") int len,
            @Cast("int32_t") int index
    );

    /**
     * 查询指定模型槽位是否已经加载。
     *
     * @param handle Native effect 指针
     * @param index  模型索引
     * @return 是否已经加载
     */
    @Name("be_effekseer_effect_has_model_loaded")
    public static native @Cast("int32_t") int effectHasModelLoaded(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取指定材质槽位的路径。
     *
     * @param handle Native effect 指针
     * @param index  材质索引
     * @return UTF-16 材质路径指针
     */
    @Name("be_effekseer_effect_get_material_path_utf16")
    public static native @Cast("const uint16_t*") ShortPointer effectGetMaterialPathUtf16(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取材质资源数量。
     *
     * @param handle Native effect 指针
     * @return 材质数量
     */
    @Name("be_effekseer_effect_get_material_count")
    public static native @Cast("int32_t") int effectGetMaterialCount(Pointer handle);

    /**
     * 向指定材质槽位注入外部材质数据。
     *
     * @param handle Native effect 指针
     * @param data   材质二进制内容
     * @param len    数据长度
     * @param index  材质索引
     * @return 是否加载成功
     */
    @Name("be_effekseer_effect_load_material")
    public static native @Cast("int32_t") int effectLoadMaterial(
            Pointer handle,
            @Cast("const uint8_t*") BytePointer data,
            @Cast("int32_t") int len,
            @Cast("int32_t") int index
    );

    /**
     * 查询指定材质槽位是否已经加载。
     *
     * @param handle Native effect 指针
     * @param index  材质索引
     * @return 是否已经加载
     */
    @Name("be_effekseer_effect_has_material_loaded")
    public static native @Cast("int32_t") int effectHasMaterialLoaded(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取指定曲线槽位的路径。
     *
     * @param handle Native effect 指针
     * @param index  曲线索引
     * @return UTF-16 曲线路径指针
     */
    @Name("be_effekseer_effect_get_curve_path_utf16")
    public static native @Cast("const uint16_t*") ShortPointer effectGetCurvePathUtf16(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取曲线资源数量。
     *
     * @param handle Native effect 指针
     * @return 曲线数量
     */
    @Name("be_effekseer_effect_get_curve_count")
    public static native @Cast("int32_t") int effectGetCurveCount(Pointer handle);

    /**
     * 向指定曲线槽位注入外部曲线数据。
     *
     * @param handle Native effect 指针
     * @param data   曲线二进制内容
     * @param len    数据长度
     * @param index  曲线索引
     * @return 是否加载成功
     */
    @Name("be_effekseer_effect_load_curve")
    public static native @Cast("int32_t") int effectLoadCurve(
            Pointer handle,
            @Cast("const uint8_t*") BytePointer data,
            @Cast("int32_t") int len,
            @Cast("int32_t") int index
    );

    /**
     * 查询指定曲线槽位是否已经加载。
     *
     * @param handle Native effect 指针
     * @param index  曲线索引
     * @return 是否已经加载
     */
    @Name("be_effekseer_effect_has_curve_loaded")
    public static native @Cast("int32_t") int effectHasCurveLoaded(Pointer handle, @Cast("int32_t") int index);

    /**
     * 获取特效生命周期最大帧数。
     *
     * @param handle Native effect 指针
     * @return 最大帧数
     */
    @Name("be_effekseer_effect_get_term_max")
    public static native @Cast("int32_t") int effectGetTermMax(Pointer handle);

    /**
     * 获取特效生命周期最小帧数。
     *
     * @param handle Native effect 指针
     * @return 最小帧数
     */
    @Name("be_effekseer_effect_get_term_min")
    public static native @Cast("int32_t") int effectGetTermMin(Pointer handle);

    /**
     * 获取统一依赖数量。
     *
     * @param handle Native effect 指针
     * @return 统一依赖数量
     */
    @Name("be_effekseer_effect_get_dependency_count")
    public static native @Cast("int32_t") int effectGetDependencyCount(Pointer handle);

    /**
     * 获取统一依赖项。
     *
     * @param handle        Native effect 指针
     * @param index         依赖索引
     * @param outDependency 输出结构
     * @return 是否成功写出
     */
    @Name("be_effekseer_effect_get_dependency")
    public static native @Cast("int32_t") int effectGetDependency(
            Pointer handle,
            @Cast("int32_t") int index,
            com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerEffectDependencyNative outDependency
    );
}
