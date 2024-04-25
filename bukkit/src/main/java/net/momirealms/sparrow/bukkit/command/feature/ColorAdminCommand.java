package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ColorableArmorMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;

import java.util.Optional;

public class ColorAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "color_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("color", TextColorParser.textColorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    Optional<TextColor> optional = commandContext.optional("color");

                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) return;
                    if (!(itemStack.getItemMeta() instanceof ColorableArmorMeta meta)) return;

                    if (optional.isPresent()) {
                        var textColor = optional.get();
                        meta.setColor(Color.fromRGB(textColor.red(), textColor.green(), textColor.blue()));
                        itemStack.setItemMeta(meta);
                    } else {
                        Color color = meta.getColor();
                        String hex = TextColor.color(color.asRGB()).asHexString();
                        SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(commandContext.sender())
                                .sendMessage(
                                        AdventureHelper.getMiniMessage().deserialize(
                                                "<white>RGB: " +
                                                        "<red>" + color.getRed() + "</red>, " +
                                                        "<green>" + color.getGreen() + "</green>, " +
                                                        "<blue>" + color.getBlue() + "</blue>" + "<newline>"
                                                + "HEX: " + hex + "(<" + hex + ">" + "⬛" + "</" + hex + ">)</white>"
                                        )
                                );
                    }
                });
    }
}
