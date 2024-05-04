package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.util.ItemStackUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.bukkit.feature.skull.BukkitSkullManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

public class HeadAdminCommand extends AbstractCommandFeature<CommandSender> {
    @Override
    public String getFeatureID() {
        return "head_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("receiver", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("player_name", StringParser.stringComponent())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .optional("amount", IntegerParser.integerParser(1, 64))
                .handler(commandContext -> {
                    String playerName = commandContext.get("player_name");
                    int amount = commandContext.<Integer>optional("amount").orElse(1);
                    var players = commandContext.<MultiplePlayerSelector>get("receiver").values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    for (Player player : players) {
                        Inventory senderInventory = player.getInventory();
                        Skull skull = BukkitSkullManager.getInstance().getSkull(playerName);
                        ItemStackUtils.createSkullItemAsync(skull, amount).thenAcceptAsync(item -> {
                            if (senderInventory.firstEmpty() != -1) {
                                senderInventory.addItem(item);
                            } else {
                                player.getWorld().dropItem(player.getLocation(), item);
                            }

                            if (!silent) {
                                if (players.size() == 1) {
                                    SparrowBukkitPlugin.getInstance().getSenderFactory()
                                            .wrap(commandContext.sender())
                                            .sendMessage(
                                                    TranslationManager.render(
                                                            MessageConstants.COMMANDS_ADMIN_HEAD_SUCCESS_SINGLE
                                                                    .arguments(Component.text(players.iterator().next().getName()))
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
                                                                    .arguments(Component.text(players.size()))
                                                                    .build()
                                                    ),
                                                    true
                                            );
                                }
                            }
                        });
                    }
                });
    }
}
