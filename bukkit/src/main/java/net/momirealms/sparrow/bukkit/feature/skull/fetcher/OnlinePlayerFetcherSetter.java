package net.momirealms.sparrow.bukkit.feature.skull.fetcher;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.argument.NameSkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.SkullArgument;
import net.momirealms.sparrow.common.feature.skull.argument.UUIDSkullArgument;
import net.momirealms.sparrow.common.feature.skull.fetcher.FetcherSetter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OnlinePlayerFetcherSetter extends FetcherSetter {
    private final boolean isOnlineModeServer;

    public OnlinePlayerFetcherSetter(@NotNull SparrowBukkitPlugin plugin, SkullArgument argument) {
        super(argument);
        this.isOnlineModeServer = plugin.getBootstrap().getLoader().getServer().getOnlineMode();
    }

    @NotNull
    @Override
    protected SkullFetcher getFetcher() throws RuntimeException {
        if (!isOnlineModeServer) {
            throw new UnsupportedOperationException("Online player fetcher is not supported in offline mode server.");
        }

        Player player;
        if (argument instanceof NameSkullArgument nameSkullArgument) {
            player = Bukkit.getPlayer(nameSkullArgument.name());
        } else if (argument instanceof UUIDSkullArgument uuidSkullArgument) {
            player = Bukkit.getPlayer(uuidSkullArgument.uniqueId());
        } else {
            throw new IllegalArgumentException("Invalid skull argument" + argument.toString());
        }

        if (player == null) {
            throw new IllegalArgumentException("Invalid skull argument" + argument.toString());
        }

        return new OnlinePlayerFetcher(player);
    }

}
