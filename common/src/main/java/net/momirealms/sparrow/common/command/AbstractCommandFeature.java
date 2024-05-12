package net.momirealms.sparrow.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.sender.SenderFactory;
import org.apache.logging.log4j.util.TriConsumer;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

import java.util.List;

public abstract class AbstractCommandFeature<C> implements CommandFeature<C> {

    @SuppressWarnings("unchecked")
    private TriConsumer<CommandContext<?>, TranslatableComponent.Builder, List<Component>> feedbackConsumer = ((context, builder, components) -> {
        if (context.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG)) {
            return;
        }
        this.getSenderFactory()
                .wrap((C) context.sender())
                .sendMessage(
                        TranslationManager.render(builder.arguments(components).build()),
                        true
                );
    });

    protected abstract SenderFactory<?, C> getSenderFactory();

    public abstract Command.Builder<? extends C> assembleCommand(CommandManager<C> manager, Command.Builder<C> builder);

    @Override
    @SuppressWarnings("unchecked")
    public Command<C> registerCommand(CommandManager<C> manager, Command.Builder<C> builder) {
        Command<C> command = (Command<C>) assembleCommand(manager, builder).build();
        manager.command(command);
        return command;
    }

    @Override
    public void registerRelatedFunctions() {
        // empty
    }

    @Override
    public void unregisterRelatedFunctions() {
        // empty
    }

    @Override
    public void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args) {
        feedbackConsumer.accept(context, key, List.of(args));
    }

    @Override
    public void feedbackConsumer(TriConsumer<CommandContext<?>, TranslatableComponent.Builder, List<Component>> consumer) {
        this.feedbackConsumer = consumer;
    }
}
