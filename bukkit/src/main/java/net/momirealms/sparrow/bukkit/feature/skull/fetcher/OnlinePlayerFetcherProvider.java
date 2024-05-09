package net.momirealms.sparrow.bukkit.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.argument.NameSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.UUIDSkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.fetcher.provider.FetcherProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class OnlinePlayerFetcherProvider implements FetcherProvider {

    @Override
    public Optional<SkullFetcher> getOrEmpty(SkullArgument skullArgument) {
        Player player = null;
        if (skullArgument instanceof NameSkullArgument nameSkullArgument) {
            player = Bukkit.getPlayer(nameSkullArgument.name());
        } else if (skullArgument instanceof UUIDSkullArgument uuidSkullArgument) {
            player = Bukkit.getPlayer(uuidSkullArgument.uniqueId());
        }
        if (player == null || !player.isOnline()) {
            return Optional.empty();
        }
        return Optional.of(new OnlinePlayerFetcher(player));
    }
}
