package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.component.ShadedAdventureComponentWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import xyz.xenondevs.inventoryaccess.InventoryAccess;

public class EnderChestPlayerCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "anvil_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    InventoryAccess.getInventoryUtils().openCustomInventory(player, player.getEnderChest(), new ShadedAdventureComponentWrapper(Component.translatable("container.enderchest")));
                });
    }
}
