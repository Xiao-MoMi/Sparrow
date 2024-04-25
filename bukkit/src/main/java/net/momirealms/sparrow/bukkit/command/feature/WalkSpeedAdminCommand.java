package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.FloatParser;
import org.incendo.cloud.util.TypeUtils;

public class WalkSpeedAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "walkspeed_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("speed", FloatParser.floatParser(-1, 1))
                .optional("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.getOrDefault("player", null);
                    float speed = commandContext.get("speed");
                    if (selector != null) {
                        for (Player player : selector.values()) {
                            player.setWalkSpeed(speed);
                        }
                    } else {
                        if (commandContext.sender() instanceof Player player) {
                            player.setWalkSpeed(speed);
                        } else {
                            SparrowBukkitPlugin.getInstance().getBootstrap().getPluginLogger().severe(
                                    String.format("%s is not allowed to execute that command. Must be of type %s", commandContext.sender().getClass().getSimpleName(), TypeUtils.simpleName(Player.class))
                            );
                        }
                    }
                });
    }
}
