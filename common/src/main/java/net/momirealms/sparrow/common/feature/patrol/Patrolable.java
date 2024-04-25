package net.momirealms.sparrow.common.feature.patrol;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Patrolable extends Comparable<Patrolable> {

    UUID getUniqueId();

    long getLastPatrolTime();

    void setLastPatrolTime(long time);

    @Override
    default int compareTo(@NotNull Patrolable o) {
        if (getLastPatrolTime() < o.getLastPatrolTime()) {
            return -1;
        } else if (getLastPatrolTime() > o.getLastPatrolTime()) {
            return 1;
        }
        return Integer.compare(getUniqueId().hashCode(), o.getUniqueId().hashCode());
    }
}
