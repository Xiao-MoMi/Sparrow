package net.momirealms.sparrow.bukkit.feature.skull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.util.PluginUtils;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.momirealms.sparrow.common.feature.skull.SkullManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class BukkitSkullManager implements SkullManager {
    private final LoadingCache<String, Skull> skullCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(CacheLoader.from(this::getSkullInternal));

    private BukkitSkullManager() {
        Bukkit.getPluginManager().registerEvents(new SkullListener(), SparrowBukkitPlugin.getInstance().getLoader());
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

    @Override
    public void removeSkull(@NotNull String name) {
        skullCache.invalidate(name);
    }

    private @NotNull Skull getSkullInternal(String name) {
        if (PluginUtils.isPluginEnabled("SkinRestorer"))
            return new SkinRestorerSkull(name);
        return new UserSkull(name);
    }

    private static class SkullListener implements Listener {
        @EventHandler
        private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
            BukkitSkullManager.getInstance().removeSkull(event.getPlayer().getName());
        }
    }
}
