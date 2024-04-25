package net.momirealms.sparrow.bukkit.command.feature;

import net.kyori.adventure.text.Component;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.command.AbstractCommand;
import net.momirealms.sparrow.bukkit.feature.patrol.BukkitPatrolManager;
import net.momirealms.sparrow.bukkit.user.BukkitOnlineUser;
import net.momirealms.sparrow.bukkit.user.BukkitUserManager;
import net.momirealms.sparrow.common.feature.patrol.PatrolManager;
import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.incendo.cloud.Command;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.data.MultiplePlayerSelector;
import org.incendo.cloud.bukkit.parser.selector.MultiplePlayerSelectorParser;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class PatrolAdminCommand extends AbstractCommand {

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
    public Command.Builder<? extends CommandSender> assembleCommand(BukkitCommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .senderType(Player.class)
                .required("players", MultiplePlayerSelectorParser.multiplePlayerSelectorParser())
                .flag(manager.flagBuilder("silent").withAliases("s"))
                .handler(commandContext -> {
                    MultiplePlayerSelector selector = commandContext.get("players");
                    final Player patrollingPlayer = commandContext.sender();
                    final HashSet<UUID> playersToCheck = new HashSet<>(
                            selector.values().stream()
                                    .filter(player -> !player.hasPermission(BYPASS) && player != patrollingPlayer)
                                    .map(Entity::getUniqueId)
                                    .toList()
                    );
                    if (playersToCheck.isEmpty()) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(patrollingPlayer)
                                        .sendMessage(Component.text("No player found"));
                        return;
                    }

                    PatrolManager patrolManager = BukkitPatrolManager.getInstance();
                    @Nullable Patrolable target = patrolManager.selectNextPatrolable(patrolable -> playersToCheck.contains(patrolable.getUniqueId()));
                    if (target == null) {
                        SparrowBukkitPlugin.getInstance().getSenderFactory().wrap(patrollingPlayer)
                                .sendMessage(Component.text("No player found"));
                        return;
                    }

                    if (!(target instanceof BukkitOnlineUser targetUser)) {
                        throw new RuntimeException("The player to be patrolled is not an online user");
                    }

                    patrolManager.finishPatrol(target);
                    patrollingPlayer.teleport(Objects.requireNonNull(targetUser.getPlayer()));
                });
    }

    public static class PatrolListener implements Listener {

        private final BukkitUserManager userManager = BukkitUserManager.getInstance();
        private final BukkitPatrolManager patrolManager = BukkitPatrolManager.getInstance();

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
