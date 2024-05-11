package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.handler.PlayerMessagingHandler;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class AnvilPlayerCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "anvil_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .meta(SparrowMetaKeys.PLAYER_SUCCESS_MESSAGE, MessageConstants.COMMANDS_PLAYER_ANVIL_SUCCESS)
                .handler(commandContext -> {
                    commandContext.sender().openAnvil(null, true);
                    commandContext.store(SparrowArgumentKeys.IS_CALLBACK, true);
                })
                .appendHandler(PlayerMessagingHandler.instance());
    }
}
