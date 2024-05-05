package net.momirealms.sparrow.bukkit.command.feature;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.EntityUtils;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.util.Either;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;

import java.util.concurrent.CompletableFuture;

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
                    SparrowBukkitSkullManager skullManager = SparrowBukkitPlugin.getInstance().getSkullManager();
                    CompletableFuture<SkullData> futureSkull = skullManager.getSkull(Either.ofPrimary(player.getName()));
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    futureSkull.thenAcceptAsync(skullData -> {
                        if (skullData == null) {
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(
                                                    MessageConstants.COMMANDS_PLAYER_HEAD_FAILED_SKULL.build()
                                            ),
                                            true
                                    );
                            return;
                        }

                        ItemStackUtils.applySkull(itemStack, skullData);
                        SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().sync().execute(
                                () -> {
                                    EntityUtils.giveItem(player, itemStack);
                                }, player.getLocation()
                        );
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_HEAD_SUCCESS.build()
                                        ),
                                        true
                                );
                    });
                });
    }
}
