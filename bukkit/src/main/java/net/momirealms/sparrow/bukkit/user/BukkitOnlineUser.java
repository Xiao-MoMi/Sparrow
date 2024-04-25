package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.common.feature.patrol.Patrolable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitOnlineUser extends BukkitOfflineUser implements Patrolable {

    private long lastPatrolTime;

    BukkitOnlineUser(UUID uniqueId) {
        super(uniqueId);
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @Override
    public long getLastPatrolTime() {
        return lastPatrolTime;
    }

    @Override
    public void setLastPatrolTime(long lastPatrolTime) {
        this.lastPatrolTime = lastPatrolTime;
    }
}
