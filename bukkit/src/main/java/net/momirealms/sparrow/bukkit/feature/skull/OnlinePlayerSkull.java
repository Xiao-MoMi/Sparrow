package net.momirealms.sparrow.bukkit.feature.skull;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.momirealms.sparrow.common.feature.skull.FailedToGetSkullException;
import net.momirealms.sparrow.common.feature.skull.Skull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class OnlinePlayerSkull implements Skull {
    private final CompletableFuture<String> base64;

    OnlinePlayerSkull(@NotNull PlayerProfile profile) {
        this.base64 = profile.getProperties().stream()
                .filter(property -> property.getName().equals("textures"))
                .findFirst()
                .map(property -> CompletableFuture.completedFuture(property.getValue()))
                .orElseGet(() -> CompletableFuture.failedFuture(new FailedToGetSkullException(profile.getName())));
    }

    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        return base64;
    }
}
