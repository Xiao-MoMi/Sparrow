package net.momirealms.sparrow.common.command;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.Collection;

public interface SparrowCommandManager<C> {

    void unregisterCommandFeatures();

    void registerCommand(CommandFeature<C> feature, CommandConfig<C> config);

    void registerCommandFeatures();

    CommandConfig<C> getCommandConfig(YamlDocument document, String featureID);

    Collection<Command.Builder<C>> buildCommandBuilders(CommandConfig<C> config);

    CommandManager<C> getCommandManager();
}
