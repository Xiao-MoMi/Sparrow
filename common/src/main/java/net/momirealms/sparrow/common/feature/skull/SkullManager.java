package net.momirealms.sparrow.common.feature.skull;

import org.jetbrains.annotations.NotNull;

public interface SkullManager {
    @NotNull
    Skull getSkull(@NotNull String name);

    void removeSkull(@NotNull String name);
}
