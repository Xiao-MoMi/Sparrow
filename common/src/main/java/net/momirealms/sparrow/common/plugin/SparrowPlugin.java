package net.momirealms.sparrow.common.plugin;

import net.momirealms.sparrow.common.dependency.DependencyManager;
import net.momirealms.sparrow.common.plugin.bootstrap.SparrowBootstrap;

public interface SparrowPlugin {

    DependencyManager getDependencyManager();

    SparrowBootstrap getBootstrap();
}
