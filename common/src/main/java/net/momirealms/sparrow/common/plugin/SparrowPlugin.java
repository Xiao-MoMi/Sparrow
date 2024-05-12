package net.momirealms.sparrow.common.plugin;

import net.momirealms.sparrow.common.config.ConfigManager;
import net.momirealms.sparrow.common.dependency.DependencyManager;
import net.momirealms.sparrow.common.event.EventManager;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.plugin.bootstrap.SparrowBootstrap;

public interface SparrowPlugin {

    DependencyManager getDependencyManager();

    SparrowBootstrap getBootstrap();

    TranslationManager getTranslationManager();

    ConfigManager getConfigManager();

    EventManager getEventManager();
}
