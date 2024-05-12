package net.momirealms.sparrow.bukkit.command.feature;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.MessagingCommandFeature;
import net.momirealms.sparrow.bukkit.feature.patrol.SparrowBukkitPatrolManager;
import net.momirealms.sparrow.bukkit.user.BukkitOnlineUser;
import net.momirealms.sparrow.bukkit.user.BukkitUserManager;
import net.momirealms.sparrow.common.command.key.SparrowArgumentKeys;
import net.momirealms.sparrow.common.command.key.SparrowFlagKeys;
import net.momirealms.sparrow.common.feature.patrol.PatrolManager;
import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import net.momirealms.sparrow.common.locale.MessageConstants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PatrolAdminCommand extends MessagingCommandFeature<CommandSender> {

    private static final String BYPASS = "sparrow.bypass.patrol";
    private final PatrolListener listener;

    public PatrolAdminCommand() {
        this.listener = new PatrolListener();
    }

    @Override
    public void registerRelatedFunctions() {
        Bukkit.getPluginManager().registerEvents(listener, SparrowBukkitPlugin.getInstance().getLoader());
    }

    @Override
    public void unregisterRelatedFunctions() {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public String getFeatureID() {
        return "patrol_admin";
    }

    @Override
    public Command.Builder<? extends CommandSender> assembleCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .required("players", patrolMultiplePlayerSelectorParser())
                .flag(SparrowFlagKeys.SILENT_FLAG)
                .senderType(Player.class)
                .handler(commandContext -> {
                    final HashSet<Player> players = commandContext.get("players");
                    final Player patrollingPlayer = commandContext.sender();
                    final List<UUID> playersToCheck = players.stream().map(Player::getUniqueId).toList();

                    PatrolManager patrolManager = SparrowBukkitPatrolManager.getInstance();
                    @Nullable Patrolable target = patrolManager.selectNextPatrolable(patrolable -> playersToCheck.contains(patrolable.getUniqueId()));
                    if (target == null) {
                        commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_PATROL_FAILED_TARGET);
                        return;
                    }

                    if (!(target instanceof BukkitOnlineUser targetUser)) {
                        throw new RuntimeException("The player to be patrolled is not an online user");
                    }

                    patrolManager.finishPatrol(target);
                    patrollingPlayer.teleport(Objects.requireNonNull(targetUser.getPlayer()));
                    commandContext.store(SparrowArgumentKeys.MESSAGE, MessageConstants.COMMANDS_ADMIN_PATROL_SUCCESS);
                    commandContext.store(SparrowArgumentKeys.MESSAGE_ARGS, List.of(Component.text(targetUser.getPlayer().getName())));
                });
    }

    @NotNull
    private ParserDescriptor<Object, HashSet<Player>> patrolMultiplePlayerSelectorParser() {
        return MultiplePlayerSelectorParser.multiplePlayerSelectorParser(false)
                .flatMapSuccess(
                        new TypeToken<>() {
                        },
                        (commandContext, multiplePlayerSelector) -> {
                            final Object patrollingPlayer = commandContext.sender();
                            final HashSet<Player> players = new HashSet<>(
                                    multiplePlayerSelector.values().stream()
                                            .filter(player -> !player.hasPermission(BYPASS) && player != patrollingPlayer)
                                            .toList()
                            );
                            if (players.isEmpty()) {
                                return ArgumentParseResult.failureFuture(new IllegalArgumentException("No players to patrol"));
                            }
                            return ArgumentParseResult.successFuture(players);
                        });
    }

    public static class PatrolListener implements Listener {

        private final BukkitUserManager userManager = BukkitUserManager.getInstance();
        private final SparrowBukkitPatrolManager patrolManager = SparrowBukkitPatrolManager.getInstance();

        @EventHandler
        private void onPlayerJoin(PlayerJoinEvent event) {
            BukkitOnlineUser user = userManager.getUser(event.getPlayer().getUniqueId());

            patrolManager.addPatrolable(user);
        }

        @EventHandler
        private void onPlayerQuit(PlayerQuitEvent event) {
            BukkitOnlineUser user = userManager.getUser(event.getPlayer().getUniqueId());

            patrolManager.removePatrolable(user);
        }
    }
}
