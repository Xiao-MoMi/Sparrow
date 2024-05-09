package net.momirealms.sparrow.common.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.FetcherProvider;
import org.jetbrains.annotations.NotNull;

public interface FetcherProviderRegistry {

    static FetcherProviderRegistry create() {
        return new FetcherProviderRegistryImpl();
    }

    void register(@NotNull String key, @NotNull FetcherProvider provider);

    void unregister(@NotNull String key);

    SkullFetcher getSkullFetcher(@NotNull SkullArgument argument);

    void close();
}
