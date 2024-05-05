package net.momirealms.sparrow.common.feature.skull;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface Skull {
    @NotNull
    CompletableFuture<String> getBase64();
}
