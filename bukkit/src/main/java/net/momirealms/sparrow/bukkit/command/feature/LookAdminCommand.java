package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.parser.LookParser;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

public class LookAdminCommand extends AbstractCommandFeature<CommandSender> {

	@Override
	public String getFeatureID() {
		return "look_admin";
	}

	@Override
	public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {

		return builder
				.required("entity", MultipleEntitySelectorParser.multipleEntitySelectorParser(false))
				.required("target", StringParser.stringParser(), SuggestionProvider.suggestingStrings( "location", "player", "entity_uuid"))
				.required("value", LookParser.lookParser())
				.handler(commandContext -> {

					Entity target = commandContext.get("target");

					Selector<Entity> selector = commandContext.get("entity");
					var entities = selector.values();
					for (Entity entity : entities) {
						EntityUtils.look(entity, target);
					}
				});
	}
}
