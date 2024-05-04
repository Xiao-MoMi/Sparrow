package net.momirealms.sparrow.bukkit.feature.skull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.common.feature.skull.SkullManager;
import org.jetbrains.annotations.NotNull;

public final class BukkitSkullManager implements SkullManager {
    private final LoadingCache<String, Skull> skullCache = CacheBuilder.newBuilder()
            .build(CacheLoader.from(name -> {
                // if (SkinRestorer.isSkinRestorerEnabled()) return new SkinRestorerSkull(name);
                return new UserSkull(name);
            }));

    private BukkitSkullManager() {
    }

    private static class SingletonHolder {
        private static final BukkitSkullManager INSTANCE = new BukkitSkullManager();
    }

    public static BukkitSkullManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @NotNull
    @Override
    public Skull getSkull(@NotNull String name) {
        return skullCache.getUnchecked(name);
    }
}
