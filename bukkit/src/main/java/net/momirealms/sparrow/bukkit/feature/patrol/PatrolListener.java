package net.momirealms.sparrow.bukkit.feature.patrol;

import net.momirealms.sparrow.bukkit.user.BukkitPatrolManager;
import net.momirealms.sparrow.bukkit.user.BukkitUser;
import net.momirealms.sparrow.bukkit.user.BukkitUserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PatrolListener implements Listener {
    private final BukkitUserManager userManager = BukkitUserManager.getInstance();
    private final BukkitPatrolManager patrolManager = BukkitPatrolManager.getInstance();

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        BukkitUser user = userManager.getUser(event.getPlayer().getUniqueId());

        patrolManager.addPatrolable(user);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        BukkitUser user = userManager.getUser(event.getPlayer().getUniqueId());

        patrolManager.removePatrolable(user);
    }
}
