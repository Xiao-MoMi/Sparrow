package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.util.EntityUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;

public class HealPlayerCommand extends AbstractCommand {
    @Override
    public String getFeatureID() {
        return "heal_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> EntityUtil.heal(commandContext.sender()));
    }
}
