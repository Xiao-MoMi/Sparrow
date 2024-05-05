package net.momirealms.sparrow.bukkit.feature.skull;

import net.momirealms.sparrow.bukkit.SparrowBukkitPlugin;
import net.momirealms.sparrow.bukkit.feature.skull.fetcher.OnlinePlayerFetcher;
import net.momirealms.sparrow.common.feature.skull.AbstractSkullManager;
import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import net.momirealms.sparrow.common.feature.skull.fetcher.APIFetcher;
import net.momirealms.sparrow.common.util.Either;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class SparrowBukkitSkullManager extends AbstractSkullManager {

    private final boolean isOnlineModeServer;

    public SparrowBukkitSkullManager(SparrowBukkitPlugin plugin) {
        super(plugin);
        this.isOnlineModeServer = plugin.getBootstrap().getLoader().getServer().getOnlineMode();
    }

    @Override
    protected SkullFetcher setSkullFetcher(Either<String, UUID> nameOrUUID) {
        if (isOnlineModeServer) {
            Player player = null;
            if (nameOrUUID.primary().isPresent()) {
                player = Bukkit.getPlayer(nameOrUUID.primary().get());
            } else {
                if (nameOrUUID.fallback().isPresent()) {
                    player = Bukkit.getPlayer(nameOrUUID.fallback().get());
                }
            }
            if (player != null && player.isOnline()) {
                return new OnlinePlayerFetcher(player);
            }
        }
        return new APIFetcher(nameOrUUID);
    }
}
