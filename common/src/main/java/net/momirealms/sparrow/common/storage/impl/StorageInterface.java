package net.momirealms.sparrow.common.storage.impl;

import net.momirealms.sparrow.common.plugin.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.storage.StorageType;

public interface StorageInterface {
    SparrowPlugin getPlugin();

    StorageType getType();

    void init();

    void shutdown();
}
