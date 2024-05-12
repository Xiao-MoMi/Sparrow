package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class SmithingTablePlayerCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "smithingtable_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    commandContext.sender().openSmithingTable(null, true);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_SMITHING_TABLE_SUCCESS);
                });
    }
}
