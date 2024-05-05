package net.momirealms.sparrow.bukkit.feature.skull;

import net.momirealms.sparrow.common.feature.skull.FailedToGetSkullException;
import net.momirealms.sparrow.common.feature.skull.Skull;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class SkinRestorerSkull implements Skull {
    private final String skinOrUniqueId;
    private final SkinsRestorer skinsRestorer;

    public SkinRestorerSkull(String skinOrUniqueId) {
        this.skinOrUniqueId = skinOrUniqueId;
        this.skinsRestorer = SkinsRestorerProvider.get();
    }

    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        try {
            Optional<MojangSkinDataResult> skin = skinsRestorer.getSkinStorage().getPlayerSkin(skinOrUniqueId, true);
            return skin.map(mojangSkinDataResult -> CompletableFuture.completedFuture(mojangSkinDataResult.getSkinProperty().getValue()))
                    .orElseGet(() -> CompletableFuture.failedFuture(new FailedToGetSkullException(skinOrUniqueId)));
        } catch (DataRequestException e) {
            return CompletableFuture.failedFuture(new FailedToGetSkullException(skinOrUniqueId, e));
        }
    }
}
