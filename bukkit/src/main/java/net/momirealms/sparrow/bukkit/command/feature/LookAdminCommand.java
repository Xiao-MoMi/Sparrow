package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.DoubleParser;
import org.incendo.cloud.parser.standard.EitherParser;
import org.incendo.cloud.parser.standard.UUIDParser;
import org.incendo.cloud.type.Either;
import org.incendo.cloud.type.tuple.Pair;

import java.util.UUID;

public class LookAdminCommand extends AbstractCommandFeature<CommandSender> {

	@Override
	public String getFeatureID() {
		return "look_admin";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
		return builder
				.required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
				.required("target", EitherParser.eitherParser(DoubleParser.doubleParser(), EitherParser.eitherParser(PlayerParser.playerParser(), UUIDParser.uuidParser())))
				.optionalArgumentPair("yz", "y", DoubleParser.doubleParser(), "z", DoubleParser.doubleParser(), Description.EMPTY)
				.handler(commandContext -> {

					Either<Double, Either<Player, UUID>> either = commandContext.get("target");

					Selector<Entity> selector = commandContext.get("entity");
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
								if (entity == null) continue;
								EntityUtils.look(e, entity);
							}
						}
					}
				});
	}
}
