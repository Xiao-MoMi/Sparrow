package net.momirealms.sparrow.bukkit.user;

import net.momirealms.sparrow.bukkit.patrol.Patrolable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BukkitUser implements User<Player>, Patrolable {
    private final UUID uniqueId;
    private long lastPatrolTime;

    BukkitUser(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    @Override
    public boolean isOnline() {
        return getPlayer() != null;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return uniqueId;
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
