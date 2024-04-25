package net.momirealms.sparrow.bukkit.feature.patrol;

import net.momirealms.sparrow.bukkit.user.BukkitOnlineUser;
import net.momirealms.sparrow.bukkit.user.BukkitUserManager;
import net.momirealms.sparrow.common.feature.patrol.PatrolManager;
import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import net.momirealms.sparrow.common.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TreeSet;
import java.util.function.Function;

public class BukkitPatrolManager implements PatrolManager {
    private static BukkitPatrolManager instance;
    private final UserManager<BukkitOnlineUser> userManager = BukkitUserManager.getInstance();
    private final TreeSet<Patrolable> patrolableUsers = new TreeSet<>();

    private BukkitPatrolManager() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitOnlineUser user = userManager.getUser(player.getUniqueId());
            addPatrolable(user);
        }
    }

    public static BukkitPatrolManager getInstance() {
        if (instance == null) {
            instance = new BukkitPatrolManager();
        }
        return instance;
    }

    @Override
    public @Nullable Patrolable selectNextPatrolable(@NotNull Function<Patrolable, Boolean> filter) {
        for (Patrolable patrolable : patrolableUsers) {
            if (filter.apply(patrolable)) {
                return patrolable;
            }
        }
        return null;
    }

    @Override
    public void finishPatrol(Patrolable patrolable) {
        removePatrolable(patrolable);
        patrolable.setLastPatrolTime(System.currentTimeMillis());
        addPatrolable(patrolable);
    }

    @Override
    public void addPatrolable(@NotNull Patrolable patrolable) {
        if (this.patrolableUsers.contains(patrolable)) {
            return;
        }
        this.patrolableUsers.add(patrolable);
    }

    @Override
    public boolean removePatrolable(@NotNull Patrolable patrolable) {
        return this.patrolableUsers.remove(patrolable);
    }
}
