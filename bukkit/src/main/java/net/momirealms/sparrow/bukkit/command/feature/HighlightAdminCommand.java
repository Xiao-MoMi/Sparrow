package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.common.command.AbstractCommandFeature;
import net.momirealms.sparrow.common.command.parser.NamedTextColorParser;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.locale.TranslationManager;
import net.momirealms.sparrow.common.util.ArrayUtils;
import net.momirealms.sparrow.heart.argument.NamedTextColor;
import net.momirealms.sparrow.heart.feature.highlight.HighlightBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.bukkit.parser.location.LocationCoordinateParser;
import org.incendo.cloud.bukkit.parser.location.LocationParser;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class HighlightAdminCommand extends AbstractCommandFeature<CommandSender> {

    @Override
    public String getFeatureID() {
        return "highlight_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("viewers", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .required("loc1", LocationParser.locationParser())
                .required("loc2", LocationParser.locationParser())
                .optional("world", WorldParser.worldParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .flag(manager.flagBuilder("highlight-duration").withAliases("d").withComponent(IntegerParser.integerParser(0,300)).build())
                .flag(manager.flagBuilder("highlight-color").withAliases("c").withComponent(NamedTextColorParser.namedTextColorParser()).build())
                .flag(manager.flagBuilder("solid-only").build())
                .handler(commandContext -> {
                    int duration = commandContext.flags().hasFlag("highlight-duration") ? (int) commandContext.flags().getValue("highlight-duration").get() : 30;
                    net.kyori.adventure.text.format.NamedTextColor color = commandContext.flags().hasFlag("highlight-color") ? (net.kyori.adventure.text.format.NamedTextColor) commandContext.flags().getValue("highlight-color").get() : net.kyori.adventure.text.format.NamedTextColor.GREEN;
                    NamedTextColor namedTextColor = NamedTextColor.namedColor(color.value());
                    MultiplePlayerSelector playerSelector = commandContext.get("viewers");
                    Collection<Player> players = playerSelector.values();
                    Location location1 = commandContext.get("loc1");
                    Location location2 = commandContext.get("loc2");
                    boolean silent = commandContext.flags().hasFlag("silent");
                    boolean solidOnly = commandContext.flags().hasFlag("solid-only");
                    Optional<World> optionalWorld = commandContext.optional("world");
                    if (commandContext.sender() instanceof ConsoleCommandSender consoleCommandSender) {
                        if (optionalWorld.isEmpty()) {
                            if (!silent)
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(consoleCommandSender)
                                        .sendMessage(
                                                TranslationManager.render(MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_FAILED_WORLD.build()),
                                                true
                                        );
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
                    int volume = deltaX * deltaY * deltaZ;
                    if (volume > 16 * 16 * 16 * 8) {
                        if (!silent)
                            SparrowBukkitPlugin.getInstance().getSenderFactory()
                                    .wrap(commandContext.sender())
                                    .sendMessage(
                                            TranslationManager.render(MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_FAILED_TOO_LARGE
                                                    .arguments(Component.text(volume))
                                                    .build()),
                                            true
                                    );
                    } else {
                        if (!silent)
                            if (players.size() == 1) {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_SUCCESS_SINGLE
                                                        .arguments(Component.text(players.iterator().next().getName()))
                                                        .build()),
                                                true
                                        );
                            } else {
                                SparrowBukkitPlugin.getInstance().getSenderFactory()
                                        .wrap(commandContext.sender())
                                        .sendMessage(
                                                TranslationManager.render(MessageConstants.COMMANDS_ADMIN_HIGHLIGHT_SUCCESS_MULTIPLE
                                                        .arguments(Component.text(players.size()))
                                                        .build()),
                                                true
                                        );
                            }

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
                    }
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
