package net.momirealms.sparrow.common.feature.patrol;

import java.util.UUID;

public interface Patrolable {
    UUID getUniqueId();

    long getLastPatrolTime();

    void setLastPatrolTime(long time);
}
