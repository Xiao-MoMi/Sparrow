package net.momirealms.sparrow.common.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public abstract class AbstractCommandFeature<C> implements CommandFeature<C> {

    public abstract Command.Builder<? extends C> assembleCommand(CommandManager<C> manager, Command.Builder<C> builder);

    @Override
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
}
