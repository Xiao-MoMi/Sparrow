package net.momirealms.sparrow.bukkit.command.feature.world;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class TopBlockPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public TopBlockPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "topblock_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    EntityUtils.toTopBlockPosition(commandContext.sender());
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_TOP_BLOCK_SUCCESS);
                });
    }
}
