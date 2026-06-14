package com.behemiron.engine.external.effect.effekseer.runtime;

import com.behemiron.engine.external.effect.effekseer.collision.EffekseerCollisionHit;
import com.behemiron.engine.external.effect.effekseer.collision.EffekseerCollisionQuery;
import com.behemiron.engine.external.effect.effekseer.collision.EffekseerWorldCollisionCallback;
import com.behemiron.engine.external.effect.effekseer.data.EffekseerEffect;
import com.behemiron.engine.external.effect.effekseer.internal.raw.common.*;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerMatrix43;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerMatrix44;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerTransform;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;

import java.util.Objects;

/**
 * 封装 Effekseer manager 的通用生命周期、播放和渲染控制能力。
 */
public class EffekseerManager implements AutoCloseable {

    private final Pointer handle;
    private BehemironEffekseerCollisionCallbackNative nativeCollisionCallback;
    private EffekseerWorldCollisionCallback worldCollisionCallback;
    private boolean closed;

    /**
     * 创建一个尚未初始化 renderer 的 manager。
     */
    public EffekseerManager() {
        this.handle = BehemironEffekseerCommonNative.managerCreate();
        if (handle == null || handle.isNull()) {
            throw new IllegalStateException("创建 Effekseer manager 失败");
        }
    }

    /**
     * 返回底层 Native handle，仅供当前封装层及子类使用。
     *
     * @return Native handle
     */
    protected final Pointer handle() {
        return handle;
    }

    /**
     * 初始化 manager 对应的 renderer。
     *
     * @param spriteMaxCount 粒子最大数量
     * @param srgbMode       是否启用 sRGB 模式
     * @return 是否初始化成功
     */
    public boolean initialize(int spriteMaxCount, boolean srgbMode) {
        return BehemironEffekseerCommonNative.managerInitialize(handle, spriteMaxCount, srgbMode ? 1 : 0) != 0;
    }

    /**
     * 开始一个 update 批次。
     */
    public void beginUpdate() {
        BehemironEffekseerCommonNative.managerBeginUpdate(handle);
    }

    /**
     * 结束一个 update 批次。
     */
    public void endUpdate() {
        BehemironEffekseerCommonNative.managerEndUpdate(handle);
    }

    /**
     * 推进 manager 时间。
     *
     * @param deltaFrames 要推进的帧数
     */
    public void update(float deltaFrames) {
        BehemironEffekseerCommonNative.managerUpdate(handle, deltaFrames);
    }

    /**
     * 把指定实例推进到目标帧。
     *
     * @param effectHandle 实例 handle
     * @param frame        目标帧
     */
    public void updateHandleToMoveToFrame(int effectHandle, float frame) {
        BehemironEffekseerCommonNative.managerUpdateHandleToMoveToFrame(handle, effectHandle, frame);
    }

    /**
     * 播放一个特效并返回 Native 实例 handle。
     *
     * @param effect 要播放的特效
     * @return Native 实例 handle；失败时可能为负数
     */
    public int play(EffekseerEffect effect) {
        Objects.requireNonNull(effect, "effect");
        return BehemironEffekseerCommonNative.managerPlay(handle, effect.handle());
    }

    /**
     * 使用 typed 播放参数播放一个特效。
     *
     * @param effect  要播放的特效
     * @param options 播放参数
     * @return Native 实例 handle；失败时可能为负数
     */
    public int play(EffekseerEffect effect, EffekseerPlayOptions options) {
        ensureOpen();
        Objects.requireNonNull(effect, "effect");
        Objects.requireNonNull(options, "options");
        try (BehemironEffekseerPlayOptionsNative nativeOptions = toNative(options)) {
            return BehemironEffekseerCommonNative.managerPlayWithOptions(handle, effect.handle(), nativeOptions);
        }
    }

    /**
     * 播放一个特效并返回便于后续控制的实例包装对象。
     *
     * @param effect 要播放的特效
     * @return 便捷实例包装对象
     */
    public EffekseerInstance playInstance(EffekseerEffect effect) {
        int effectHandle = play(effect);
        if (effectHandle < 0) {
            throw new IllegalStateException("播放 Effekseer effect 失败");
        }
        return new EffekseerInstance(this, effectHandle);
    }

    /**
     * 使用 typed 播放参数播放一个特效并返回实例包装对象。
     *
     * @param effect  要播放的特效
     * @param options 播放参数
     * @return 实例包装对象
     */
    public EffekseerInstance playInstance(EffekseerEffect effect, EffekseerPlayOptions options) {
        int effectHandle = play(effect, options);
        if (effectHandle < 0) {
            throw new IllegalStateException("播放 Effekseer effect 失败");
        }
        return new EffekseerInstance(this, effectHandle);
    }

    /**
     * 把已有的 Native 实例 handle 包装成便捷实例对象。
     *
     * @param effectHandle Native 实例 handle
     * @return 实例包装对象
     */
    public EffekseerInstance wrapInstance(int effectHandle) {
        return new EffekseerInstance(this, effectHandle);
    }

    /**
     * 停止当前 manager 上的全部实例。
     */
    public void stopAll() {
        BehemironEffekseerCommonNative.managerStopAll(handle);
    }

    /**
     * 停止指定实例。
     *
     * @param effectHandle 实例 handle
     */
    public void stop(int effectHandle) {
        BehemironEffekseerCommonNative.managerStop(handle, effectHandle);
    }

    /**
     * 设置实例的暂停状态。
     *
     * @param effectHandle 实例 handle
     * @param paused       是否暂停
     */
    public void setPaused(int effectHandle, boolean paused) {
        BehemironEffekseerCommonNative.managerSetPaused(handle, effectHandle, paused ? 1 : 0);
    }

    /**
     * 设置实例是否可见。
     *
     * @param effectHandle 实例 handle
     * @param shown        是否显示
     */
    public void setShown(int effectHandle, boolean shown) {
        BehemironEffekseerCommonNative.managerSetShown(handle, effectHandle, shown ? 1 : 0);
    }

    /**
     * 向实例发送 trigger。
     *
     * @param effectHandle 实例 handle
     * @param index        trigger 索引
     */
    public void sendTrigger(int effectHandle, int index) {
        BehemironEffekseerCommonNative.managerSendTrigger(handle, effectHandle, index);
    }

    /**
     * 设置实例位置。
     *
     * @param effectHandle 实例 handle
     * @param x            X 坐标
     * @param y            Y 坐标
     * @param z            Z 坐标
     */
    public void setEffectPosition(int effectHandle, float x, float y, float z) {
        BehemironEffekseerCommonNative.managerSetEffectPosition(handle, effectHandle, x, y, z);
    }

    /**
     * 设置实例旋转。
     *
     * @param effectHandle 实例 handle
     * @param x            X 轴旋转
     * @param y            Y 轴旋转
     * @param z            Z 轴旋转
     */
    public void setEffectRotation(int effectHandle, float x, float y, float z) {
        BehemironEffekseerCommonNative.managerSetEffectRotation(handle, effectHandle, x, y, z);
    }

    /**
     * 设置实例缩放。
     *
     * @param effectHandle 实例 handle
     * @param x            X 轴缩放
     * @param y            Y 轴缩放
     * @param z            Z 轴缩放
     */
    public void setEffectScale(int effectHandle, float x, float y, float z) {
        BehemironEffekseerCommonNative.managerSetEffectScale(handle, effectHandle, x, y, z);
    }

    /**
     * 设置指定 layer 的观察参数。
     *
     * @param layer        layer 索引
     * @param viewerPosX   观察者 X 坐标
     * @param viewerPosY   观察者 Y 坐标
     * @param viewerPosZ   观察者 Z 坐标
     * @param distanceBias 距离偏移
     */
    public void setLayerParameter(int layer, float viewerPosX, float viewerPosY, float viewerPosZ, float distanceBias) {
        BehemironEffekseerCommonNative.managerSetLayerParameter(handle, layer, viewerPosX, viewerPosY, viewerPosZ, distanceBias);
    }

    /**
     * 设置实例的 3x4 变换矩阵。
     *
     * @param effectHandle 实例 handle
     * @param values12     12 个 float，按 Native 接口顺序传入
     */
    public void setEffectTransformMatrix(int effectHandle, float... values12) {
        Objects.requireNonNull(values12, "values12");
        if (values12.length != 12) {
            throw new IllegalArgumentException("effect transform matrix 需要 12 个 float");
        }
        BehemironEffekseerCommonNative.managerSetEffectTransformMatrix(
                handle,
                effectHandle,
                values12[0], values12[1], values12[2], values12[3],
                values12[4], values12[5], values12[6], values12[7],
                values12[8], values12[9], values12[10], values12[11]
        );
    }

    /**
     * 设置实例的基础 3x4 变换矩阵。
     *
     * @param effectHandle 实例 handle
     * @param values12     12 个 float，按 Native 接口顺序传入
     */
    public void setEffectTransformBaseMatrix(int effectHandle, float... values12) {
        ensureOpen();
        Objects.requireNonNull(values12, "values12");
        if (values12.length != 12) {
            throw new IllegalArgumentException("effect transform base matrix 需要 12 个 float");
        }
        BehemironEffekseerCommonNative.managerSetEffectTransformBaseMatrix(
                handle,
                effectHandle,
                values12[0], values12[1], values12[2], values12[3],
                values12[4], values12[5], values12[6], values12[7],
                values12[8], values12[9], values12[10], values12[11]
        );
    }

    /**
     * 设置实例的 3x4 变换矩阵。
     *
     * @param effectHandle 实例 handle
     * @param matrix       3x4 矩阵
     */
    public void setEffectTransformMatrix(int effectHandle, EffekseerMatrix43 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        setEffectTransformMatrix(effectHandle, matrix.toArray());
    }

    /**
     * 设置实例的基础 3x4 变换矩阵。
     *
     * @param effectHandle 实例 handle
     * @param matrix       3x4 矩阵
     */
    public void setEffectTransformBaseMatrix(int effectHandle, EffekseerMatrix43 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        setEffectTransformBaseMatrix(effectHandle, matrix.toArray());
    }

    /**
     * 绘制指定 layer 的后景特效。
     *
     * @param layer layer 索引
     */
    public void drawBack(int layer) {
        BehemironEffekseerCommonNative.managerDrawBack(handle, layer);
    }

    /**
     * 绘制指定 layer 的前景特效。
     *
     * @param layer layer 索引
     */
    public void drawFront(int layer) {
        BehemironEffekseerCommonNative.managerDrawFront(handle, layer);
    }

    /**
     * 设置实例所属 layer。
     *
     * @param effectHandle 实例 handle
     * @param layer        layer 索引
     */
    public void setLayer(int effectHandle, int layer) {
        BehemironEffekseerCommonNative.managerSetLayer(handle, effectHandle, layer);
    }

    /**
     * 设置相机朝向和位置参数。
     *
     * @param frontX 前向向量 X
     * @param frontY 前向向量 Y
     * @param frontZ 前向向量 Z
     * @param posX   相机位置 X
     * @param posY   相机位置 Y
     * @param posZ   相机位置 Z
     */
    public void setCameraParameter(float frontX, float frontY, float frontZ, float posX, float posY, float posZ) {
        BehemironEffekseerCommonNative.managerSetCameraParameter(handle, frontX, frontY, frontZ, posX, posY, posZ);
    }

    /**
     * 设置 4x4 投影矩阵。
     *
     * @param values16 16 个 float，按 Native 接口顺序传入
     */
    public void setProjectionMatrix(float[] values16) {
        setMatrix16(values16, true);
    }

    /**
     * 设置 4x4 投影矩阵。
     *
     * @param matrix 4x4 投影矩阵
     */
    public void setProjectionMatrix(EffekseerMatrix44 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        setProjectionMatrix(matrix.toArray());
    }

    /**
     * 设置 4x4 相机矩阵。
     *
     * @param values16 16 个 float，按 Native 接口顺序传入
     */
    public void setCameraMatrix(float[] values16) {
        setMatrix16(values16, false);
    }

    /**
     * 设置 4x4 相机矩阵。
     *
     * @param matrix 4x4 相机矩阵
     */
    public void setCameraMatrix(EffekseerMatrix44 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        setCameraMatrix(matrix.toArray());
    }

    /**
     * 检查实例是否仍然存在。
     *
     * @param effectHandle 实例 handle
     * @return 是否存在
     */
    public boolean exists(int effectHandle) {
        return BehemironEffekseerCommonNative.managerExists(handle, effectHandle) != 0;
    }

    /**
     * 根据窗口尺寸设置一个简单的 view-projection 矩阵。
     *
     * @param windowWidth  窗口宽度
     * @param windowHeight 窗口高度
     */
    public void setViewProjectionMatrixWithSimpleWindow(int windowWidth, int windowHeight) {
        BehemironEffekseerCommonNative.managerSetViewProjectionMatrixWithSimpleWindow(handle, windowWidth, windowHeight);
    }

    /**
     * 设置实例动态输入值。
     *
     * @param effectHandle 实例 handle
     * @param index        动态输入索引
     * @param value        动态输入值
     */
    public void setDynamicInput(int effectHandle, int index, float value) {
        ensureOpen();
        BehemironEffekseerCommonNative.managerSetDynamicInput(handle, effectHandle, index, value);
    }

    /**
     * 获取实例动态输入值。
     *
     * @param effectHandle 实例 handle
     * @param index        动态输入索引
     * @return 当前动态输入值
     */
    public float getDynamicInput(int effectHandle, int index) {
        ensureOpen();
        return BehemironEffekseerCommonNative.managerGetDynamicInput(handle, effectHandle, index);
    }

    /**
     * 直接按 typed 变换参数设置实例的位置、旋转和缩放。
     *
     * @param effectHandle 实例 handle
     * @param transform    变换参数
     */
    public void setTransform(int effectHandle, EffekseerTransform transform) {
        Objects.requireNonNull(transform, "transform");
        setEffectPosition(effectHandle, transform.position().x(), transform.position().y(), transform.position().z());
        setEffectRotation(effectHandle, transform.rotation().x(), transform.rotation().y(), transform.rotation().z());
        setEffectScale(effectHandle, transform.scale().x(), transform.scale().y(), transform.scale().z());
    }

    /**
     * 批量播放特效。
     *
     * @param effects 要播放的特效列表
     * @param options 对应的播放参数列表
     * @return 返回各项对应的实例 handle
     */
    public int[] playBatch(EffekseerEffect[] effects, EffekseerPlayOptions[] options) {
        ensureOpen();
        Objects.requireNonNull(effects, "effects");
        Objects.requireNonNull(options, "options");
        if (effects.length != options.length) {
            throw new IllegalArgumentException("effects 与 options 长度必须一致");
        }
        if (effects.length == 0) {
            return new int[0];
        }

        try (BehemironEffekseerBatchPlayItemNative items = new BehemironEffekseerBatchPlayItemNative(effects.length);
             IntPointer outHandles = new IntPointer(effects.length)) {
            for (int i = 0; i < effects.length; i++) {
                Objects.requireNonNull(effects[i], "effects[" + i + "]");
                Objects.requireNonNull(options[i], "options[" + i + "]");
                var item = items.position(i);
                item.effect_handle(effects[i].handle());
                fillNativeOptions(item.options(), options[i]);
            }
            items.position(0);
            BehemironEffekseerCommonNative.managerBatchPlay(handle, items, effects.length, outHandles);
            int[] result = new int[effects.length];
            outHandles.get(result, 0, effects.length);
            return result;
        }
    }

    /**
     * 批量设置实例变换。
     *
     * @param effectHandles 实例 handle 列表
     * @param transforms    对应变换列表
     */
    public void setTransformBatch(int[] effectHandles, EffekseerTransform[] transforms) {
        ensureOpen();
        Objects.requireNonNull(effectHandles, "effectHandles");
        Objects.requireNonNull(transforms, "transforms");
        if (effectHandles.length != transforms.length) {
            throw new IllegalArgumentException("effectHandles 与 transforms 长度必须一致");
        }
        if (effectHandles.length == 0) {
            return;
        }

        try (BehemironEffekseerBatchTransformItemNative items = new BehemironEffekseerBatchTransformItemNative(effectHandles.length)) {
            for (int i = 0; i < effectHandles.length; i++) {
                Objects.requireNonNull(transforms[i], "transforms[" + i + "]");
                var item = items.position(i);
                item.effect_handle(effectHandles[i]);
                item.position_x(transforms[i].position().x());
                item.position_y(transforms[i].position().y());
                item.position_z(transforms[i].position().z());
                item.rotation_x(transforms[i].rotation().x());
                item.rotation_y(transforms[i].rotation().y());
                item.rotation_z(transforms[i].rotation().z());
                item.scale_x(transforms[i].scale().x());
                item.scale_y(transforms[i].scale().y());
                item.scale_z(transforms[i].scale().z());
            }
            items.position(0);
            BehemironEffekseerCommonNative.managerBatchSetTransform(handle, items, effectHandles.length);
        }
    }

    /**
     * 批量设置实例 dynamic input。
     *
     * @param effectHandles 实例 handle 列表
     * @param inputIndices  对应输入索引列表
     * @param values        对应值列表
     */
    public void setDynamicInputBatch(int[] effectHandles, int[] inputIndices, float[] values) {
        ensureOpen();
        Objects.requireNonNull(effectHandles, "effectHandles");
        Objects.requireNonNull(inputIndices, "inputIndices");
        Objects.requireNonNull(values, "values");
        if (effectHandles.length != inputIndices.length || effectHandles.length != values.length) {
            throw new IllegalArgumentException("effectHandles、inputIndices、values 长度必须一致");
        }
        if (effectHandles.length == 0) {
            return;
        }

        try (BehemironEffekseerBatchDynamicInputItemNative items = new BehemironEffekseerBatchDynamicInputItemNative(effectHandles.length)) {
            for (int i = 0; i < effectHandles.length; i++) {
                var item = items.position(i);
                item.effect_handle(effectHandles[i]);
                item.input_index(inputIndices[i]);
                item.value(values[i]);
            }
            items.position(0);
            BehemironEffekseerCommonNative.managerBatchSetDynamicInput(handle, items, effectHandles.length);
        }
    }

    /**
     * 设置世界碰撞回调，使 Effekseer 能把内部碰撞检测委托给宿主世界。
     *
     * @param callback 世界碰撞回调；传入 {@code null} 表示清除
     */
    public void setWorldCollisionCallback(EffekseerWorldCollisionCallback callback) {
        ensureOpen();
        worldCollisionCallback = callback;
        if (callback == null) {
            nativeCollisionCallback = null;
            BehemironEffekseerCommonNative.managerSetCollisionCallback(handle, null, null);
            return;
        }

        nativeCollisionCallback = new BehemironEffekseerCollisionCallbackNative() {
            @Override
            public int call(
                    com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerCollisionQueryNative query,
                    com.behemiron.engine.external.effect.effekseer.internal.raw.common.BehemironEffekseerCollisionHitNative hit,
                    Pointer userData
            ) {
                EffekseerCollisionHit result = worldCollisionCallback.collide(new EffekseerCollisionQuery(
                        query.start_x(),
                        query.start_y(),
                        query.start_z(),
                        query.end_x(),
                        query.end_y(),
                        query.end_z()
                ));
                if (result == null) {
                    return 0;
                }
                hit.position_x(result.positionX());
                hit.position_y(result.positionY());
                hit.position_z(result.positionZ());
                hit.normal_x(result.normalX());
                hit.normal_y(result.normalY());
                hit.normal_z(result.normalZ());
                hit.distance(result.distance());
                hit.user_flags(result.userFlags());
                return 1;
            }
        };
        BehemironEffekseerCommonNative.managerSetCollisionCallback(handle, nativeCollisionCallback, null);
    }

    /**
     * 启动 manager 的 worker 线程。
     *
     * @param workerCount 线程数量
     */
    public void launchWorkerThreads(int workerCount) {
        BehemironEffekseerCommonNative.managerLaunchWorkerThreads(handle, workerCount);
    }

    /**
     * 清除当前绑定的背景资源。
     */
    public void unsetBackground() {
        BehemironEffekseerCommonNative.managerUnsetBackground(handle);
    }

    /**
     * 清除当前绑定的深度资源。
     */
    public void unsetDepth() {
        BehemironEffekseerCommonNative.managerUnsetDepth(handle);
    }

    /**
     * 获取指定实例当前的粒子数量。
     *
     * @param effectHandle 实例 handle
     * @return 粒子数量
     */
    public int getInstanceCount(int effectHandle) {
        return BehemironEffekseerCommonNative.managerGetInstanceCount(handle, effectHandle);
    }

    /**
     * 获取当前 manager 中全部实例的总粒子数量。
     *
     * @return 总粒子数量
     */
    public int getTotalInstanceCount() {
        return BehemironEffekseerCommonNative.managerGetTotalInstanceCount(handle);
    }

    /**
     * 获取当前 manager 的统计信息。
     *
     * @return manager 统计信息
     */
    public EffekseerManagerStats getStats() {
        ensureOpen();
        var nativeStats = new BehemironEffekseerManagerStatsNative();
        if (BehemironEffekseerCommonNative.managerGetStats(handle, nativeStats) == 0) {
            throw new IllegalStateException("获取 Effekseer manager stats 失败");
        }
        return new EffekseerManagerStats(
                nativeStats.total_instance_count(),
                nativeStats.rest_instance_count(),
                nativeStats.update_time(),
                nativeStats.draw_time(),
                nativeStats.gpu_time(),
                nativeStats.draw_call_count(),
                nativeStats.draw_vertex_count()
        );
    }

    /**
     * 释放底层 Native manager。
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        BehemironEffekseerCommonNative.managerSetCollisionCallback(handle, null, null);
        nativeCollisionCallback = null;
        worldCollisionCallback = null;
        BehemironEffekseerCommonNative.managerDestroy(handle);
    }

    /**
     * 把 Java 矩阵数组转换为 Native 所需的连续内存，并写入对应矩阵。
     *
     * @param values16   16 个 float
     * @param projection {@code true} 表示写入投影矩阵，{@code false} 表示写入相机矩阵
     */
    private void setMatrix16(float[] values16, boolean projection) {
        ensureOpen();
        Objects.requireNonNull(values16, "values16");
        if (values16.length != 16) {
            throw new IllegalArgumentException("matrix 需要 16 个 float");
        }
        try (FloatPointer pointer = new FloatPointer(values16)) {
            if (projection) {
                BehemironEffekseerCommonNative.managerSetProjectionMatrix(handle, pointer);
            } else {
                BehemironEffekseerCommonNative.managerSetCameraMatrix(handle, pointer);
            }
        }
    }

    private static BehemironEffekseerPlayOptionsNative toNative(EffekseerPlayOptions options) {
        var nativeOptions = new BehemironEffekseerPlayOptionsNative();
        fillNativeOptions(nativeOptions, options);
        return nativeOptions;
    }

    private static void fillNativeOptions(BehemironEffekseerPlayOptionsNative nativeOptions, EffekseerPlayOptions options) {
        nativeOptions.position_x(options.transform().position().x());
        nativeOptions.position_y(options.transform().position().y());
        nativeOptions.position_z(options.transform().position().z());
        nativeOptions.rotation_x(options.transform().rotation().x());
        nativeOptions.rotation_y(options.transform().rotation().y());
        nativeOptions.rotation_z(options.transform().rotation().z());
        nativeOptions.scale_x(options.transform().scale().x());
        nativeOptions.scale_y(options.transform().scale().y());
        nativeOptions.scale_z(options.transform().scale().z());
        nativeOptions.start_frame(options.startFrame());
    }

    private void ensureOpen() {
        if (closed) {
            throw new IllegalStateException("EffekseerManager 已关闭");
        }
    }
}
