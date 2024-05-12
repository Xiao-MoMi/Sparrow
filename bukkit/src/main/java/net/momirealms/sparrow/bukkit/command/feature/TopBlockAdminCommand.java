package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultipleEntitySelector;
import org.incendo.cloud.bukkit.parser.selector.MultipleEntitySelectorParser;

public class TopBlockAdminCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "topblock_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.ENTITY_SELECTOR, MultipleEntitySelectorParser.multipleEntitySelectorParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    MultipleEntitySelector selector = commandContext.get(SparrowBukkitArgumentKeys.ENTITY_SELECTOR);
                    var entities = selector.values();
                    for (Entity entity : entities) {
                        EntityUtils.toTopBlockPosition(entity);
                    }
                    CommandUtils.storeEntitySelectorMessage(commandContext, selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_TOP_BLOCK_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_TOP_BLOCK_SUCCESS_MULTIPLE)
                    );
                });
    }
}
