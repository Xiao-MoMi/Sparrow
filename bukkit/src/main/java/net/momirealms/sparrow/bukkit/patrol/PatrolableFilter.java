package net.momirealms.sparrow.bukkit.patrol;

import java.util.Collection;
import java.util.function.Predicate;

public interface PatrolableFilter extends Predicate<Patrolable> {
    default <T extends Collection<? extends Patrolable>> T filter(T collection) {
        collection.removeIf(this);
        return collection;
    }
}
