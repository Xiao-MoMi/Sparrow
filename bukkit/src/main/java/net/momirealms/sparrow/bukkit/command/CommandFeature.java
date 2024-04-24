package net.momirealms.sparrow.bukkit.command;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public interface CommandFeature {

    void registerFeature(SparrowBukkitPlugin plugin, BukkitCommandManager<CommandSender> manager, CommandConfig commandConfig);

    String getFeatureID();
}
