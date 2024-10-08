package net.momirealms.sparrow.bukkit.command.feature.world;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.SparrowNMSProxy;
import net.momirealms.sparrow.bukkit.command.BukkitCommandFeature;
import net.momirealms.sparrow.bukkit.command.key.SparrowBukkitArgumentKeys;
import net.momirealms.sparrow.common.command.SparrowCommandManager;
import net.momirealms.sparrow.common.command.parser.NamedTextColorParser;
import net.momirealms.sparrow.common.locale.MessageConstants;
import net.momirealms.sparrow.common.util.ArrayUtils;
import net.momirealms.sparrow.heart.feature.color.NamedTextColor;
import net.momirealms.sparrow.heart.feature.highlight.HighlightBlocks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
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
import org.incendo.cloud.bukkit.data.Selector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.standard.IntegerParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DuplicatedCode")
public class HighlightPlayerCommand extends BukkitCommandFeature<CommandSender> {

    private final HighlightListener listener = new HighlightListener();
    private static final ConcurrentHashMap<UUID, HighlightArguments> argMap = new ConcurrentHashMap<>();

    public HighlightPlayerCommand(SparrowCommandManager<CommandSender> sparrowCommandManager) {
        super(sparrowCommandManager);
    }

    @Override
    public String getFeatureID() {
        return "highlight_player";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .optional(SparrowBukkitArgumentKeys.PLAYER_SELECTOR, MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(manager.flagBuilder("highlight-duration").withAliases("d").withComponent(IntegerParser.integerParser(0,300)).build())
                .flag(manager.flagBuilder("highlight-color").withAliases("c").withComponent(NamedTextColorParser.namedTextColorParser()).build())
                .flag(manager.flagBuilder("solid-only").build())
                .handler(commandContext -> {
                    final Player player = commandContext.sender();
                    if (argMap.containsKey(player.getUniqueId())) {
                        argMap.remove(player.getUniqueId());
                        handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_CANCEL_POSITIVE);
                        return;
                    }
                    int duration = commandContext.flags().hasFlag("highlight-duration") ? (int) commandContext.flags().getValue("highlight-duration").get() : 30;
                    net.kyori.adventure.text.format.NamedTextColor color = commandContext.flags().hasFlag("highlight-color") ? (net.kyori.adventure.text.format.NamedTextColor) commandContext.flags().getValue("highlight-color").get() : net.kyori.adventure.text.format.NamedTextColor.GREEN;
                    NamedTextColor namedTextColor = NamedTextColor.namedColor(color.value());
                    Optional<MultiplePlayerSelector> playerSelector = commandContext.optional(SparrowBukkitArgumentKeys.PLAYER_SELECTOR);
                    Collection<Player> players = playerSelector.map(Selector::values).orElse(player.getWorld().getPlayers());
                    long time = System.currentTimeMillis();
                    HighlightArguments args = new HighlightArguments(duration, namedTextColor, players, commandContext.flags().hasFlag("solid-only"), time);
                    argMap.put(player.getUniqueId(), args);
                    handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_TIP);
                    SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().asyncLater(() -> {
                        HighlightArguments arg = argMap.get(player.getUniqueId());
                        if (arg != null && arg.timeStamp == time) {
                            argMap.remove(player.getUniqueId());
                            if (player.isOnline()) {
                                handleFeedback(commandContext, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_CANCEL_TIMEOUT);
                            }
                        }
                    }, 30, TimeUnit.SECONDS);
                });
    }

    @Override
    public void registerRelatedFunctions() {
        Bukkit.getPluginManager().registerEvents(listener, SparrowBukkitPlugin.getInstance().getLoader());
    }

    @Override
    public void unregisterRelatedFunctions() {
        HandlerList.unregisterAll(listener);
    }

    public static class HighlightArguments {
        private final int duration;
        private final NamedTextColor color;
        private final Collection<Player> viewers;
        private final long timeStamp;
        private Location firstPoint;
        private final boolean solidOnly;
        public HighlightArguments(int duration, NamedTextColor color, Collection<Player> viewers, boolean solidOnly, long timeStamp) {
            this.duration = duration;
            this.color = color;
            this.viewers = viewers;
            this.timeStamp = timeStamp;
            this.solidOnly = solidOnly;
        }
    }

    public class HighlightListener implements Listener {

        @EventHandler
        private void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getHand() != EquipmentSlot.HAND) return;

            final Player player = event.getPlayer();
            HighlightArguments arg = argMap.get(player.getUniqueId());
            if (arg == null) return;

            event.setCancelled(true);
            Location selectedLocation = null;
            if (event.getAction().isLeftClick()) {
                selectedLocation = player.getLocation();
            }

            if (event.getAction().isRightClick()) {
                Block block = event.getClickedBlock();
                if (block != null) {
                    selectedLocation = block.getLocation();
                }
            }

            if (selectedLocation == null) return;

            if (arg.firstPoint == null) {
                arg.firstPoint = selectedLocation;
                handleFeedback(player, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_SELECTED_FIRST,
                        Component.text(arg.firstPoint.getBlockX()),
                        Component.text(arg.firstPoint.getBlockY()),
                        Component.text(arg.firstPoint.getBlockZ())
                );
            } else {
                argMap.remove(player.getUniqueId());
                handleFeedback(player, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_SELECTED_SECOND,
                        Component.text(selectedLocation.getBlockX()),
                        Component.text(selectedLocation.getBlockY()),
                        Component.text(selectedLocation.getBlockZ())
                );
                World world = selectedLocation.getWorld();
                if (world != arg.firstPoint.getWorld()) {
                    handleFeedback(player, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_FAILED_WORLD);
                    return;
                }

                int deltaX = Math.abs(selectedLocation.getBlockX() - arg.firstPoint.getBlockX());
                int deltaY = Math.abs(selectedLocation.getBlockY() - arg.firstPoint.getBlockY());
                int deltaZ = Math.abs(selectedLocation.getBlockZ() - arg.firstPoint.getBlockZ());
                long volume = (long) deltaX * deltaY * deltaZ;
                if (volume > 16 * 16 * 16 * 8) {
                    handleFeedback(player, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_FAILED_TOO_LARGE, Component.text(volume));
                    return;
                }

                Collection<Player> remaining = arg.viewers.stream().filter(OfflinePlayer::isOnline).toList();

                var pair = resolveSelector(remaining, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_SUCCESS_SINGLE, MessageConstants.COMMANDS_PLAYER_HIGHLIGHT_SUCCESS_MULTIPLE);
                handleFeedback(player, pair.left(), pair.right());

                int lowerX = Math.min(selectedLocation.getBlockX(), arg.firstPoint.getBlockX());
                int lowerY = Math.min(selectedLocation.getBlockY(), arg.firstPoint.getBlockY());
                int lowerZ = Math.min(selectedLocation.getBlockZ(), arg.firstPoint.getBlockZ());

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
                    if (arg.solidOnly) {
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
                    for (Player rp : remaining) {
                        for (Location[] l : split) {
                            HighlightBlocks blocks = SparrowNMSProxy.getInstance().highlightBlocks(rp, arg.color, l);
                            SparrowBukkitPlugin.getInstance().getBootstrap().getScheduler().asyncLater(() -> {
                                if (rp.isOnline())
                                    blocks.destroy(rp);
                            }, arg.duration, TimeUnit.SECONDS);
                        }
                    }
                });
            }
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

        @EventHandler (ignoreCancelled = true)
        private void onPlayerQuit(PlayerQuitEvent event) {
            final UUID uuid = event.getPlayer().getUniqueId();
            argMap.remove(uuid);
        }
    }
}
