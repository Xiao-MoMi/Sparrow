package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class FlyPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public FlyPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "fly_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    if (player.getAllowFlight()) {
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_FLY_SUCCESS_OFF);
                    } else {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_FLY_SUCCESS_ON);
                    }
                });
    }
}
