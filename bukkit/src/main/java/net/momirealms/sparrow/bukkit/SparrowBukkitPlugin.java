package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.bukkit.command.SparrowBukkitCommand;
import net.momirealms.sparrow.common.config.ConfigManager;
import net.momirealms.sparrow.common.dependency.Dependency;
import net.momirealms.sparrow.common.plugin.AbstractSparrowPlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SparrowBukkitPlugin extends AbstractSparrowPlugin {

    private static SparrowBukkitPlugin plugin;
    private final SparrowBukkitBootstrap bootstrap;
    private BukkitSenderFactory senderFactory;
    private ConfigManager configManager;
    private SparrowBukkitCommand sparrowBukkitCommand;

    public SparrowBukkitPlugin(SparrowBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        plugin = this;
    }

    @Override
    public SparrowBukkitBootstrap getBootstrap() {
        return bootstrap;
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = super.getGlobalDependencies();
        dependencies.add(Dependency.NBT_API);
        dependencies.add(Dependency.CLOUD_BUKKIT);
        dependencies.add(Dependency.CLOUD_PAPER);
        return dependencies;
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }

    @Override
    protected void setupCommand() {
        this.sparrowBukkitCommand = new SparrowBukkitCommand(this);
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    public BukkitSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    public static SparrowBukkitPlugin getInstance() {
        return plugin;
    }
}
