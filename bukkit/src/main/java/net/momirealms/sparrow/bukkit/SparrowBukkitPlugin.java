package net.momirealms.sparrow.bukkit;

import net.momirealms.sparrow.bukkit.feature.enchant.SparrowBukkitEnchantManager;
import net.momirealms.sparrow.bukkit.feature.proxy.SparrowBukkitBungeeManager;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.NBTUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.dependency.Dependency;
import net.momirealms.sparrow.common.plugin.plugin.AbstractSparrowPlugin;
import net.momirealms.sparrow.common.sender.SenderFactory;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SparrowBukkitPlugin extends AbstractSparrowPlugin {

    private static SparrowBukkitPlugin plugin;
    private final SparrowBukkitBootstrap bootstrap;
    private final SparrowBukkitBungeeManager bungeeManager;
    private SparrowBukkitSkullManager skullManager;
    private SparrowBukkitSenderFactory senderFactory;
    private SparrowBukkitCommandManager commandManager;
    private SparrowBukkitEnchantManager enchantManager;

    public SparrowBukkitPlugin(SparrowBukkitBootstrap bootstrap) {
        plugin = this;
        this.bootstrap = bootstrap;
        this.bungeeManager = new SparrowBukkitBungeeManager(this);
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        Set<Dependency> dependencies = super.getGlobalDependencies();
        dependencies.add(Dependency.BSTATS_BUKKIT);
        dependencies.add(Dependency.NBT_API);
        dependencies.add(Dependency.CLOUD_BUKKIT);
        dependencies.add(Dependency.CLOUD_PAPER);
        dependencies.add(Dependency.SPARROW_HEART);
        return dependencies;
    }

    @Override
    public void reload() {
        super.reload();
        this.enchantManager.reload();
    }

    @Override
    public void enable() {
        super.enable();
        this.skullManager = new SparrowBukkitSkullManager(this);
        this.enchantManager = new SparrowBukkitEnchantManager(this);
        NBTUtils.disableNBTAPILogs();
        new Metrics(getLoader(), 21789);
    }

    @Override
    public void disable() {
        this.commandManager.unregisterCommandFeatures();
        this.bungeeManager.disable();
        this.enchantManager.disable();
        this.skullManager.disable();
        super.disable();
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SparrowBukkitSenderFactory(this);
    }

    @Override
    protected void setupCommandManager() {
        this.commandManager = new SparrowBukkitCommandManager(this);
        this.commandManager.registerCommandFeatures();
    }

    public JavaPlugin getLoader() {
        return this.bootstrap.getLoader();
    }

    public static SparrowBukkitPlugin getInstance() {
        return plugin;
    }

    public SparrowBukkitBungeeManager getBungeeManager() {
        return bungeeManager;
    }

    public SenderFactory<SparrowBukkitPlugin, CommandSender> getSenderFactory() {
        return senderFactory;
    }

    @Override
    public SparrowBukkitBootstrap getBootstrap() {
        return bootstrap;
    }

    public SparrowBukkitSkullManager getSkullManager() {
        return skullManager;
    }

    public SparrowCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public SparrowBukkitEnchantManager getEnchantManager() {
        return enchantManager;
    }
}
