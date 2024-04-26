package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.parser.standard.StringParser;

public class ServerAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "server_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .required("server", StringParser.stringParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    String server = commandContext.get("server");
                    MultiplePlayerSelector selector = commandContext.get("player");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    var players = selector.values();
                    if (players.size() == 0) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.ARGUMENT_ENTITY_NOTFOUND_PLAYER.build()
                                            ),
                                            true
                                    );
                        return;
                    }
                    for (Player player : players) {
                        SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(player, server);
                    }
                    if (!silent) {
                        if (players.size() == 1) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_SERVER_SUCCESS_SINGLE
                                                            .arguments(Component.text(players.iterator().next().getName()))
                                                            .build()
                                            ),
                                            true
                                    );
                        } else {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    Message.COMMANDS_ADMIN_SERVER_SUCCESS_MULTIPLE
                                                            .arguments(Component.text(players.size()))
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    }
                });
    }
}
