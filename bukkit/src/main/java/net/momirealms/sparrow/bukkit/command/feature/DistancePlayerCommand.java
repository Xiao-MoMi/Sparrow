package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.util.LocationUtils;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.parser.NamedTextColorParser;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.concurrent.TimeUnit;

public class DistancePlayerCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "distance_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("max-distance").withAliases("m").withComponent(IntegerParser.integerParser(0,512)).build())
                .flag(manager.flagBuilder("disable-highlight").build())
                .flag(manager.flagBuilder("highlight-duration").withAliases("d").withComponent(IntegerParser.integerParser(0,60)).build())
                .flag(manager.flagBuilder("highlight-color").withAliases("c").withComponent(NamedTextColorParser.namedTextColorParser()).build())
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    boolean distanceFlag = commandContext.flags().hasFlag("max-distance");
                    int maxDistance = distanceFlag ? (int) commandContext.flags().getValue("max-distance").get() : 256;
                    Block block = player.getTargetBlockExact(maxDistance);
                    if (block == null) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_DISTANCE_FAILED
                                                        .arguments(Component.text(maxDistance))
                                                        .build()
                                        ),
                                        true
                                );
                    } else {
                        if (!commandContext.flags().hasFlag("disable-highlight")) {
                            int duration = commandContext.flags().hasFlag("highlight-duration") ? (int) commandContext.flags().getValue("highlight-duration").get() : 5;
                            NamedTextColor color = commandContext.flags().hasFlag("highlight-color") ? (NamedTextColor) commandContext.flags().getValue("highlight-color").get() : NamedTextColor.GREEN;
                            var highlighted = SparrowNMSProxy.getInstance().highlightBlocks(player, net.momirealms.sparrow.heart.argument.NamedTextColor.namedColor(color.value()), block.getLocation());
                            SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().asyncLater(() -> {
                                if (player.isOnline()) {
                                    highlighted.destroy(player);
                                }
                            }, duration, TimeUnit.SECONDS);
                        }
                        SparrowBukkitPlugin.getInstance().getSenderFactory()
                                .wrap(commandContext.sender())
                                .sendMessage(
                                        TranslationManager.render(
                                                MessageConstants.COMMANDS_PLAYER_DISTANCE_SUCCESS
                                                        .arguments(
                                                                Component.text(String.format("%.2f", LocationUtils.euclideanDistance(player.getLocation(), LocationUtils.toBlockCenter(block.getLocation())))),
                                                                Component.text(String.format("%.2f", LocationUtils.manhattanDistance(player.getLocation(), LocationUtils.toBlockCenter(block.getLocation()))))
                                                        )
                                                        .build()
                                        ),
                                        true
                                );
                    }
                });
    }
}
