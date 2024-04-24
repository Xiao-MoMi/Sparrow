package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;

public class SuicidePlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "suicide_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> commandContext.sender().setHealth(0));
    }
}
