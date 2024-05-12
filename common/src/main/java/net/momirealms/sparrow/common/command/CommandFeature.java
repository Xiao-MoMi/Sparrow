package net.momirealms.sparrow.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.apache.logging.log4j.util.TriConsumer;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

public interface CommandFeature<C> {

    Command<C> registerCommand(CommandManager<C> cloudCommandManager, Command.Builder<C> builder);

    String getFeatureID();

    void registerRelatedFunctions();

    void unregisterRelatedFunctions();

    void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args);

    void feedbackConsumer(TriConsumer<CommandContext<?>, TranslatableComponent.Builder, List<Component>> consumer);
}
