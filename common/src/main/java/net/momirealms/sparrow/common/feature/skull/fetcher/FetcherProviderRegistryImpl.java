package net.momirealms.sparrow.common.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.FetcherProvider;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Optional;

public class FetcherProviderRegistryImpl implements FetcherProviderRegistry {

    private final LinkedHashMap<String, FetcherProvider> registeredProviders = new LinkedHashMap<>();

    @Override
    public void register(@NotNull String key, @NotNull FetcherProvider provider) {
        this.registeredProviders.put(key, provider);
    }

    @Override
    public void unregister(@NotNull String key) {
        this.registeredProviders.remove(key);
    }

    @Override
    public void close() {
        this.registeredProviders.clear();
    }

    @Override
    public SkullFetcher getSkullFetcher(@NotNull SkullArgument argument) {
        for (FetcherProvider provider : this.registeredProviders.values()) {
            Optional<SkullFetcher> fetcher = provider.getOrEmpty(argument);
            if (fetcher.isPresent()) {
                return fetcher.get();
            }
        }
        throw new IllegalStateException("No SkullFetcher found for argument: " + argument);
    }
}
