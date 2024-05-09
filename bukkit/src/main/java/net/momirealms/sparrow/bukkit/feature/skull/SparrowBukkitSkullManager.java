package net.momirealms.sparrow.bukkit.feature.skull;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.fetcher.OnlinePlayerFetcherProvider;
import net.momirealms.sparrow.common.feature.skull.AbstractSkullManager;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.APIFetcherProvider;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.FetcherProvider;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.MineSkinFetcherProvider;
import net.momirealms.sparrow.common.util.Pair;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public final class SparrowBukkitSkullManager extends AbstractSkullManager {

    private final SparrowBukkitPlugin plugin;

    public SparrowBukkitSkullManager(SparrowBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected List<Pair<String, FetcherProvider>> getProviders() {
        ArrayList<Pair<String, FetcherProvider>> providers = new ArrayList<>();
        if (Bukkit.getServer().getOnlineMode()) {
            providers.add(Pair.of("online", new OnlinePlayerFetcherProvider()));
        }
        providers.add(Pair.of("mineskin", new MineSkinFetcherProvider()));
        providers.add(Pair.of("api", new APIFetcherProvider()));
        return providers;
    }
}
