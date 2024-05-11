package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.handler.EntitySelectorParserMessagingHandler;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.command.parser.TimeParser;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;

import java.util.List;

public class BurnAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "burn_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .required(SparrowArgumentKeys.TIME, TimeParser.timeParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_SINGLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_BURN_SUCCESS_SINGLE)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_MULTIPLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_BURN_SUCCESS_MULTIPLE)
                .handler(commandContext -> {
                    Selector<Entity> selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    var entities = selector.values();
                    long ticks = commandContext.get(SparrowArgumentKeys.TIME);
                    for (Entity entity : entities) {
                        entity.setFireTicks((int) ticks);
                    }
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(ticks)));
                    commandContext.store(SparrowArgumentKeys.IS_CALLBACK, true);
                })
                .appendHandler(EntitySelectorParserMessagingHandler.instance());
    }
}
