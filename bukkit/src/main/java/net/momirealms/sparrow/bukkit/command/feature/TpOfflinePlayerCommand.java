package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.List;
import java.util.Optional;

public class TpOfflinePlayerCommand extends MessagingCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "tpoffline_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("player", StringParser.stringParser())
                .handler(commandContext -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(commandContext.get("player"));
                    if (player == null || player.getLocation() == null) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_FAILED_NEVER_PLAYED);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text((String) commandContext.get("player"))));
                        return;
                    }

                    commandContext.sender().teleport(player.getLocation());
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_SUCCESS);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId())))));
                });
    }
}
