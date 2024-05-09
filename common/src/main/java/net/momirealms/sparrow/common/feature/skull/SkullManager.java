package net.momirealms.sparrow.common.feature.skull;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.FetcherProviderRegistry;
import net.momirealms.sparrow.common.util.Either;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SkullManager {

    CompletableFuture<SkullData> getSkull(@NotNull Either<String, UUID> either);

    CompletableFuture<SkullData> getSkull(@NotNull SkullArgument argument);

    FetcherProviderRegistry getProviderRegistry();

    void disable();
}
