package net.momirealms.sparrow.bukkit.feature.skull;

import net.momirealms.sparrow.common.feature.skull.Skull;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkinRestorerSkull implements Skull {
    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
