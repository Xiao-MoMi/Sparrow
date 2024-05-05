package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.bukkit.feature.skull.BukkitSkullManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.sender.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.util.concurrent.CompletableFuture;

public class HeadAdminCommand extends AbstractCommandFeature<CommandSender> {
    @Override
    public String getFeatureID() {
        return "head_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("receiver", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("target_name", StringParser.stringComponent()
                        .suggestionProvider(PlayerParser.playerComponent().suggestionProvider())
                        .parser()
                )
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .optional("amount", IntegerParser.integerParser(1, 64))
                .handler(commandContext -> {
                    String targetName = commandContext.get("target_name");
                    int amount = commandContext.<Integer>optional("amount").orElse(1);
                    var players = commandContext.<MultiplePlayerSelector>get("receiver").values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    BukkitSkullManager skullManager = BukkitSkullManager.getInstance();

                    CompletableFuture<?>[] futures = players.stream().map(player -> {
                        Inventory senderInventory = player.getInventory();
                        Skull skull = skullManager.getSkull(targetName);
                        return ItemStackUtils.createSkullItemAsync(skull, amount)
                                .thenApplyAsync(item -> {
                                    if (senderInventory.firstEmpty() != -1) {
                                        senderInventory.addItem(item);
                                    } else {
                                        player.getWorld().dropItem(player.getLocation(), item);
                                    }
                                    return targetName;
                                });
                    }).toArray(CompletableFuture<?>[]::new);

                    CompletableFuture.allOf(futures)
                            .thenAcceptAsync(unused -> {
                                if (!silent) {
                                    if (players.size() == 1) {
                                        String receiverName = players.iterator().next().getName();
                                        Sender sender = SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender());

                                        if (receiverName.equals(targetName)) {
                                            sender.sendMessage(
                                                    TranslationManager.render(
                                                            MessageConstants.COMMANDS_PLAYER_HEAD_SUCCESS.build()
                                                    ),
                                                    true
                                            );
                                            return;
                                        }
                                        sender.sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_SINGLE
                                                                .arguments(
                                                                        Component.text(receiverName),
                                                                        Component.text(targetName)
                                                                )
                                                                .build()
                                                ),
                                                true
                                        );
                                    } else {
                                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender())
                                                .sendMessage(
                                                        TranslationManager.render(
                                                                MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_MULTIPLE
                                                                        .arguments(Component.text(players.size()), Component.text(targetName))
                                                                        .build()
                                                        ),
                                                        true
                                                );
                                    }
                                }
                            })
                            .exceptionally(throwable -> {
                                skullManager.removeSkull(targetName);
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL
                                                                .arguments(Component.text(targetName))
                                                                .build()
                                                ),
                                                true
                                        );
                                return null;
                            });
                });
    }
}
