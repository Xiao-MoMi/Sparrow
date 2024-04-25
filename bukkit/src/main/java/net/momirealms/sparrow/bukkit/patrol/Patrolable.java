package net.momirealms.sparrow.bukkit.patrol;

import java.util.UUID;

public interface Patrolable {
    UUID getUniqueId();

    long getLastPatrolTime();

    void setLastPatrolTime(long time);
}
