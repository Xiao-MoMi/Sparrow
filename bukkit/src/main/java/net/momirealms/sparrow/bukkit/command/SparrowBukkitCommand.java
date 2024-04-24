package net.momirealms.sparrow.bukkit.command;

import com.google.common.base.Preconditions;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.feature.WorkbenchAdminCommand;
import net.momirealms.sparrow.bukkit.command.feature.WorkbenchPlayerCommand;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.List;

public class SparrowBukkitCommand {

    private final List<CommandFeature> FEATURES = List.of(
        new WorkbenchPlayerCommand(),
        new WorkbenchAdminCommand()
    );

    private final SparrowBukkitPlugin plugin;
    private final PaperCommandManager<CommandSender> manager;

    public SparrowBukkitCommand(SparrowBukkitPlugin plugin) {
        this.plugin = plugin;
        this.manager = new PaperCommandManager<>(
                plugin.getLoader(),
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        if (this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.manager.registerBrigadier();
        } else if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }
        this.registerCommandFeatures();
    }

    private void registerCommandFeatures() {
        YamlDocument document = plugin.getConfigManager().loadConfig("commands.yml");
        FEATURES.forEach(feature -> feature.registerFeature(this.plugin, this.manager,
                Preconditions.checkNotNull(getCommandConfigFromDocument(document, feature.getFeatureID()), feature.getFeatureID() + " doesn't exist in commands.yml")));
    }

    private CommandConfig getCommandConfigFromDocument(YamlDocument document, String id) {
        Section section = document.getSection(id);
        if (section == null) return null;
        return new CommandConfig(
                section.getBoolean("enable", false),
                section.getStringList("usage"),
                section.getString("permission")
        );
    }
}
