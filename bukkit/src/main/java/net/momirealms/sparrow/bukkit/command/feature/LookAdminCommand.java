package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.parser.standard.EitherParser;
import org.incendo.cloud.parser.standard.UUIDParser;
import org.incendo.cloud.type.Either;
import org.incendo.cloud.type.tuple.Pair;

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
				.required("target", EitherParser.eitherParser(DoubleParser.doubleParser(), EitherParser.eitherParser(PlayerParser.playerParser(), UUIDParser.uuidParser())))
				.optionalArgumentPair("yz", "y", DoubleParser.doubleParser(), "z", DoubleParser.doubleParser(), Description.EMPTY)
				.handler(commandContext -> {

					Either<Double, Either<Player, UUID>> either = commandContext.get("target");

					MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
					var entities = selector.values();
					for (Entity e : entities) {
						if (commandContext.optional("yz").isPresent()) {
							if (either.primary().isEmpty()) continue;
							final Double x = either.primary().get();
							Pair<?, ?> yz = (Pair<?,?>) commandContext.optional("yz").get();
							EntityUtils.look(e, new Location(e.getWorld(), x, (Double) yz.first(), (Double) yz.second()));
						} else if (either.fallback().isPresent()) {
							if (either.fallback().get().primary().isPresent())
								EntityUtils.look(e, either.fallback().get().primary().get());
							else if (either.fallback().get().fallback().isPresent()) {
								Entity entity = Bukkit.getEntity(either.fallback().get().fallback().get());
								if (entity == null) {
									handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_FAILED_UUID);
									continue;
								}
								EntityUtils.look(e, entity);
							}
						}
						if (entities.size() == 1) handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_SUCCESS_SINGLE, Component.text(entities.size()));
						else handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_LOOK_SUCCESS_MULTIPLE, Component.text(entities.size()));
					}
				});
	}
}
