package net.momirealms.sparrow.common.feature.patrol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface PatrolManager {

    @Nullable
    Patrolable selectNextPatrolable(@NotNull Function<Patrolable, Boolean> filter);

    void finishPatrol(Patrolable patrolable);

    void addPatrolable(@NotNull Patrolable patrolable);

    boolean removePatrolable(@NotNull Patrolable patrolable);
}
