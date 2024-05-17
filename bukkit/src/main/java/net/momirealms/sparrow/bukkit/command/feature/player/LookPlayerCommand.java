package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.parser.standard.EnumParser;

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
				.flag(manager.flagBuilder("location").withComponent(LocationParser.locationParser()).build())
				.flag(manager.flagBuilder("face").withComponent(EnumParser.enumParser(LookAdminCommand.Face.class)).build())
				.handler(commandContext -> {
					boolean hasLocation = commandContext.flags().getValue("location").isPresent();
					if (hasLocation) {
						Location location = (Location) commandContext.flags().getValue("location").get();
						EntityUtils.look(commandContext.sender(), location);
						handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_LOOK_SUCCESS, Component.text(
								(location.x() + " " + location.y() + " " + location.z()
								)));
						return;
					}

					boolean hasFace = commandContext.flags().getValue("face").isPresent();
					if (hasFace) {
						LookAdminCommand.Face face = (LookAdminCommand.Face) commandContext.flags().getValue("face").get();
						TranslatableComponent.Builder component;
						switch (face) {
							case NORTH -> component = MessageConstants.NORTH;
							case NORTHEAST -> component = MessageConstants.NORTHEAST;
							case EAST -> component = MessageConstants.EAST;
							case SOUTHEAST -> component = MessageConstants.SOUTHEAST;
							case SOUTH -> component = MessageConstants.SOUTH;
							case SOUTHWEST -> component = MessageConstants.SOUTHWEST;
							case WEST -> component = MessageConstants.WEST;
							case NORTHWEST -> component = MessageConstants.NORTHWEST;
							default -> {
								return;
							}
						}
						EntityUtils.look(commandContext.sender(), face);
						handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_LOOK_SUCCESS, TranslationManager.render(component.build()));
						return;
					}

					handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_FAILURE_FLAG);
				});
	}
}
