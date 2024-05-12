package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.locale.MessageConstants;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HeadAdminCommand extends MessagingCommandFeature<CommandSender> {
    @Override
    public String getFeatureID() {
        return "head_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("target", EitherParser.eitherParser(
                        StringParser.stringParser(StringParser.StringMode.SINGLE),
                        UUIDParser.uuidParser()
                ))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .handler(commandContext -> {
                    int amount = commandContext.<Integer>optional("amount").orElse(1);
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    var players = selector.values();
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
                                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL);
                                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS,
                                            List.of(
                                                    Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString)))
                                            )
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

                                if (players.size() == 1) {
                                    String receiverName = players.iterator().next().getName();
                                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_SINGLE);
                                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS,
                                            List.of(
                                                    Component.text(amount),
                                                    Component.text(skullData.getOwner()),
                                                    Component.text(receiverName)
                                            )
                                    );
                                } else {
                                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_MULTIPLE);
                                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS,
                                            List.of(
                                                    Component.text(amount),
                                                    Component.text(skullData.getOwner()),
                                                    Component.text(players.size())
                                            )
                                    );
                                }
                            }
                    ).exceptionally(throwable -> {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL);
                        commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS,
                                List.of(
                                        Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString)))
                                )
                        );
                        return null;
                    }).join();
                });
    }
}
