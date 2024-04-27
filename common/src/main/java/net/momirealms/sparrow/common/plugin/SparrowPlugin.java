package net.momirealms.sparrow.common.plugin;

import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.config.ConfigManager;
import net.momirealms.sparrow.common.dependency.DependencyManager;
import net.momirealms.sparrow.common.plugin.bootstrap.SparrowBootstrap;
import net.momirealms.sparrow.common.sender.SenderFactory;

public interface SparrowPlugin {

    DependencyManager getDependencyManager();

    SparrowBootstrap getBootstrap();

    ConfigManager getConfigManager();

    <C> SparrowCommandManager<C> getCommandManager();

    <C, P extends SparrowPlugin> SenderFactory<P,C> getSenderFactory();
}
