package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.parser.standard.UUIDParser;

import java.util.UUID;

public class LookAdminCommand extends BukkitCommandFeature<CommandSender> {

	public LookAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
		super(sparrowCommandManager);
	}

	@Override
	public String getFeatureID() {
		return "look_admin";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
		return builder
				.required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
				.meta(SparrowMetaKeys.ALLOW_EMPTY_ENTITY_SELECTOR, false)
				.flag(manager.flagBuilder("entity_uuid").withComponent(UUIDParser.uuidParser()))
				.flag(manager.flagBuilder("player").withComponent(PlayerParser.playerParser()))
				.flag(manager.flagBuilder("location").withComponent(LocationParser.locationParser()))
				.flag(manager.flagBuilder("face").withComponent(EnumParser.enumParser(BlockFace.class)))
				.flag(manager.flagBuilder("silent").withAliases("s"))
				.handler(commandContext -> {
					boolean hasUUID = commandContext.flags().getValue("entity_uuid").isPresent();
					boolean hasPlayer = commandContext.flags().getValue("player").isPresent();
					boolean hasLocation = commandContext.flags().getValue("location").isPresent();

					BlockFace face = null;
					if (commandContext.flags().getValue("face").isPresent()) {
						face = (BlockFace) commandContext.flags().getValue("face").get();
					}

					MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
					var entities = selector.values();
					for (Entity e : entities) {
						if (hasUUID) {
							Entity entity = Bukkit.getEntity((UUID) commandContext.flags().getValue("entity_uuid").get());
							if (entity == null) {
								handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_FAILURE_UUID);
								return;
							}
							EntityUtils.look(e, entity);
						} else if (hasPlayer) {
							EntityUtils.look(e, (Player) commandContext.flags().getValue("player").get());
						} else if (hasLocation) {
							EntityUtils.look(e, (Location) commandContext.flags().getValue("location").get());
						} else if (face != null) {
							EntityUtils.look(e, face);
						} else {
							handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_FAILURE_FLAG);
							return;
						}
					}

					if (entities.size() == 1) handleFeedback(commandContext,
							MessageConstants.COMMANDS_ADMIN_LOOK_SUCCESS_SINGLE,
							Component.text(entities.iterator().next().getName()));
					else handleFeedback(commandContext,
							MessageConstants.COMMANDS_ADMIN_LOOK_SUCCESS_MULTIPLE,
							Component.text(entities.size()));
				});
	}
}
