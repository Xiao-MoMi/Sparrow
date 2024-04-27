package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.BooleanParser;

public class FlyAdminCommand extends AbstractCommand {

    @Override
    public String getFeatureID() {
        return "fly_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("player", MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false))
                .required("fly", BooleanParser.booleanParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("player");
                    boolean fly = commandContext.get("fly");
                    var players = selector.values();
                    boolean silent = commandContext.flags().hasFlag("silent");
                    if (players.size() == 1) {
                        final Player player = players.iterator().next();
                        if (player.getAllowFlight() && fly) {
                            if (!silent) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_FAILED_ON
                                                                .arguments(Component.text(player.getName()))
                                                                .build()
                                                ),
                                                true
                                        );
                            }
                            return;
                        }
                        if (!player.getAllowFlight() && !fly) {
                            if (!silent) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_FAILED_OFF
                                                                .arguments(Component.text(player.getName()))
                                                                .build()
                                                ),
                                                true
                                        );
                            }
                            return;
                        }
                        player.setAllowFlight(fly);
                        player.setFlying(fly);
                        if (!silent) {
                            if (fly) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_ON_SINGLE
                                                                .arguments(Component.text(player.getName()))
                                                                .build()
                                                ),
                                                true
                                        );
                            } else {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_OFF_SINGLE
                                                                .arguments(Component.text(player.getName()))
                                                                .build()
                                                ),
                                                true
                                        );
                            }
                        }
                    } else {
                        for (Player player : players) {
                            player.setAllowFlight(fly);
                            player.setFlying(fly);
                        }
                        if (!silent) {
                            if (fly) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_ON_MULTIPLE
                                                                .arguments(Component.text(players.size()))
                                                                .build()
                                                ),
                                                true
                                        );
                            } else {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(
                                                        MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_OFF_MULTIPLE
                                                                .arguments(Component.text(players.size()))
                                                                .build()
                                                ),
                                                true
                                        );
                            }
                        }
                    }
                });
    }
}
