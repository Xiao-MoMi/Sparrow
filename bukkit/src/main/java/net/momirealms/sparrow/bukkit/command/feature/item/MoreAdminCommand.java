package net.momirealms.sparrow.bukkit.command.feature.item;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.parser.standard.IntegerParser;

public class MoreAdminCommand extends BukkitCommandFeature<CommandSender> {

    public MoreAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "more_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    Player player = commandContext.get("player");
                    int amount = (int) commandContext.optional("amount").orElse(0);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.isEmpty()) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_MORE_FAILED_NO_CHANGE);
                        return;
                    }
                    int maxStack = itemInHand.getType().getMaxStackSize();
                    if (amount == 0) {
                        if (itemInHand.getAmount() == maxStack) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_MORE_FAILED_NO_CHANGE);
                            return;
                        }
                        itemInHand.setAmount(maxStack);
                        amount = maxStack - itemInHand.getAmount();
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_MORE_SUCCESS, Component.text(amount), Component.text(player.getName()));
                    } else {
                        if (amount > maxStack * 100) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_MORE_FAILED_TOO_MANY);
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
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_MORE_SUCCESS);
                    }
                });
    }
}
