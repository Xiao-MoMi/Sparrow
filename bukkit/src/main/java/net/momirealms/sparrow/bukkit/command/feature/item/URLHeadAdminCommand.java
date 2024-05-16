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
import net.momirealms.sparrow.common.command.parser.URLParser;
import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.feature.skull.argument.URLSkullArgument;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class URLHeadAdminCommand extends BukkitCommandFeature<CommandSender> {

    public URLHeadAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "urlhead_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .required("url", StringParser.<CommandSender>quotedStringParser().flatMap(URL.class, new URLParser<>()))
                .optional("amount", IntegerParser.integerParser(1, 6400))
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    int amount = commandContext.<Integer>optional("amount").orElse(1);
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    final URL url = commandContext.get("url");
                    var players = selector.values();
                    SparrowBukkitSkullManager skullManager = SparrowBukkitPlugin.getInstance().getSkullManager();
                    CompletableFuture<SkullData> futureSkull = skullManager.getSkull(new URLSkullArgument(url));
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    futureSkull.thenAcceptAsync(
                            skullData -> {
                                if (skullData == null) {
                                    handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_URLHEAD_FAILED_SKULL, Component.text(url.toString()));
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

                                var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_URLHEAD_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_URLHEAD_SUCCESS_MULTIPLE);
                                handleFeedback(commandContext, pair.left(),
                                        Component.text(amount),
                                        Component.text(skullData.getOwner()),
                                        pair.right()
                                );
                            }
                    ).exceptionally(throwable -> {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_URLHEAD_FAILED_SKULL, Component.text(url.toString()));
                        return null;
                    });
                });
    }
}
