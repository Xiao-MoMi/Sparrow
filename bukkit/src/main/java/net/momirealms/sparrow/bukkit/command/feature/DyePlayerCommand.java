package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;

public class DyePlayerCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "dye_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("color", TextColorParser.textColorParser())
                .handler(commandContext -> {
                    TextColor textColor = commandContext.get("color");
                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_DYE_FAILED_ITEMLESS
                                                        .build()
                                        ),
                                        true
                                );
                        return;
                    }
                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_DYE_FAILED_INCOMPATIBLE
                                                        .arguments(Component.translatable(itemStack.translationKey()))
                                                        .build()
                                        ),
                                        true
                                );
                        return;
                    }

                    meta.setColor(Color.fromRGB(textColor.red(), textColor.green(), textColor.blue()));
                    itemStack.setItemMeta(meta);
                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                            .wrap(commandContext.sender())
                            .sendMessage(
                                    TranslationManager.render(
                                            MessageConstants.COMMANDS_PLAYER_DYE_SUCCESS
                                                    .build()
                                    ),
                                    true
                            );
                });
    }
}
