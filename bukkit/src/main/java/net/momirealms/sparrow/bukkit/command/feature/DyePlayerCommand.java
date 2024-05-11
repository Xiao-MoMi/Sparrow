package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.command.handler.SparrowMessagingHandler;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;

import java.util.List;

public class DyePlayerCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "dye_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required(SparrowBukkitArgumentKeys.TEXT_COLOR, TextColorParser.textColorParser())
                .handler(commandContext -> {
                    TextColor textColor = commandContext.get(SparrowBukkitArgumentKeys.TEXT_COLOR);
                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_DYE_FAILED_ITEMLESS);
                        return;
                    }
                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_DYE_FAILED_INCOMPATIBLE);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(
                                Component.translatable(itemStack.translationKey())
                        ));
                        return;
                    }

                    meta.setColor(Color.fromRGB(textColor.red(), textColor.green(), textColor.blue()));
                    itemStack.setItemMeta(meta);
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_PLAYER_DYE_SUCCESS);
                })
                .appendHandler(SparrowMessagingHandler.instance());
    }
}
