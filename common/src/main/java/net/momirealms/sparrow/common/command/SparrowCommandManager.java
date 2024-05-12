package net.momirealms.sparrow.common.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.util.Index;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Collection;

public interface SparrowCommandManager<C> {

    String commandsFile = "commands.yml";

    void unregisterFeatures();

    void registerFeature(CommandFeature<C> feature, CommandConfig<C> config);

    void registerDefaultFeatures();

    Index<String, CommandFeature<C>> getFeatures();

    CommandConfig<C> getCommandConfig(YamlDocument document, String featureID);

    Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig<C> config);

    CommandManager<C> getCommandManager();

    void handleCommandFeedback(C sender, TranslatableComponent.Builder key, Component... args);
}
