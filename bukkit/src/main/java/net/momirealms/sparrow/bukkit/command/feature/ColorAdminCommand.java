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

public class ColorAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "color_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    boolean silent = commandContext.flags().hasFlag("silent");
                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_COLOR_FAILED_ITEMLESS
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }
                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                        if (!silent) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_ADMIN_COLOR_FAILED_INCOMPATIBLE
                                                            .build()
                                            ),
                                            true
                                    );
                        }
                        return;
                    }
                    Color color = meta.getColor();
                    String hex = TextColor.color(color.asRGB()).asHexString();
                    if (!silent)
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_ADMIN_COLOR_SUCCESS_QUERY
                                                        .arguments(
                                                                Component.text(color.getRed()),
                                                                Component.text(color.getGreen()),
                                                                Component.text(color.getBlue()),
                                                                Component.text(color.asRGB()),
                                                                Component.text(hex).color(TextColor.color(color.asRGB()))
                                                        )
                                                        .build()
                                        ),
                                        true
                                );
                });
    }
}
