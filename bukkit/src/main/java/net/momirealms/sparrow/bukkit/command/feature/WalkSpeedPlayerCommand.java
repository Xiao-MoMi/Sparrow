package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.FloatParser;

import java.util.List;

public class WalkSpeedPlayerCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "walkspeed_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("speed", FloatParser.floatParser(-1, 1))
                .handler(commandContext -> {
                    float speed = commandContext.get("speed");
                    commandContext.sender().setWalkSpeed(speed);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_WALK_SPEED_SUCCESS);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(speed)));
                });
    }
}
