package net.momirealms.sparrow.common.user;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a user.
 * @param <T> The type of the user.
 */
public interface User<T> {

    T getPlayer();

    boolean isOnline();

    @NotNull
    UUID getUniqueId();
}
