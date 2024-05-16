package net.momirealms.sparrow.common.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.sender.SenderFactory;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;

public abstract class AbstractCommandFeature<C> implements CommandFeature<C> {

    protected final SparrowCommandManager<C> sparrowCommandManager;
    protected CommandConfig<C> commandConfig;

    public AbstractCommandFeature(SparrowCommandManager<C> sparrowCommandManager) {
        this.sparrowCommandManager = sparrowCommandManager;
    }

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
    @SuppressWarnings("unchecked")
    public void handleFeedback(CommandContext<?> context, TranslatableComponent.Builder key, Component... args) {
        if (context.flags().hasFlag(SparrowFlagKeys.SILENT_FLAG)) {
            return;
        }
        sparrowCommandManager.handleCommandFeedback((C) context.sender(), key, args);
    }

    @Override
    public void handleFeedback(C sender, TranslatableComponent.Builder key, Component... args) {
        sparrowCommandManager.handleCommandFeedback(sender, key, args);
    }

    @Override
    public SparrowCommandManager<C> getSparrowCommandManager() {
        return sparrowCommandManager;
    }

    @Override
    public CommandConfig<C> getCommandConfig() {
        return commandConfig;
    }

    public void setCommandConfig(CommandConfig<C> commandConfig) {
        this.commandConfig = commandConfig;
    }
}
