package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.command.handler.SparrowMessagingHandler;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.List;

public class ColorAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "color_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_COLOR_FAILED_ITEMLESS);
                        return;
                    }
                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_COLOR_FAILED_INCOMPATIBLE);
                        return;
                    }
                    Color color = meta.getColor();
                    String hex = TextColor.color(color.asRGB()).asHexString();
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_COLOR_SUCCESS_QUERY);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                            Component.text(color.getRed()),
                            Component.text(color.getGreen()),
                            Component.text(color.getBlue()),
                            Component.text(color.asRGB()),
                            Component.text(hex).color(TextColor.color(color.asRGB()))
                    ));
                })
                .appendHandler(SparrowMessagingHandler.instance());
    }
}
