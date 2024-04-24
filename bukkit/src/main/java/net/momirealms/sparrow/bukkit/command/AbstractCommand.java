package net.momirealms.sparrow.bukkit.command;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public abstract class AbstractCommand implements CommandFeature {

    @Override
    public void registerFeature(SparrowBukkitPlugin plugin, BukkitCommandManager<CommandSender> manager, CommandConfig commandConfig) {
        for (Command.Builder<CommandSender> builder : commandConfig.builders(manager)) {
            manager.command(assembleCommand(builder));
        }
    }

    public abstract Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder);
}
