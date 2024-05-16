package net.momirealms.sparrow.bukkit.command.feature.item;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.bukkit.feature.skull.SparrowBukkitSkullManager;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.bukkit.util.PlayerUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
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

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HeadAdminCommand extends BukkitCommandFeature<CommandSender> {

    public HeadAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "head_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
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
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL, Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString))));
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

                                var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_MULTIPLE);
                                handleFeedback(commandContext, pair.left(),
                                        Component.text(amount),
                                        Component.text(skullData.getOwner()),
                                        pair.right()
                                );
                            }
                    ).exceptionally(throwable -> {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_HEAD_FAILED_SKULL, Component.text(Objects.requireNonNull(either.primaryOrMapFallback(UUID::toString))));
                        return null;
                    });
                });
    }
}
