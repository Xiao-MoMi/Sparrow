package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.type.tuple.Triplet;

public class LookPlayerCommand extends AbstractCommandFeature<CommandSender> {

	@Override
	public String getFeatureID() {
		return "look_player";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
		return builder
				.senderType(Player.class)
				.requiredArgumentTriplet("location", "x", DoubleParser.doubleParser(), "y", DoubleParser.doubleParser(), "z", DoubleParser.doubleParser(), Description.EMPTY)
				.handler(commandContext -> {

					Triplet<Double, Double, Double> xyz = commandContext.get("location");

					Player sender = commandContext.sender();
					EntityUtils.look(commandContext.sender(), new Location(sender.getWorld(), xyz.first(), xyz.second(), xyz.third()));

					SparrowBukkitPlugin.getInstance().getSenderFactory()
							.wrap(commandContext.sender())
							.sendMessage(
									TranslationManager.render(
											MessageConstants.COMMANDS_PLAYER_LOOK_SUCCESS
													.arguments(Component.text(xyz.first() + " " + xyz.second() + " " + xyz.third()))
													.build()
									),
									true
							);
				});
	}
}
