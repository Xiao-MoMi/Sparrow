package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.handler.PlayerSelectorParserMessagingHandler;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;

public class FeedAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "feed_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_SINGLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_FEED_SUCCESS_SINGLE)
                .meta(SparrowMetaKeys.SELECTOR_SUCCESS_MULTIPLE_MESSAGE, MessageConstants.COMMANDS_ADMIN_FEED_SUCCESS_MULTIPLE)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    var players = selector.values();
                    for (Player player : players) {
                        player.setFoodLevel(20);
                        player.setSaturation(10f);
                    }
                    commandContext.store(SparrowArgumentKeys.IS_CALLBACK, true);
                })
                .appendHandler(PlayerSelectorParserMessagingHandler.instance());
    }
}
