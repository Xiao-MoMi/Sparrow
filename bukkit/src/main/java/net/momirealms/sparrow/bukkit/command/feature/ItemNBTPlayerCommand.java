package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class ItemNBTPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public ItemNBTPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "itemnbt_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    SparrowBukkitPlugin.getInstance().getItemFactory()
                            .wrap(commandContext.sender().getInventory().getItemInMainHand())
                            .customModelData(1)
                            .getItem();
                });
    }
}
