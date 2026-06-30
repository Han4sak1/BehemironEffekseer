package com.behemiron.engine.external.effect.effekseer.data;

import com.behemiron.engine.external.effect.effekseer.internal.raw.effect.BehemironEffekseerEffectNative;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 表示一个可被加载、查询资源并交给 manager 播放的 Effekseer 特效对象。
 */
public final class EffekseerEffect implements AutoCloseable {

    private final Pointer handle;
    private boolean closed;

    /**
     * 创建一个尚未加载内容的特效对象。
     */
    public EffekseerEffect() {
        this.handle = BehemironEffekseerEffectNative.effectCreate();
        if (handle == null || handle.isNull()) {
            throw new IllegalStateException("创建 Effekseer effect 失败");
        }
    }

    /**
     * 返回底层 Native handle，仅供当前封装层内部使用。
     *
     * @return Native handle
     */
    public Pointer handle() {
        return handle;
    }

    /**
     * 加载 `.efkefc` 或等价的 Effekseer 二进制数据。
     *
     * @param data          特效二进制内容
     * @param magnification 播放倍率
     * @return 是否加载成功
     */
    public boolean load(byte[] data, float magnification) {
        Objects.requireNonNull(data, "data");
        try (BytePointer bytes = new BytePointer(data)) {
            return BehemironEffekseerEffectNative.effectLoad(handle, bytes, data.length, magnification) != 0;
        }
    }

    /**
     * 获取指定纹理类型的资源数量。
     *
     * @param type 纹理槽类型
     * @return 该类型纹理数量
     */
    public int getTextureCount(EffekseerTextureType type) {
        Objects.requireNonNull(type, "type");
        return BehemironEffekseerEffectNative.effectGetTextureCount(handle, type.nativeValue());
    }

    /**
     * 获取指定纹理槽位的资源路径。
     *
     * @param index 纹理索引
     * @param type  纹理槽类型
     * @return 纹理路径；若不存在则返回 {@code null}
     */
    public String getTexturePath(int index, EffekseerTextureType type) {
        Objects.requireNonNull(type, "type");
        return fromUtf16(BehemironEffekseerEffectNative.effectGetTexturePathUtf16(handle, index, type.nativeValue()));
    }

    /**
     * 向指定纹理槽位注入外部纹理数据。
     *
     * @param data  纹理二进制内容
     * @param index 纹理索引
     * @param type  纹理槽类型
     * @return 是否加载成功
     */
    public boolean loadTexture(byte[] data, int index, EffekseerTextureType type) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(type, "type");
        try (BytePointer bytes = new BytePointer(data)) {
            return BehemironEffekseerEffectNative.effectLoadTexture(handle, bytes, data.length, index, type.nativeValue()) != 0;
        }
    }

    /**
     * 检查指定纹理槽位是否已经注入资源。
     *
     * @param index 纹理索引
     * @param type  纹理槽类型
     * @return 是否已经加载
     */
    public boolean hasTextureLoaded(int index, EffekseerTextureType type) {
        Objects.requireNonNull(type, "type");
        return BehemironEffekseerEffectNative.effectHasTextureLoaded(handle, index, type.nativeValue()) != 0;
    }

    /**
     * 获取模型资源数量。
     *
     * @return 模型数量
     */
    public int getModelCount() {
        return BehemironEffekseerEffectNative.effectGetModelCount(handle);
    }

    /**
     * 获取指定模型资源路径。
     *
     * @param index 模型索引
     * @return 模型路径；若不存在则返回 {@code null}
     */
    public String getModelPath(int index) {
        return fromUtf16(BehemironEffekseerEffectNative.effectGetModelPathUtf16(handle, index));
    }

    /**
     * 向指定模型槽位注入外部模型数据。
     *
     * @param data  模型二进制内容
     * @param index 模型索引
     * @return 是否加载成功
     */
    public boolean loadModel(byte[] data, int index) {
        Objects.requireNonNull(data, "data");
        try (BytePointer bytes = new BytePointer(data)) {
            return BehemironEffekseerEffectNative.effectLoadModel(handle, bytes, data.length, index) != 0;
        }
    }

    /**
     * 检查指定模型槽位是否已经注入资源。
     *
     * @param index 模型索引
     * @return 是否已经加载
     */
    public boolean hasModelLoaded(int index) {
        return BehemironEffekseerEffectNative.effectHasModelLoaded(handle, index) != 0;
    }

    /**
     * 获取材质资源数量。
     *
     * @return 材质数量
     */
    public int getMaterialCount() {
        return BehemironEffekseerEffectNative.effectGetMaterialCount(handle);
    }

    /**
     * 获取指定材质资源路径。
     *
     * @param index 材质索引
     * @return 材质路径；若不存在则返回 {@code null}
     */
    public String getMaterialPath(int index) {
        return fromUtf16(BehemironEffekseerEffectNative.effectGetMaterialPathUtf16(handle, index));
    }

    /**
     * 向指定材质槽位注入外部材质数据。
     *
     * @param data  材质二进制内容
     * @param index 材质索引
     * @return 是否加载成功
     */
    public boolean loadMaterial(byte[] data, int index) {
        Objects.requireNonNull(data, "data");
        try (BytePointer bytes = new BytePointer(data)) {
            return BehemironEffekseerEffectNative.effectLoadMaterial(handle, bytes, data.length, index) != 0;
        }
    }

    /**
     * 检查指定材质槽位是否已经注入资源。
     *
     * @param index 材质索引
     * @return 是否已经加载
     */
    public boolean hasMaterialLoaded(int index) {
        return BehemironEffekseerEffectNative.effectHasMaterialLoaded(handle, index) != 0;
    }

    /**
     * 获取曲线资源数量。
     *
     * @return 曲线数量
     */
    public int getCurveCount() {
        return BehemironEffekseerEffectNative.effectGetCurveCount(handle);
    }

    /**
     * 获取指定曲线资源路径。
     *
     * @param index 曲线索引
     * @return 曲线路径；若不存在则返回 {@code null}
     */
    public String getCurvePath(int index) {
        return fromUtf16(BehemironEffekseerEffectNative.effectGetCurvePathUtf16(handle, index));
    }

    /**
     * 向指定曲线槽位注入外部曲线数据。
     *
     * @param data  曲线二进制内容
     * @param index 曲线索引
     * @return 是否加载成功
     */
    public boolean loadCurve(byte[] data, int index) {
        Objects.requireNonNull(data, "data");
        try (BytePointer bytes = new BytePointer(data)) {
            return BehemironEffekseerEffectNative.effectLoadCurve(handle, bytes, data.length, index) != 0;
        }
    }

    /**
     * 检查指定曲线槽位是否已经注入资源。
     *
     * @param index 曲线索引
     * @return 是否已经加载
     */
    public boolean hasCurveLoaded(int index) {
        return BehemironEffekseerEffectNative.effectHasCurveLoaded(handle, index) != 0;
    }

    /**
     * 获取特效最短生命周期帧数。
     *
     * @return 最短生命周期帧数
     */
    public int getTermMin() {
        return BehemironEffekseerEffectNative.effectGetTermMin(handle);
    }

    /**
     * 获取特效最长生命周期帧数。
     *
     * @return 最长生命周期帧数
     */
    public int getTermMax() {
        return BehemironEffekseerEffectNative.effectGetTermMax(handle);
    }

    /**
     * 获取统一依赖数量。
     *
     * @return 统一依赖数量
     */
    public int getDependencyCount() {
        return BehemironEffekseerEffectNative.effectGetDependencyCount(handle);
    }

    /**
     * 获取指定索引的统一依赖项。
     *
     * @param index 依赖索引
     * @return 统一依赖项
     */
    public EffekseerDependency getDependency(int index) {
        var dependency = new com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerEffectDependencyNative();
        if (BehemironEffekseerEffectNative.effectGetDependency(handle, index, dependency) == 0) {
            throw new IllegalArgumentException("依赖索引越界: " + index);
        }
        return new EffekseerDependency(
                EffekseerDependencyType.fromNative(dependency.type()),
                dependency.slot(),
                fromUtf16(dependency.path_utf16())
        );
    }

    /**
     * 获取全部统一依赖项。
     *
     * @return 全部统一依赖项
     */
    public List<EffekseerDependency> getDependencies() {
        int count = getDependencyCount();
        List<EffekseerDependency> dependencies = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            dependencies.add(getDependency(i));
        }
        return Collections.unmodifiableList(dependencies);
    }

    /**
     * 释放底层 Native 特效对象。
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        BehemironEffekseerEffectNative.effectDestroy(handle);
    }

    /**
     * 把 Native 返回的 UTF-16 字符串转换为 Java 字符串。
     *
     * @param pointer Native UTF-16 指针
     * @return Java 字符串；若指针为空则返回 {@code null}
     */
    private static String fromUtf16(ShortPointer pointer) {
        if (pointer == null || pointer.isNull()) {
            return null;
        }

        int length = 0;
        while (pointer.get(length) != 0) {
            length++;
        }

        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) (pointer.get(i) & 0xFFFF);
        }
        return new String(chars);
    }
}
