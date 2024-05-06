package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.parser.standard.IntegerParser;

public class MoreAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "more_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", PlayerParser.playerParser())
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    boolean silent = commandContext.flags().hasFlag("silent");
                    Player player = commandContext.get("player");
                    int amount = (int) commandContext.optional("amount").orElse(0);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand.isEmpty()) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_MORE_FAILED_NO_CHANGE.build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }
                    int maxStack = itemInHand.getType().getMaxStackSize();
                    if (amount == 0) {
                        if (itemInHand.getAmount() == maxStack) {
                            if (!silent) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_MORE_FAILED_NO_CHANGE.build()
                                                ),
                                                true
                                        );
                            }
                            return;
                        }
                        itemInHand.setAmount(maxStack);
                        if (!silent) {
                            amount = maxStack - itemInHand.getAmount();
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_MORE_SUCCESS
                                                            .arguments(
                                                                    Component.text(amount),
                                                                    Component.text(player.getName())
                                                            )
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    } else {
                        if (amount > maxStack * 100) {
                            if (!silent) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_MORE_FAILED_TOO_MANY.build()
                                                ),
                                                true
                                        );
                            }
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
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_MORE_SUCCESS
                                                            .arguments(
                                                                    Component.text(amount),
                                                                    Component.text(player.getName())
                                                            )
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                    }
                });
    }
}
