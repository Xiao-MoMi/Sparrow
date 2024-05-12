package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.util.CommandUtils;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.FloatParser;

public class FlySpeedAdminCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "flyspeed_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("speed", FloatParser.floatParser(-1, 1))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    float speed = commandContext.get("speed");
                    var players = selector.values();
                    for (Player player : players) {
                        player.setFlySpeed(speed);
                    }
                    CommandUtils.storeEntitySelectorMessage(commandContext, selector,
                            Pair.of(MessageConstants.COMMANDS_ADMIN_FLY_SPEED_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_FLY_SPEED_SUCCESS_MULTIPLE)
                    );
                });
    }
}
