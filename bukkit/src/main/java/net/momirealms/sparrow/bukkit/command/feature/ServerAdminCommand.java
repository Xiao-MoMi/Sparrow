package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.List;

public class ServerAdminCommand extends MessagingCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "server_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("server", StringParser.stringParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    String server = commandContext.get("server");
                    MultiplePlayerSelector selector = commandContext.get("player");
                    var players = selector.values();
                    for (Player player : players) {
                        SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(player, server);
                    }
                    CommandUtils.storeSelectorMessage(commandContext, selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_SERVER_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_SERVER_SUCCESS_MULTIPLE),
                            Pair.of(
                                    () -> List.of(Component.text(players.iterator().next().getName()), Component.text(server)),
                                    () -> List.of(Component.text(players.size()), Component.text(server))
                            ));
                });
    }
}
