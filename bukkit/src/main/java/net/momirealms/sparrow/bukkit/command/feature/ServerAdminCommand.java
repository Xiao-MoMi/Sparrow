package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.util.TypeUtils;

public class ServerAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "server_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("server", StringParser.stringParser())
                .optional("player", PlayerParser.playerParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    String server = commandContext.get("server");
                    MultiplePlayerSelector selector = commandContext.getOrDefault("player", null);
                    if (selector != null) {
                        for (Player player : selector.values()) {
                            SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(player, server);
                        }
                    } else {
                        if (commandContext.sender() instanceof Player player) {
                            SparrowBukkitPlugin.getInstance().getBungeeManager().connectServer(player, server);
                        } else {
                            SparrowBukkitPlugin.getInstance().getBootstrap().getPluginLogger().severe(
                                    String.format("%s is not allowed to execute that command. Must be of type %s", commandContext.sender().getClass().getSimpleName(), TypeUtils.simpleName(Player.class))
                            );
                        }
                    }
                });
    }
}
