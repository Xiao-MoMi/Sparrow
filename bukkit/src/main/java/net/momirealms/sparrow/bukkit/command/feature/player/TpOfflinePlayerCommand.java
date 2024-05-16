package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class TpOfflinePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public TpOfflinePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

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
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_FAILED_NEVER_PLAYED, Component.text((String) commandContext.get("player")));
                        return;
                    }

                    commandContext.sender().teleport(player.getLocation());
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_SUCCESS, Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId()))));
                });
    }
}
