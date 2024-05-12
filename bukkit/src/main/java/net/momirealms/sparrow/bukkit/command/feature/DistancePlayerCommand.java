package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.util.LocationUtils;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.minecraft.extras.parser.TextColorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

public class DistancePlayerCommand extends BukkitCommandFeature<CommandSender> {

    public DistancePlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "distance_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .flag(manager.flagBuilder("max-distance").withAliases("m").withComponent(IntegerParser.integerParser(0,512)).build())
                .flag(manager.flagBuilder("disable-marker").build())
                .flag(manager.flagBuilder("marker-duration").withAliases("d").withComponent(IntegerParser.integerParser(0,60)).build())
                .flag(manager.flagBuilder("marker-color").withAliases("c").withComponent(TextColorParser.textColorParser()).build())
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    boolean distanceFlag = commandContext.flags().hasFlag("max-distance");
                    int maxDistance = distanceFlag ? (int) commandContext.flags().getValue("max-distance").get() : 256;
                    Block block = player.getTargetBlockExact(maxDistance);
                    if (block == null) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_DISTANCE_FAILED, Component.text(maxDistance));
                    } else {
                        if (!commandContext.flags().hasFlag("disable-highlight")) {
                            Location location = block.getLocation();
                            int duration = commandContext.flags().hasFlag("marker-duration") ? (int) commandContext.flags().getValue("marker-duration").get() : 10;
                            TextColor color = commandContext.flags().hasFlag("marker-color") ? (TextColor) commandContext.flags().getValue("marker-color").get() : NamedTextColor.GREEN;
                            int argb = 0x64000000 | color.value();
                            SparrowNMSProxy.getInstance().sendDebugMarker(player, location, "x:" + location.getBlockX() + ", y:" + location.getBlockY() + ", z:" + location.getBlockZ(), duration * 1000, argb);
                        }
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_DISTANCE_SUCCESS,
                                Component.text(String.format("%.2f", LocationUtils.euclideanDistance(player.getLocation(), LocationUtils.toBlockCenter(block.getLocation())))),
                                Component.text(String.format("%.2f", LocationUtils.manhattanDistance(player.getLocation(), LocationUtils.toBlockCenter(block.getLocation()))))
                        );
                    }
                });
    }
}
