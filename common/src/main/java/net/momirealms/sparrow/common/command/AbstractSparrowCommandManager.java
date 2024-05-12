package net.momirealms.sparrow.common.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.locale.SparrowCaptionFormatter;
import net.momirealms.sparrow.common.locale.SparrowCaptionProvider;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.sender.Sender;
import net.momirealms.sparrow.common.util.ArrayUtils;
import org.apache.logging.log4j.util.TriConsumer;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractSparrowCommandManager<C> implements SparrowCommandManager<C> {

    protected final HashSet<CommandComponent<C>> registeredRootCommandComponents = new HashSet<>();
    protected final HashSet<CommandFeature<C>> registeredFeatures = new HashSet<>();
    protected final CommandManager<C> commandManager;
    protected final SparrowPlugin plugin;
    private TriConsumer<C, TranslatableComponent.Builder, List<Component>> feedbackConsumer;

    public AbstractSparrowCommandManager(SparrowPlugin plugin, CommandManager<C> commandManager) {
        this.commandManager = commandManager;
        this.plugin = plugin;
        this.injectLocales();
        this.feedbackConsumer = defaultFeedbackConsumer();
    }

    public void setFeedbackConsumer(@NotNull TriConsumer<C, TranslatableComponent.Builder, List<Component>> feedbackConsumer) {
        this.feedbackConsumer = feedbackConsumer;
    }

    public TriConsumer<C, TranslatableComponent.Builder, List<Component>> defaultFeedbackConsumer() {
        return  ((sender, builder, components) -> {
            wrapSender(sender).sendMessage(
                    TranslationManager.render(builder.arguments(components).build()),
                    true
            );
        });
    }

    protected abstract Audience wrapAudience(C c);

    protected abstract Sender wrapSender(C c);

    private void injectLocales() {
        MinecraftExceptionHandler.<C>create(this::wrapAudience)
                .defaultHandlers()
                .captionFormatter(new SparrowCaptionFormatter<>())
                .registerTo(getCommandManager());
        getCommandManager().captionRegistry().registerProvider(new SparrowCaptionProvider<>());
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
    public void registerFeature(CommandFeature<C> feature, CommandConfig<C> config) {
        if (!config.isEnable()) throw new RuntimeException("Registering a disabled command feature is not allowed");
        for (Command.Builder<C> builder : buildCommandBuilders(config)) {
            Command<C> command = feature.registerCommand(commandManager, builder);
            this.registeredRootCommandComponents.add(command.rootComponent());
        }
        feature.registerRelatedFunctions();
        this.registeredFeatures.add(feature);
    }

    @Override
    public void registerDefaultFeatures() {
        YamlDocument document = plugin.getConfigManager().loadConfig(commandsFile);
        this.getFeatures().values().forEach(feature -> {
            CommandConfig<C> config = getCommandConfig(document, feature.getFeatureID());
            if (config.isEnable()) {
                registerFeature(feature, config);
            }
        });
    }

    @Override
    public void unregisterFeatures() {
        this.registeredRootCommandComponents.forEach(component -> this.commandManager.commandRegistrationHandler().unregisterRootCommand(component));
        this.registeredRootCommandComponents.clear();
        this.registeredFeatures.forEach(CommandFeature::unregisterRelatedFunctions);
        this.registeredFeatures.clear();
    }

    @Override
    public CommandManager<C> getCommandManager() {
        return commandManager;
    }

    @Override
    public void handleCommandFeedback(C sender, TranslatableComponent.Builder key, Component... args) {
        this.feedbackConsumer.accept(sender, key, List.of(args));
    }
}
