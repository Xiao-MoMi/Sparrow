package net.momirealms.sparrow.bukkit.command.feature.misc;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.StringParser;

public class ServerPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public ServerPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

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
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_SERVER_SUCCESS, Component.text(server));
                });
    }
}
