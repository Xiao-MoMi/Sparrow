package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.helper.AdventureHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.Optional;

public class ModelDataAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "modeldata_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("modeldata", IntegerParser.integerParser(0))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    Optional<Integer> optional = commandContext.optional("modeldata");
                    ItemStack itemStack = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemStack.isEmpty()) return;
                    ItemMeta meta = itemStack.getItemMeta();
                    if (optional.isPresent()) {
                        meta.setCustomModelData(optional.get());
                        itemStack.setItemMeta(meta);
                    } else {
                        if (!meta.hasCustomModelData()) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(commandContext.sender())
                                    .sendMessage(Component.text("No data"));
                        } else {
                            SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(commandContext.sender())
                                    .sendMessage(
                                            AdventureHelper.getMiniMessage().deserialize(
                                                    "CustomModelData: " + meta.getCustomModelData()
                                            )
                                    );
                        }
                    }
                });
    }
}
