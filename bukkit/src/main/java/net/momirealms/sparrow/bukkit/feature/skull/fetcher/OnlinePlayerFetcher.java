package net.momirealms.sparrow.bukkit.feature.skull.fetcher;

import net.momirealms.sparrow.common.feature.skull.SkullData;
import net.momirealms.sparrow.common.feature.skull.SkullFetcher;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class OnlinePlayerFetcher implements SkullFetcher {

    private final SkullData skullData;

    public OnlinePlayerFetcher(Player player) {
        this.skullData = player.getPlayerProfile()
                .getProperties()
                .stream()
                .filter(profileProperty -> profileProperty.getName().equals("textures"))
                .findAny()
                .map(profileProperty -> SkullData.builder().owner(player.getName()).uuid(player.getUniqueId()).textureBase64(profileProperty.getValue()).build())
                .orElse(null);
    }

    @Override
    public CompletableFuture<SkullData> fetchData() {
        return CompletableFuture.completedFuture(skullData);
    }
}
