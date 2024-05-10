package net.momirealms.sparrow.common.plugin;

import net.momirealms.sparrow.common.config.ConfigManager;
import net.momirealms.sparrow.common.config.ConfigManagerImpl;
import net.momirealms.sparrow.common.dependency.Dependency;
import net.momirealms.sparrow.common.dependency.DependencyManager;
import net.momirealms.sparrow.common.dependency.DependencyManagerImpl;
import net.momirealms.sparrow.common.event.EventManager;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.storage.StorageFactory;

import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractSparrowPlugin implements SparrowPlugin {

    private DependencyManager dependencyManager;
    private ConfigManager configManager;
    private TranslationManager translationManager;
    private EventManager eventManager;

    @Override
    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public final void load() {
        // load dependencies
        this.getBootstrap().getPluginLogger().info("Loading libraries. Please wait...");
        this.dependencyManager = createDependencyManager();
        this.dependencyManager.loadDependencies(getGlobalDependencies());
    }

    public void reload() {
        this.translationManager.reload();
    }

    public void enable() {
        this.setupConfigManager();
        this.setupTranslations();
        this.setupSenderFactory();
        this.setupCommandManager();
        StorageFactory storageFactory = new StorageFactory(this);
        this.eventManager = EventManager.create(this);
    }

    protected void setupTranslations() {
        this.translationManager = new TranslationManager(this);
    }

    public void disable() {
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    protected Set<Dependency> getGlobalDependencies() {
        return EnumSet.of(
                Dependency.GSON,
                Dependency.CAFFEINE,
                Dependency.GEANTY_REF,
                Dependency.ADVENTURE_API,
                Dependency.CLOUD_CORE,
                Dependency.CLOUD_BRIGADIER,
                Dependency.CLOUD_SERVICES,
                Dependency.CLOUD_MINECRAFT_EXTRAS,
                Dependency.BOOSTED_YAML,
                Dependency.BYTEBUDDY,
                Dependency.BSTATS_BASE,
                Dependency.MARIADB_DRIVER,
                Dependency.MYSQL_DRIVER,
                Dependency.HIKARI_CP,
                Dependency.COMMONS_POOL_2,
                Dependency.LETTUCE,
                Dependency.MONGODB_DRIVER_BSON,
                Dependency.MONGODB_DRIVER_CORE,
                Dependency.MONGODB_DRIVER_SYNC,
                Dependency.SQLITE_DRIVER,
                Dependency.H2_DRIVER
        );
    }

    protected abstract void setupSenderFactory();

    protected abstract void setupCommandManager();

    private void setupConfigManager() {
        this.configManager = new ConfigManagerImpl(this);
    }

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public EventManager getEventDispatcher() {
        return this.eventManager;
    }
}
