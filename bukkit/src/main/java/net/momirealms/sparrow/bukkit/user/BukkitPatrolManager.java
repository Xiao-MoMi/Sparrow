package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.common.feature.patrol.PatrolManager;
import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import net.momirealms.sparrow.common.feature.patrol.PatrolableFilter;
import net.momirealms.sparrow.common.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.TreeSet;

public class BukkitPatrolManager implements PatrolManager {
    private static BukkitPatrolManager instance;
    private final UserManager<BukkitUser> userManager = BukkitUserManager.getInstance();

    private BukkitPatrolManager() {}

    public static BukkitPatrolManager getInstance() {
        if (instance == null) {
            instance = new BukkitPatrolManager();
        }
        return instance;
    }

    private final TreeSet<Patrolable> patrolUser = new TreeSet<>(Comparator.comparing(Patrolable::getLastPatrolTime));

    {
        initPatrolable();
    }

    @Override
    public @NotNull Patrolable getNextPatrolable(@NotNull PatrolableFilter filter) {
        Patrolable target = filter.filter(patrolUser).first();
        target.setLastPatrolTime(System.currentTimeMillis());

        return target;
    }

    private void initPatrolable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitUser user = userManager.getUser(player.getUniqueId());
            patrolUser.add(user);
        }
    }

    public void addPatrolable(@NotNull Patrolable patrolable) {
        patrolUser.add(patrolable);
    }

    public void removePatrolable(@NotNull Patrolable patrolable) {
        patrolUser.remove(patrolable);
    }
}
