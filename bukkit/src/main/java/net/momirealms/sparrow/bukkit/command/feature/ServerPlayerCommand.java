package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class ServerPlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "server_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("server", StringParser.stringParser())
                .handler(commandContext -> {
                    String server = commandContext.get("server");
                    SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(commandContext.sender(), server);
                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                            .wrap(commandContext.sender())
                            .sendMessage(
                                    TranslationManager.render(
                                            MessageConstants.COMMANDS_PLAYER_SERVER_SUCCESS
                                                    .arguments(Component.text(server))
                                                    .build()
                                    ),
                                    true
                            );
                });
    }
}
