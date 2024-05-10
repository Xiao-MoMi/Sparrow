package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class CondenseAdminCommand extends AbstractCommandFeature<CommandSender> {

	@Override
	public String getFeatureID() {
		return "condense_admin";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
		return null;
	}
}
