package net.momirealms.sparrow.common.feature.skull;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface SkullManager {

    @NotNull
    CompletableFuture<SkullData> getSkull(@NotNull SkullArgument argument);
}
