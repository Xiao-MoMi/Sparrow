package net.momirealms.sparrow.bukkit.command.feature.item.editor;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.Optional;

public class ItemCustomModelDataPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public ItemCustomModelDataPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "custommodeldata_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional("custommodeldata", IntegerParser.integerParser(0))
                .handler(commandContext -> {
                    Optional<Integer> optional = commandContext.optional("custommodeldata");
                    ItemStack itemInHand = commandContext.sender().getInventory().getItemInMainHand();
                    if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_FAILURE_ITEMLESS);
                        return;
                    }
                    if (optional.isPresent()) {
                        ItemStack modified = SparrowBukkitPlugin.getInstance().getItemFactory()
                                .wrap(itemInHand)
                                .customModelData(optional.get())
                                .load();
                        itemInHand.setItemMeta(modified.getItemMeta());
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_EDIT_CUSTOM_MODEL_DATA_SUCCESS, Component.text(optional.get()));
                    } else {
                        Optional<Integer> cmd = SparrowBukkitPlugin.getInstance().getItemFactory()
                                .wrap(itemInHand)
                                .customModelData();
                        if (cmd.isPresent()) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_QUERY_CUSTOM_MODEL_DATA_SUCCESS, Component.text(cmd.get()));
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_ITEM_QUERY_CUSTOM_MODEL_DATA_FAILURE);
                        }
                    }
                });
    }
}
