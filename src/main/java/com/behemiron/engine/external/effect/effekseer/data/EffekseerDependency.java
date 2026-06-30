package com.behemiron.engine.external.effect.effekseer.data;

import java.util.Objects;

/**
 * 表示特效依赖清单中的一个统一资源项。
 */
public final class EffekseerDependency {

    private final EffekseerDependencyType type;
    private final int slot;
    private final String path;

    public EffekseerDependency(EffekseerDependencyType type, int slot, String path) {
        this.type = Objects.requireNonNull(type, "type");
        this.slot = slot;
        this.path = path;
    }

    public EffekseerDependencyType type() {
        return type;
    }

    public int slot() {
        return slot;
    }

    public String path() {
        return path;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EffekseerDependency that)) {
            return false;
        }
        return slot == that.slot
                && type == that.type
                && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, slot, path);
    }

    @Override
    public String toString() {
        return "EffekseerDependency[type=" + type + ", slot=" + slot + ", path=" + path + ']';
    }
}
