package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.List;

public class MorePlayerCommand extends BukkitCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "more_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    int amount = (int) commandContext.optional("amount").orElse(0);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.isEmpty()) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_MORE_FAILED_NO_CHANGE);
                        return;
                    }
                    int maxStack = itemInHand.getType().getMaxStackSize();
                    if (amount == 0) {
                        if (itemInHand.getAmount() == maxStack) {
                            commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_MORE_FAILED_NO_CHANGE);
                            return;
                        }
                        itemInHand.setAmount(maxStack);
                        amount = maxStack - itemInHand.getAmount();
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_MORE_SUCCESS);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(amount)));
                    } else {
                        if (amount > maxStack * 100) {
                            commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_MORE_FAILED_TOO_MANY);
                            return;
                        }
                        int amountToGive = amount;
                        while (amountToGive > 0) {
                            int perStackSize = Math.min(maxStack, amountToGive);
                            amountToGive -= perStackSize;
                            ItemStack more = itemInHand.clone();
                            more.setAmount(perStackSize);
                            PlayerUtils.dropItem(player, more, false, true, false);
                        }
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_MORE_SUCCESS);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(amount)));
                    }
                });
    }
}
