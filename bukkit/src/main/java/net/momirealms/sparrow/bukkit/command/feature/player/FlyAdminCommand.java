package net.momirealms.sparrow.bukkit.command.feature.player;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.BooleanParser;

public class FlyAdminCommand extends BukkitCommandFeature<CommandSender> {

    public FlyAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "fly_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .required("fly", BooleanParser.booleanParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    boolean fly = commandContext.get("fly");
                    var players = selector.values();
                    if (players.size() == 1) {
                        final Player player = players.iterator().next();
                        if (player.getAllowFlight() && fly) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_FAILED_ON, Component.text(player.getName()));
                            return;
                        }
                        if (!player.getAllowFlight() && !fly) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_FAILED_OFF, Component.text(player.getName()));
                            return;
                        }
                        player.setAllowFlight(fly);
                        player.setFlying(fly);
                        if (fly) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_ON_SINGLE, Component.text(player.getName()));
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_OFF_SINGLE, Component.text(player.getName()));
                        }
                    } else {
                        for (Player player : players) {
                            player.setAllowFlight(fly);
                            player.setFlying(fly);
                        }

                        if (fly) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_ON_MULTIPLE, Component.text(players.size()));
                        } else {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_FLY_SUCCESS_OFF_MULTIPLE, Component.text(players.size()));
                        }
                    }
                });
    }
}
