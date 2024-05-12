package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.List;

public class ServerPlayerCommand extends MessagingCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "server_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("server", StringParser.stringParser())
                .handler(commandContext -> {
                    String server = commandContext.get("server");
                    SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(commandContext.sender(), server);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_SERVER_SUCCESS);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(server)));
                });
    }
}
