package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.Either;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.concurrent.CompletableFuture;

public class HeadPlayerCommand extends BukkitCommandFeature<CommandSender> {

    public HeadPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

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
                    SparrowBukkitSkullManager skullManager = SparrowBukkitPlugin.getInstance().getSkullManager();
                    CompletableFuture<SkullData> futureSkull = skullManager.getSkull(Either.ofPrimary(player.getName()));
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    futureSkull.thenAcceptAsync(skullData -> {
                        if (skullData == null) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_HEAD_FAILED_SKULL);
                            return;
                        }

                        ItemStackUtils.applySkull(itemStack, skullData);
                        SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().sync().execute(
                                () -> {
                                    PlayerUtils.dropItem(player, itemStack, false, true, false);
                                }, player.getLocation()
                        );
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_HEAD_SUCCESS);
                    });
                });
    }
}
