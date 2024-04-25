package net.momirealms.sparrow.bukkit.patrol;

import net.momirealms.sparrow.bukkit.user.BukkitUser;
import net.momirealms.sparrow.bukkit.user.BukkitUserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.TreeSet;

// TODO: This class is not complete, it is just a placeholder for the actual implementation
public class PatrolManager {
    private static PatrolManager instance;
    private final BukkitUserManager userManager = BukkitUserManager.getInstance();

    private PatrolManager() {}

    public static PatrolManager getInstance() {
        if (instance == null) {
            instance = new PatrolManager();
        }
        return instance;
    }

    private final TreeSet<Patrolable> patrolUser = new TreeSet<>(Comparator.comparing(Patrolable::getLastPatrolTime));

    {
        initPatrolable();
    }

    public @NotNull Patrolable getNextPatrolable(@NotNull PatrolableFilter filter) {
        Patrolable target = filter.filter(patrolUser).first();
        target.setLastPatrolTime(System.currentTimeMillis());

        return target;
    }

    void initPatrolable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitUser user = userManager.getUser(player.getUniqueId());
            patrolUser.add(user);
        }
    }

    void addPatrolable(@NotNull Patrolable patrolable) {
        patrolUser.add(patrolable);
    }

    void removePatrolable(@NotNull Patrolable patrolable) {
        patrolUser.remove(patrolable);
    }
}
