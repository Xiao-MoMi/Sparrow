package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.FloatParser;

public class FlySpeedAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "flyspeed_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("speed", FloatParser.floatParser(-1, 1))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    float speed = commandContext.get("speed");
                    for (Player player : selector.values()) {
                        player.setFlySpeed(speed);
                    }
                });
    }
}
