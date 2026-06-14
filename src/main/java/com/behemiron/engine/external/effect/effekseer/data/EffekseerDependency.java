package com.behemiron.engine.external.effect.effekseer.data;

/**
 * 表示特效依赖清单中的一个统一资源项。
 *
 * @param type 依赖类型
 * @param slot 同类资源中的槽位索引
 * @param path 资源路径；若原始资源未声明路径则可能为 {@code null}
 */
public record EffekseerDependency(
    EffekseerDependencyType type,
    int slot,
    String path
) {
}
