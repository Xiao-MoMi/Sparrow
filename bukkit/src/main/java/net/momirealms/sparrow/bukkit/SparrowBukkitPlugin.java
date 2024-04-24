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
        dependencies.add(Dependency.PACKET_EVENT_SPIGOT);
        dependencies.add(Dependency.CLOUD_BUKKIT);
        dependencies.add(Dependency.CLOUD_PAPER);
        dependencies.add(Dependency.INVENTORY_ACCESS);
        dependencies.add(Dependency.INVENTORY_ACCESS_NMS.setArtifactID(getInventoryAccessArtifact()).setArtifact(getInventoryAccessArtifact()));
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

    private String getInventoryAccessArtifact() {
        String version = bootstrap.getServerVersion();
        String artifact;
        switch (version) {
            case "1.17.1" -> artifact = "r9";
            case "1.18.1" -> artifact = "r10";
            case "1.18.2" -> artifact = "r11";
            case "1.19.1", "1.19.2" -> artifact = "r13";
            case "1.19.3" -> artifact = "r14";
            case "1.19.4" -> artifact = "r15";
            case "1.20.1" -> artifact = "r16";
            case "1.20.2" -> artifact = "r17";
            case "1.20.3", "1.20.4" -> artifact = "r18";
            case "1.20.5" -> artifact = "r19";
            default -> throw new RuntimeException("Unsupported version: " + version);
        }
        return String.format("inventory-access-%s", artifact);
    }
}
