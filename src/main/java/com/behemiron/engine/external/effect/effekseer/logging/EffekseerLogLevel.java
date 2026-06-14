package com.behemiron.engine.external.effect.effekseer.logging;

/**
 * 表示 Native 层日志等级。
 */
public enum EffekseerLogLevel {
    INFO(0),
    WARNING(1),
    ERROR(2),
    DEBUG(3);

    private final int nativeValue;

    EffekseerLogLevel(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    /**
     * 返回 Native 层日志等级值。
     *
     * @return Native 层日志等级值
     */
    public int nativeValue() {
        return nativeValue;
    }

    /**
     * 根据 Native 层值解析日志等级。
     *
     * @param value Native 层日志等级值
     * @return 对应日志等级
     */
    public static EffekseerLogLevel fromNative(int value) {
        for (EffekseerLogLevel level : values()) {
            if (level.nativeValue == value) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知的日志等级: " + value);
    }
}
