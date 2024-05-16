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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.context.CommandContext;

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
				.flag(manager.flagBuilder("north").build())
				.flag(manager.flagBuilder("east").build())
				.flag(manager.flagBuilder("south").build())
				.flag(manager.flagBuilder("west").build())
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
					if (handleDirectionFlag(commandContext, "north") ||
							handleDirectionFlag(commandContext, "east") ||
							handleDirectionFlag(commandContext, "south") ||
							handleDirectionFlag(commandContext, "west")) {
						return;
					}

					handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_FAILURE_FLAG);
				});
	}

	private boolean handleDirectionFlag(CommandContext<?> commandContext, String direction) {
		if (commandContext.flags().hasFlag(direction)) {
			TranslatableComponent.Builder component = MessageConstants.NORTH;
			switch (direction) {
				case "east" -> component = MessageConstants.EAST;
				case "south" -> component = MessageConstants.SOUTH;
				case "west" -> component = MessageConstants.WEST;
			}
			EntityUtils.look((Entity) commandContext.sender(), direction);
			handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_LOOK_SUCCESS, TranslationManager.render(component.build()));
			return true;
		}
		return false;
	}
}
