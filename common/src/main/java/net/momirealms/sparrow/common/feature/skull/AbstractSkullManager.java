package net.momirealms.sparrow.common.feature.skull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.momirealms.sparrow.common.plugin.SparrowPlugin;
import net.momirealms.sparrow.common.util.Either;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSkullManager implements SkullManager {

    private final Cache<Either<String, UUID>, SkullFetcher> userCache;
    private SparrowPlugin plugin;

    public AbstractSkullManager(SparrowPlugin plugin) {
        this.userCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        this.plugin = plugin;
    }

    public void load() {
    }

    public void unload() {
    }

    protected abstract SkullFetcher setSkullFetcher(Either<String, UUID> nameOrUUID);

    @NotNull
    @Override
    public CompletableFuture<SkullData> getSkull(@NotNull Either<String, UUID> nameOrUUID) {
        return userCache.get(nameOrUUID, this::setSkullFetcher).fetchData();
    }
}
