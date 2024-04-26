package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.Message;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;

public class GrindStonePlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "grindstone_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    commandContext.sender().openGrindstone(null, true);
                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                            .wrap(commandContext.sender())
                            .sendMessage(
                                    TranslationManager.render(
                                            Message.COMMANDS_PLAYER_GRINDSTONE_SUCCESS.build()
                                    ),
                                    true
                            );
                });
    }
}
