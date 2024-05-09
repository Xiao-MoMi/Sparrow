package net.momirealms.sparrow.bukkit.feature.skull;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.fetcher.OnlinePlayerFetcherSetter;
import net.momirealms.sparrow.common.feature.skull.AbstractSkullManager;
import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.FetcherSetter;

public final class SparrowBukkitSkullManager extends AbstractSkullManager {

    private final SparrowBukkitPlugin plugin;

    public SparrowBukkitSkullManager(SparrowBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected SkullFetcher setSkullFetcher(SkullArgument argument) {
        return FetcherSetter.chain(
                new OnlinePlayerFetcherSetter(plugin, argument),
                new FetcherSetter.Mineskin(argument),
                new FetcherSetter.API(argument)
        ).fetcher();
    }
}
