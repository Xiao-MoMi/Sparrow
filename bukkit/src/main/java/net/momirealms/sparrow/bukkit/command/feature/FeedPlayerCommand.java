package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class FeedPlayerCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "feed_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    player.setFoodLevel(20);
                    player.setSaturation(10f);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_FEED_SUCCESS);
                });
    }
}
