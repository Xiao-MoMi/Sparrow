package net.momirealms.sparrow.common.feature.patrol;

import org.jetbrains.annotations.NotNull;

public interface PatrolManager {
    @NotNull Patrolable getNextPatrolable(@NotNull PatrolableFilter filter);
}
