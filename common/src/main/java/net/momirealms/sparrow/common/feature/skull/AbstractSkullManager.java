package net.momirealms.sparrow.common.feature.skull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.momirealms.sparrow.common.feature.skull.argument.NameSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.UUIDSkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.FetcherProviderRegistry;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.FetcherProvider;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.util.Either;
import net.momirealms.sparrow.common.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSkullManager implements SkullManager {

    private final Cache<SkullArgument, SkullFetcher> userCache;
    private SparrowPlugin plugin;
    private final FetcherProviderRegistry providerRegistry;

    public AbstractSkullManager(SparrowPlugin plugin) {
        this.userCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
        this.plugin = plugin;
        this.providerRegistry = FetcherProviderRegistry.create();
        this.load();
    }

    private void load() {
        for (Pair<String, FetcherProvider> pair : getProviders()) {
            this.getProviderRegistry().register(pair.left(), pair.right());
        }
    }

    @Override
    public void disable() {
        this.getProviderRegistry().close();
    }

    protected abstract List<Pair<String, FetcherProvider>> getProviders();

    private SkullFetcher setSkullFetcher(SkullArgument argument) {
        return providerRegistry.getSkullFetcher(argument);
    }

    @Override
    public CompletableFuture<SkullData> getSkull(@NotNull Either<String, UUID> either) {
        return either.mapEither(
                name -> getSkull(new NameSkullArgument(name)),
                uuid -> getSkull(new UUIDSkullArgument(uuid))
        );
    }

    @NotNull
    @Override
    public CompletableFuture<SkullData> getSkull(@NotNull SkullArgument argument) {
        return userCache.get(argument, this::setSkullFetcher).fetchData();
    }

    @Override
    public FetcherProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }
}
