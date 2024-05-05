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

public class SkinRestorerSkull implements Skull {
    private final String name;
    private final SkinsRestorer skinsRestorer;

    public SkinRestorerSkull(String name) {
        this.name = name;
        this.skinsRestorer = SkinsRestorerProvider.get();
    }

    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        try {
            Optional<MojangSkinDataResult> skin = skinsRestorer.getSkinStorage().getPlayerSkin(name, true);
            return skin.map(mojangSkinDataResult -> CompletableFuture.completedFuture(mojangSkinDataResult.getSkinProperty().getValue()))
                    .orElseGet(() -> CompletableFuture.failedFuture(new FailedToGetSkullException(name)));
        } catch (DataRequestException e) {
            return CompletableFuture.failedFuture(new FailedToGetSkullException(name, e));
        }
    }
}
