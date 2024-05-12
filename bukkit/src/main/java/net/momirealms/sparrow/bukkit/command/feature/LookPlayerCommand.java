package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.location.LocationParser;

public class LookPlayerCommand extends BukkitCommandFeature<CommandSender> {

	public LookPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
		super(sparrowCommandManager);
	}

	@Override
	public String getFeatureID() {
		return "look_player";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
		return builder
				.senderType(Player.class)
				.required("location", LocationParser.locationParser())
				.handler(commandContext -> {

					Location location = commandContext.get("location");

					EntityUtils.look(commandContext.sender(), location);

					handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_LOOK_SUCCESS, Component.text(
							(location.x() + " " + location.y() + " " + location.z()
					)));
				});
	}
}
