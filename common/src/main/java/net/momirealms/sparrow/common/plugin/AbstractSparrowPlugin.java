package net.momirealms.sparrow.common.plugin;

import net.momirealms.sparrow.common.config.ConfigManager;
import net.momirealms.sparrow.common.config.ConfigManagerImpl;
import net.momirealms.sparrow.common.dependency.Dependency;
import net.momirealms.sparrow.common.dependency.DependencyManager;
import net.momirealms.sparrow.common.dependency.DependencyManagerImpl;

import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractSparrowPlugin implements SparrowPlugin {

    private DependencyManager dependencyManager;
    private ConfigManager configManager;

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public final void load() {
        // load dependencies
        this.dependencyManager = createDependencyManager();
        this.dependencyManager.loadDependencies(getGlobalDependencies());
    }

    public abstract void reload();

    public void enable() {
        this.setupConfigManager();
        this.setupSenderFactory();
        this.setupCommands();
    }

    public void disable() {
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    protected Set<Dependency> getGlobalDependencies() {
        return EnumSet.of(
                Dependency.ADVENTURE_API,
                Dependency.CLOUD_CORE,
                Dependency.CLOUD_BRIGADIER,
                Dependency.CLOUD_SERVICES,
                Dependency.CLOUD_MINECRAFT_EXTRAS,
                Dependency.BOOSTED_YAML
        );
    }

    protected abstract void setupSenderFactory();

    protected abstract void setupCommands();

    private void setupConfigManager() {
        this.configManager = new ConfigManagerImpl(this);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
