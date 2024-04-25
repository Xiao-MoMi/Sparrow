package net.momirealms.sparrow.common.feature.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents a user.
 * @param <T> The type of the user.
 */
public interface User<T> {
    @Nullable
    T getPlayer();

    boolean isOnline();

    @NotNull
    UUID getUniqueId();
}
