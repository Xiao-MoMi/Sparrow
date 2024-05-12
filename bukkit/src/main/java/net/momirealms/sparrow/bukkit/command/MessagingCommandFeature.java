package net.momirealms.sparrow.bukkit.command;

import net.momirealms.sparrow.bukkit.command.handler.SparrowMessagingHandler;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public abstract class MessagingCommandFeature<C extends CommandSender> extends AbstractCommandFeature<C> {

    @Override
    @SuppressWarnings("unchecked")
    public Command<C> registerCommand(CommandManager<C> manager, Command.Builder<C> builder) {
        Command<C> command = (Command<C>) assembleCommand(manager, builder)
                .appendHandler(SparrowMessagingHandler.instance())
                .build();
        manager.command(command);
        return command;
    }
}
