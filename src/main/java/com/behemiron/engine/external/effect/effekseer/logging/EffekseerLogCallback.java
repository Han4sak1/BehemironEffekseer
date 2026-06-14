package com.behemiron.engine.external.effect.effekseer.logging;

import com.behemiron.engine.external.effect.effekseer.logging.EffekseerLogLevel;

/**
 * 用于接收 Native 层日志。
 */
@FunctionalInterface
public interface EffekseerLogCallback {

    /**
     * 处理一条 Native 日志。
     *
     * @param level 日志等级
     * @param message 日志内容
     */
    void onLog(EffekseerLogLevel level, String message);
}
