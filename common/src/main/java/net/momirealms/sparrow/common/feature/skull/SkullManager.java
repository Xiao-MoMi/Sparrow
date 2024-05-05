package net.momirealms.sparrow.common.feature.skull;

import net.momirealms.sparrow.common.util.Either;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkullManager {

    @NotNull
    CompletableFuture<SkullData> getSkull(@NotNull Either<String, UUID> nameOrUUID);
}
