package com.behemiron.engine.external.effect.effekseer.runtime;

import com.behemiron.engine.external.effect.effekseer.math.EffekseerMatrix43;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerTransform;
import com.behemiron.engine.external.effect.effekseer.math.EffekseerVec3;

import java.util.Objects;

/**
 * 封装一个已经播放中的 Effekseer 实例，便于后续控制。
 */
public final class EffekseerInstance implements AutoCloseable {

    private final EffekseerManager manager;
    private final int handle;

    /**
     * 使用现有的 manager 和 Native 实例 handle 创建包装对象。
     *
     * @param manager 管理该实例的 manager
     * @param handle  Native 实例 handle
     */
    public EffekseerInstance(EffekseerManager manager, int handle) {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.handle = handle;
    }

    /**
     * 返回底层 Native 实例 handle。
     *
     * @return Native 实例 handle
     */
    public int handle() {
        return handle;
    }

    /**
     * 检查实例当前是否仍然存在。
     *
     * @return 是否存在
     */
    public boolean exists() {
        return manager.exists(handle);
    }

    /**
     * 停止当前实例。
     */
    public void stop() {
        manager.stop(handle);
    }

    /**
     * 设置实例暂停状态。
     *
     * @param paused 是否暂停
     */
    public void setPaused(boolean paused) {
        manager.setPaused(handle, paused);
    }

    /**
     * 设置实例显示状态。
     *
     * @param shown 是否显示
     */
    public void setShown(boolean shown) {
        manager.setShown(handle, shown);
    }

    /**
     * 向实例发送 trigger。
     *
     * @param index trigger 索引
     */
    public void sendTrigger(int index) {
        manager.sendTrigger(handle, index);
    }

    /**
     * 设置实例位置。
     *
     * @param x X 坐标
     * @param y Y 坐标
     * @param z Z 坐标
     */
    public void setPosition(float x, float y, float z) {
        manager.setEffectPosition(handle, x, y, z);
    }

    /**
     * 设置实例位置。
     *
     * @param position 位置向量
     */
    public void setPosition(EffekseerVec3 position) {
        Objects.requireNonNull(position, "position");
        setPosition(position.x(), position.y(), position.z());
    }

    /**
     * 设置实例旋转。
     *
     * @param x X 轴旋转
     * @param y Y 轴旋转
     * @param z Z 轴旋转
     */
    public void setRotation(float x, float y, float z) {
        manager.setEffectRotation(handle, x, y, z);
    }

    /**
     * 设置实例旋转。
     *
     * @param rotation 旋转向量
     */
    public void setRotation(EffekseerVec3 rotation) {
        Objects.requireNonNull(rotation, "rotation");
        setRotation(rotation.x(), rotation.y(), rotation.z());
    }

    /**
     * 设置实例缩放。
     *
     * @param x X 轴缩放
     * @param y Y 轴缩放
     * @param z Z 轴缩放
     */
    public void setScale(float x, float y, float z) {
        manager.setEffectScale(handle, x, y, z);
    }

    /**
     * 设置实例缩放。
     *
     * @param scale 缩放向量
     */
    public void setScale(EffekseerVec3 scale) {
        Objects.requireNonNull(scale, "scale");
        setScale(scale.x(), scale.y(), scale.z());
    }

    /**
     * 设置实例的 3x4 变换矩阵。
     *
     * @param values12 12 个 float，按 Native 接口顺序传入
     */
    public void setTransformMatrix(float... values12) {
        manager.setEffectTransformMatrix(handle, values12);
    }

    /**
     * 设置实例的 3x4 变换矩阵。
     *
     * @param matrix 3x4 矩阵
     */
    public void setTransformMatrix(EffekseerMatrix43 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        manager.setEffectTransformMatrix(handle, matrix);
    }

    /**
     * 设置实例的基础 3x4 变换矩阵。
     *
     * @param values12 12 个 float，按 Native 接口顺序传入
     */
    public void setTransformBaseMatrix(float... values12) {
        manager.setEffectTransformBaseMatrix(handle, values12);
    }

    /**
     * 设置实例的基础 3x4 变换矩阵。
     *
     * @param matrix 3x4 矩阵
     */
    public void setTransformBaseMatrix(EffekseerMatrix43 matrix) {
        Objects.requireNonNull(matrix, "matrix");
        manager.setEffectTransformBaseMatrix(handle, matrix);
    }

    /**
     * 一次性设置位置、旋转和缩放。
     *
     * @param transform 变换参数
     */
    public void setTransform(EffekseerTransform transform) {
        Objects.requireNonNull(transform, "transform");
        manager.setTransform(handle, transform);
    }

    /**
     * 设置实例所属 layer。
     *
     * @param layer layer 索引
     */
    public void setLayer(int layer) {
        manager.setLayer(handle, layer);
    }

    /**
     * 设置实例动态输入值。
     *
     * @param index 动态输入索引
     * @param value 动态输入值
     */
    public void setDynamicInput(int index, float value) {
        manager.setDynamicInput(handle, index, value);
    }

    /**
     * 获取实例动态输入值。
     *
     * @param index 动态输入索引
     * @return 当前动态输入值
     */
    public float getDynamicInput(int index) {
        return manager.getDynamicInput(handle, index);
    }

    /**
     * 获取当前实例的粒子数量。
     *
     * @return 当前粒子数量
     */
    public int getInstanceCount() {
        return manager.getInstanceCount(handle);
    }

    /**
     * 以 stop 的语义释放当前实例。
     */
    @Override
    public void close() {
        stop();
    }
}
