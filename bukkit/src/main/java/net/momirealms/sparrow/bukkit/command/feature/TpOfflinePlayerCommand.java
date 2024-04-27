package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.Optional;

public class TpOfflinePlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "tpoffline_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("player", StringParser.stringParser())
                .handler(commandContext -> {
                    OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(commandContext.get("player"));
                    if (player == null || player.getLocation() == null) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_FAILED_NEVER_PLAYED
                                                        .arguments(Component.text((String) commandContext.get("player")))
                                                        .build()
                                        ),
                                        true
                                );
                        return;
                    }

                    commandContext.sender().teleport(player.getLocation());
                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                            .wrap(commandContext.sender())
                            .sendMessage(
                                    TranslationManager.render(
                                            MessageConstants.COMMANDS_PLAYER_TP_OFFLINE_SUCCESS
                                                    .arguments(Component.text(Optional.ofNullable(player.getName()).orElse(String.valueOf(player.getUniqueId()))))
                                                    .build()
                                    ),
                                    true
                            );
                });
    }
}
