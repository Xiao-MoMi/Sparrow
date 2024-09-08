package net.momirealms.sparrow.bukkit.command.feature.world;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.command.key.SparrowMetaKeys;
import net.momirealms.sparrow.common.command.parser.NamedTextColorParser;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.ArrayUtils;
import net.momirealms.sparrow.heart.feature.highlight.HighlightBlocks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class HighlightAdminCommand extends BukkitCommandFeature<CommandSender> {

    public HighlightAdminCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "highlight_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .meta(SparrowMetaKeys.ALLOW_EMPTY_PLAYER_SELECTOR, false)
                .required("location1", LocationParser.locationParser())
                .required("location2", LocationParser.locationParser())
                .optional("world", WorldParser.worldParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .flag(manager.flagBuilder("highlight-duration").withAliases("d").withComponent(IntegerParser.integerParser(0, 300)).build())
                .flag(manager.flagBuilder("highlight-color").withAliases("c").withComponent(NamedTextColorParser.namedTextColorParser()).build())
                .flag(manager.flagBuilder("solid-only").build())
                .handler(commandContext -> {
                    int duration = commandContext.flags().hasFlag("highlight-duration") ? (int) commandContext.flags().getValue("highlight-duration").get() : 30;
                    net.kyori.adventure.text.format.NamedTextColor color = commandContext.flags().hasFlag("highlight-color") ? (net.kyori.adventure.text.format.NamedTextColor) commandContext.flags().getValue("highlight-color").get() : net.kyori.adventure.text.format.NamedTextColor.GREEN;
                    net.momirealms.sparrow.heart.feature.color.NamedTextColor namedTextColor = net.momirealms.sparrow.heart.feature.color.NamedTextColor.namedColor(color.value());
                    MultiplePlayerSelector selector = commandContext.get(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    Collection<Player> players = selector.values();
                    Location location1 = commandContext.get("location1");
                    Location location2 = commandContext.get("location2");
                    boolean solidOnly = commandContext.flags().hasFlag("solid-only");
                    Optional<World> optionalWorld = commandContext.optional("world");
                    if (commandContext.sender() instanceof ConsoleCommandSender) {
                        if (optionalWorld.isEmpty()) {
                            handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_FAILED_WORLD);
                            return;
                        }
                    }
                    World world;
                    if (optionalWorld.isPresent()) {
                        world = optionalWorld.get();
                        location1 = new Location(world, location1.getX(), location1.getY(), location1.getZ());
                        location2 = new Location(world, location2.getX(), location2.getY(), location2.getZ());
                    } else {
                        world = location1.getWorld();
                    }
                    int deltaX = Math.abs(location1.getBlockX() - location2.getBlockX());
                    int deltaY = Math.abs(location1.getBlockY() - location2.getBlockY());
                    int deltaZ = Math.abs(location1.getBlockZ() - location2.getBlockZ());
                    long volume = (long) deltaX * deltaY * deltaZ;

                    if (volume > 16 * 16 * 16 * 8) {
                        handleFeedback(commandContext, MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_FAILED_TOO_LARGE, Component.text(volume));
                        return;
                    }

                    var pair = resolveSelector(selector, MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_SUCCESS_SINGLE, MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_SUCCESS_MULTIPLE);
                    handleFeedback(commandContext, pair.left(), pair.right());

                    int lowerX = Math.min(location1.getBlockX(), location2.getBlockX());
                    int lowerY = Math.min(location1.getBlockY(), location2.getBlockY());
                    int lowerZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

                    SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().async().execute(() -> {
                        Location location = new Location(world, lowerX + 0.5, lowerY, lowerZ + 0.5);
                        ArrayList<Location> locationsPrune = new ArrayList<>((int) (volume / 0.75));
                        for (int i = 0; i <= deltaX; i++) {
                            for (int j = 0; j <= deltaY; j++) {
                                for (int k = 0; k <= deltaZ; k++) {
                                    locationsPrune.add(location.clone().add(i, j, k));
                                }
                            }
                        }
                        List<BlockFace> faces = List.of(BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN);
                        Location[] locationsArray;
                        if (solidOnly) {
                            locationsArray = locationsPrune.stream().filter(selectedBlockLocation -> {
                                Block block = selectedBlockLocation.getBlock();
                                if (block.isPassable()) {
                                    return false;
                                }
                                if (isBoundary(deltaX, deltaY, deltaZ, lowerX, lowerY, lowerZ, selectedBlockLocation))
                                    return true;
                                for (BlockFace face : faces) {
                                    if (block.getRelative(face).isPassable()) {
                                        return true;
                                    }
                                }
                                return false;
                            }).toList().toArray(new Location[0]);
                        } else {
                            locationsArray = locationsPrune.stream().filter(selectedBlockLocation -> isBoundary(deltaX, deltaY, deltaZ, lowerX, lowerY, lowerZ, selectedBlockLocation)).toList().toArray(new Location[0]);
                        }
                        List<Location[]> split = ArrayUtils.splitArray(locationsArray, 1024);
                        for (Player rp : players) {
                            for (Location[] l : split) {
                                HighlightBlocks blocks = SparrowNMSProxy.getInstance().highlightBlocks(rp, namedTextColor, l);
                                SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().asyncLater(() -> {
                                    if (rp.isOnline())
                                        blocks.destroy(rp);
                                }, duration, TimeUnit.SECONDS);
                            }
                        }
                    });
                });
    }

    private boolean isBoundary(int deltaX, int deltaY, int deltaZ, int lowerX, int lowerY, int lowerZ, Location selectedBlockLocation) {
        if (selectedBlockLocation.getBlockX() == lowerX || selectedBlockLocation.getBlockX() == lowerX + deltaX) {
            return true;
        }
        if (selectedBlockLocation.getBlockY() == lowerY || selectedBlockLocation.getBlockY() == lowerY + deltaY) {
            return true;
        }
        if (selectedBlockLocation.getBlockZ() == lowerZ || selectedBlockLocation.getBlockZ() == lowerZ + deltaZ) {
            return true;
        }
        return false;
    }
}
