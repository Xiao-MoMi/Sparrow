package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.component.ShadedAdventureComponentWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import xyz.xenondevs.inventoryaccess.InventoryAccess;

public class EnderChestAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "enderchest_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    for (Player player : selector.values()) {
                        InventoryAccess.getInventoryUtils().openCustomInventory(player, player.getEnderChest(), new ShadedAdventureComponentWrapper(Component.translatable("container.enderchest")));
                    }
                });
    }
}
