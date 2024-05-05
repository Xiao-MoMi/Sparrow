package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.BukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

public class HeadPlayerCommand extends AbstractCommandFeature<CommandSender> {
    @Override
    public String getFeatureID() {
        return "head_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .handler(commandContext -> {
                    Player player = commandContext.sender();
                    Inventory senderInventory = player.getInventory();
                    BukkitSkullManager skullManager = BukkitSkullManager.getInstance();
                    String targetName = player.getName();
                    Skull skull = skullManager.getSkull(targetName);

                    ItemStackUtils.createSkullItemAsync(skull, 1)
                            .thenAcceptAsync(item -> {
                                if (senderInventory.firstEmpty() != -1) {
                                    senderInventory.addItem(item);
                                } else {
                                    player.getWorld().dropItem(player.getLocation(), item);
                                }

                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_PLAYER_HEAD_SUCCESS.build()
                                                ),
                                                true
                                        );
                            })
                            .exceptionally(throwable -> {
                                skullManager.removeSkull(targetName);
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_PLAYER_HEAD_FAILED_SKULL.build()
                                                ),
                                                true
                                        );
                                return null;
                            });
                });
    }
}
