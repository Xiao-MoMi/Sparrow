package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.sender.Sender;
import net.momirealms.sparrow.common.util.Either;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.EitherParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.parser.standard.UUIDParser;

import java.util.Objects;
import java.util.UUID;
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
                .required("target", EitherParser.eitherParser(
                        StringParser.stringParser(StringParser.StringMode.SINGLE),
                        UUIDParser.uuidParser()
                ))
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .handler(commandContext -> {
                    int amount = commandContext.<Integer>optional("amount").orElse(1);
                    var players = commandContext.<MultiplePlayerSelector>get("receiver").values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    SparrowBukkitSkullManager skullManager = SparrowBukkitPlugin.getInstance().getSkullManager();
                    org.incendo.cloud.type.Either<String, UUID> cloudEither = commandContext.get("target");
                    Either<String, UUID> either;
                    if (cloudEither.primary().isPresent()) either = Either.ofPrimary(cloudEither.primary().get());
                    else if (cloudEither.fallback().isPresent()) either = Either.ofFallback(cloudEither.fallback().get());
                    else return;

                    CompletableFuture<SkullData> futureSkull = skullManager.getSkull(either);
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    futureSkull.thenAcceptAsync(
                            skullData -> {
                                if (skullData == null) {
                                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                                            .wrap(commandContext.sender())
                                            .sendMessage(
                                                    TranslationManager.render(
                                                            MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL
                                                                    .arguments(Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString))))
                                                                    .build()
                                                    ),
                                                    true
                                            );
                                    return;
                                }

                                ItemStackUtils.applySkull(itemStack, skullData);
                                itemStack.setAmount(amount);
                                players.forEach(player -> {
                                    SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().sync().execute(
                                            () -> {
                                                PlayerUtils.dropItem(player, itemStack, false, true, false);
                                            }, player.getLocation()
                                    );
                                });

                                if (!silent) {
                                    if (players.size() == 1) {
                                        String receiverName = players.iterator().next().getName();
                                        Sender sender = SparrowBukkitPlugin.getInstance().getSenderFactory()
                                                .wrap(commandContext.sender());
                                        sender.sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_SINGLE
                                                                .arguments(
                                                                        Component.text(amount),
                                                                        Component.text(skullData.getOwner()),
                                                                        Component.text(receiverName)
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
                                                                        .arguments(
                                                                                Component.text(amount),
                                                                                Component.text(skullData.getOwner()),
                                                                                Component.text(players.size())
                                                                        )
                                                                        .build()
                                                        ),
                                                        true
                                                );
                                    }
                                }
                            }
                    ).exceptionally(throwable -> {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL
                                                        .arguments(Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString))))
                                                        .build()
                                        ),
                                        true
                                );
                        return null;
                    });
                });
    }
}
