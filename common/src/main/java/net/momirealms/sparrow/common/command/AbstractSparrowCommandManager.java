package net.momirealms.sparrow.common.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.momirealms.sparrow.common.locale.SparrowCaptionFormatter;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.util.ArrayUtils;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractSparrowCommandManager<C> implements SparrowCommandManager<C> {

    protected final HashSet<CommandComponent<C>> registeredRootCommandComponents = new HashSet<>();
    protected final HashSet<CommandFeature<C>> registeredFeatures = new HashSet<>();
    protected final CommandManager<C> commandManager;
    protected final SparrowPlugin plugin;

    public AbstractSparrowCommandManager(SparrowPlugin plugin, CommandManager<C> commandManager) {
        this.commandManager = commandManager;
        this.plugin = plugin;
        this.injectLocales();
    }

    protected abstract List<CommandFeature<C>> getFeatureList();

    public void injectLocales() {
        MinecraftExceptionHandler.<C>create(c -> plugin.getSenderFactory().getAudience(c))
                .defaultHandlers()
                .captionFormatter(new SparrowCaptionFormatter<>())
                .registerTo(getCommandManager());
    }

    @Override
    public CommandConfig<C> getCommandConfig(YamlDocument document, String featureID) {
        Section section = document.getSection(featureID);
        if (section == null) return null;
        return new CommandConfig.Builder<C>()
                .permission(section.getString("permission"))
                .usages(section.getStringList("usage"))
                .enable(section.getBoolean("enable", false))
                .build();
    }

    @Override
    public Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig<C> config) {
        ArrayList<Command.Builder<C>> list = new ArrayList<>();
        for (String usage : config.getUsages()) {
            if (!usage.startsWith("/")) continue;
            String command = usage.substring(1).trim();
            String[] split = command.split(" ");
            Command.Builder<C> builder = new CommandBuilder.BasicCommandBuilder<>(getCommandManager(), split[0])
                    .setCommandNode(ArrayUtils.subArray(split, 1))
                    .setPermission(config.getPermission())
                    .getBuiltCommandBuilder();
            list.add(builder);
        }
        return list;
    }

    @Override
    public void registerCommand(CommandFeature<C> feature, CommandConfig<C> config) {
        for (Command.Builder<C> builder : buildCommandBuilders(config)) {
            Command<C> command = feature.registerCommand(commandManager, builder);
            this.registeredRootCommandComponents.add(command.rootComponent());
        }
        feature.registerRelatedFunctions();
        this.registeredFeatures.add(feature);
    }

    @Override
    public void registerCommandFeatures() {
        YamlDocument document = plugin.getConfigManager().loadConfig("commands.yml");
        this.getFeatureList().forEach(feature -> {
            CommandConfig<C> config = getCommandConfig(document, feature.getFeatureID());
            if (config.isEnable()) {
                registerCommand(feature, config);
            }
        });
    }

    @Override
    public void unregisterCommandFeatures() {
        this.registeredRootCommandComponents.forEach(component -> this.commandManager.commandRegistrationHandler().unregisterRootCommand(component));
        this.registeredRootCommandComponents.clear();
        this.registeredFeatures.forEach(CommandFeature::unregisterRelatedFunctions);
        this.registeredFeatures.clear();
    }

    @Override
    public CommandManager<C> getCommandManager() {
        return commandManager;
    }
}
