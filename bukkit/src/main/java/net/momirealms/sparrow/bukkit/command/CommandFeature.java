package net.momirealms.sparrow.bukkit.command;

import net.momirealms.sparrow.bukkit.BukkitCommands;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public interface CommandFeature {

    void registerFeature(BukkitCommands commands, BukkitCommandManager<CommandSender> manager, CommandConfig commandConfig);

    String getFeatureID();
}
