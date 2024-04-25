package net.momirealms.sparrow.bukkit.command;

import net.momirealms.sparrow.bukkit.BukkitCommands;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public abstract class AbstractCommand implements CommandFeature {

    @Override
    public void registerFeature(BukkitCommands commands, BukkitCommandManager<CommandSender> manager, CommandConfig commandConfig) {
        var builders = commandConfig.builders(manager);
        for (Command.Builder<CommandSender> builder : builders) {
            var command = assembleCommand(manager, builder).build();
            commands.addCommandComponent(command.rootComponent());
            manager.command(command);
        }
        if (builders.size() != 0)
            registerRelatedFunctions();
    }

    public abstract Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder);

    public void registerRelatedFunctions() {}

    public void unregisterRelatedFunctions() {}
}
